package com.altale.esperis.skills.buff;

import com.altale.esperis.player_data.skill_data.SkillsId;
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
        int maxStack;
        int currentStack;
        BuffData(LivingEntity target, String SkillId, int duration,
                    double percentBuff, double constantBuff, int maxStack) {
            this.remainingTicks = duration;
            this.duration = duration;
            this.target = target;
            this.SkillId = SkillId;
            this.percentBuff = percentBuff;
            this.constantBuff = constantBuff;
            this.maxStack = maxStack;
            this.currentStack = 0;
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
                Iterator<Map.Entry<StatType, List<BuffData>>> innerIter = entry.getValue().entrySet().iterator();
                while (innerIter.hasNext()) {
                    Map.Entry<StatType, List<BuffData>> innerEntry = innerIter.next();
                    StatType statType = innerEntry.getKey();
                    List<BuffData> buffDataList = innerEntry.getValue();
                    Iterator<BuffData> buffListIter = buffDataList.iterator();
                    while (buffListIter.hasNext()) {
                        BuffData buffData = buffListIter.next();
                        buffData.remainingTicks--;
                        if(buffData.remainingTicks <= 0){
                            buffListIter.remove();
                            StatManager.statUpdate(player);

                        }
                        if(buffData.remainingTicks == buffData.duration-1){

                            StatManager.statUpdate(player);
                        }
                        if(innerEntry.getValue().isEmpty()){
                            innerIter.remove();
                        }
                    }
                }
            }
        });
    }
    public static void giveBuff(LivingEntity target, String SkillId,StatType statType, int duration, double percentBuff, double constantBuff, int maxStack) {
        BuffData buffData = new BuffData(target, SkillId, duration, percentBuff, constantBuff,maxStack);
        if(!buffMap.containsKey(target.getUuid())){
            buffMap.computeIfAbsent(target.getUuid(), k -> new EnumMap<>(StatType.class))
                    .computeIfAbsent(statType, k -> new ArrayList<>()).add(buffData);
        }else{
            if(buffMap.get(target.getUuid()).containsKey(statType)){
                List<BuffData> dataList = buffMap.get(target.getUuid()).get(statType);
                boolean stackTargetFound = false;
                for(BuffData data : dataList){
                    if(data.SkillId.equals(SkillId)){
                        stackTargetFound = true;
                        data.remainingTicks = duration;
                        if(data.currentStack < maxStack-1){
                            data.currentStack++;
                            data.percentBuff += percentBuff;
                            data.constantBuff += constantBuff;
                        }
                    }
                }
                if(!stackTargetFound){
                    buffMap.get(target.getUuid()).get(statType).add(buffData);
                }
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
    public static int getBuffStack(LivingEntity target, String buffName){
        UUID uuid = target.getUuid();

        if(buffMap.containsKey(uuid)){
            for(Map.Entry<StatType, List<BuffData>> innerEntry : buffMap.get(uuid).entrySet()){
                List<BuffData> buffDataList = innerEntry.getValue();
                for(BuffData buffData : buffDataList){
                    if(buffData.SkillId.equals(buffName)){
                        return buffData.currentStack+1;
                    }
                }
            }
        }
        return 0;
    }
    public static Map<String, Map<Integer, Integer>> getBufInfoForDisplay(LivingEntity target){
        Map<String, Map<Integer, Integer>> bufInfoMap = new HashMap<>();
        UUID uuid = target.getUuid();
        if(buffMap.containsKey(uuid)){
            for(Map.Entry<StatType, List<BuffData>> innerEntry : buffMap.get(uuid).entrySet()){
                List<BuffData> buffDataList = innerEntry.getValue();
                for(BuffData buffData : buffDataList){
                    Map<Integer, Integer> remainAndStacksMap = new HashMap<>();
                    remainAndStacksMap.putIfAbsent(buffData.remainingTicks, buffData.currentStack);
                    bufInfoMap.putIfAbsent(buffData.SkillId, remainAndStacksMap);
                }
            }
        }
        return bufInfoMap;//스킬이름 <스킬의 남은 시간(tick), 스킬의 현재 스택>
    }
    public static boolean hasBuff(LivingEntity target, String buffName){
        UUID uuid = target.getUuid();
        if(buffMap.containsKey(uuid)){
            for(Map.Entry<StatType, List<BuffData>> innerEntry : buffMap.get(uuid).entrySet()){
                List<BuffData> buffDataList = innerEntry.getValue();
                for(BuffData buffData : buffDataList){
                    if(buffData.SkillId.equals(buffName)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
