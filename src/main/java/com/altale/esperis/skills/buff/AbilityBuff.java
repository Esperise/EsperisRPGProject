package com.altale.esperis.skills.buff;

import com.altale.esperis.player_data.stat_data.StatManager;
import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class AbilityBuff {
    private static class BuffData{
        LivingEntity target;
        String SkillId;
        int remainingTicks;
        int duration;
        double percentBuff;
        double constantBuff;
        BuffData(LivingEntity target, String SkillId, int duration,
                    double percentBuff, double constantBuff) {
            this.remainingTicks = duration;
            this.duration = duration;
            this.target = target;
            this.SkillId = SkillId;
            this.percentBuff = percentBuff;
            this.constantBuff = constantBuff;
        }
    }

    private static final Map<UUID, Map<StatType, List<BuffData>>> buffMap= new HashMap<>();
    public static void register(){
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            Iterator<Map.Entry<UUID, Map<StatType, List<BuffData>>>> outerIter = buffMap.entrySet().iterator();
            while (outerIter.hasNext()) {
                Map.Entry<UUID, Map<StatType, List<BuffData>>> entry = outerIter.next();
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
                if(player == null){
                    outerIter.remove();
                    continue;
                }
                if(!player.isAlive() || player.isRemoved()){
                    //죽거나 나가거나 없어지면 모든 버프를 없애고 statUpdate
                    outerIter.remove();
                    StatManager.statUpdate(player);
                    continue;
                }
                if(entry.getValue().isEmpty()){
                    //uuid에 버프가 하나도 없을 경우 map에서 지워서 자원 아끼기
                    outerIter.remove();
                    continue;
                }
                for (Map.Entry<StatType, List<BuffData>> innerEntry : entry.getValue().entrySet()) {
                    StatType statType = innerEntry.getKey();
                    List<BuffData> buffDataList = innerEntry.getValue();
                    Iterator<BuffData> buffListIter = buffDataList.iterator();
                    while (buffListIter.hasNext()) {
                        BuffData buffData = buffListIter.next();
                        buffData.remainingTicks--;
                        if(buffData.remainingTicks <= 0){
                            buffListIter.remove();
                            StatManager.statUpdate(player);
                            System.out.println("Removed " + buffData.remainingTicks + " ticks from " + statType);
                        }
                        if(buffData.remainingTicks == buffData.duration-1){
                            System.out.println("buffData remainingTicks: " + buffData.remainingTicks);
                            StatManager.statUpdate(player);
                        }
                    }
                }
            }
        });
    }
    public static void giveBuff(LivingEntity target, String SkillId,StatType statType, int duration, double percentBuff, double constantBuff) {
        BuffData buffData = new BuffData(target, SkillId, duration, percentBuff, constantBuff);
        if(!buffMap.containsKey(target.getUuid())){
            buffMap.computeIfAbsent(target.getUuid(), k -> new EnumMap<>(StatType.class))
                    .computeIfAbsent(statType, k -> new ArrayList<>()).add(buffData);
        }else{
            if(buffMap.get(target.getUuid()).containsKey(statType)){
                buffMap.get(target.getUuid()).get(statType).add(buffData);
            }else{
                buffMap.get(target.getUuid()).computeIfAbsent(statType, k -> new ArrayList<>()).add(buffData);
            }
        }

    }

    public static Map<StatType, List<Double>> getBuffs(LivingEntity target){
        Map<StatType, List<Double>> buffs = new EnumMap<>(StatType.class);
        UUID uuid = target.getUuid();
        if(buffMap.containsKey(uuid)){
            for(Map.Entry<StatType, List<BuffData>> innerEntry : buffMap.get(uuid).entrySet()){
                StatType statType = innerEntry.getKey();
                double constantBuffValue=0;
                double percentBuffValue=0;
                List<BuffData> buffDataList = innerEntry.getValue();
                for(BuffData buffData : buffDataList){
                    constantBuffValue += buffData.constantBuff;
                    percentBuffValue += buffData.percentBuff;
                }
                buffs.put(statType, Arrays.asList(constantBuffValue, percentBuffValue));
            }
        }
        return buffs;
    }

}
