package com.altale.esperis.skills.buff;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.lang.ref.WeakReference;
import java.util.*;

public class HealBuff {
    public static class HealData{
        int remainingTicks;
        int duration;
        double expectedHealAmount;
        double healPerNTicks;
        int healTickDelta;
        String skillId;
        WeakReference<LivingEntity> targetRef;
        HealData(LivingEntity target , int duration, int healDelta,double healPerNTicks, double expectedHealAmount, String skillId){
            this.remainingTicks = duration;
            this.duration = duration;
            this.healPerNTicks = healPerNTicks;
            this.expectedHealAmount = expectedHealAmount;
            this.healTickDelta = healDelta;
            this.skillId = skillId;
            this.targetRef = new WeakReference<>(target);
        }
        public int getRemainingTicks(){
            return remainingTicks;
        }
        public int getDuration(){
            return duration;
        }
        public double getHealAmount(){
            return expectedHealAmount;
        }
        public int getHealTickDelta(){
            return healTickDelta;
        }
        public String getSkillId(){
            return skillId;
        }

    }
    private static final Map<UUID, List<HealData>> healMap= new HashMap<>();
    public static void register(){
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            Iterator<Map.Entry<UUID, List<HealData>>> outerIterator = healMap.entrySet().iterator();
            while (outerIterator.hasNext()) {
                Map.Entry<UUID, List<HealData>> entry = outerIterator.next();
                UUID uuid = entry.getKey();
                List<HealData> healData = entry.getValue();
                Iterator<HealData> innerIterator = healData.iterator();
                while (innerIterator.hasNext()) {
                    HealData data= innerIterator.next();
                    LivingEntity target = data.targetRef.get();
                    if(target==null){
                        innerIterator.remove();
                        continue;
                    }
                    if(!target.isAlive() || target.isRemoved()){
                        innerIterator.remove();
                        continue;
                    }
                    data.remainingTicks--;
                    if(data.remainingTicks>0 && data.remainingTicks % data.healTickDelta == 0 ){
//                        System.out.println(data.skillId + " 힐 적용: "  + data.healPerNTicks);
                        target.heal((float) Math.round(data.healPerNTicks*100)/100 );
                        if (target.getWorld() instanceof ServerWorld serverWorld) {
                            Vec3d pos = target.getPos();
                            serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                                    pos.x, pos.y, pos.z, 3, 0.5, 0.8, 0.5, 0.3);
                        }
                    }
                    if(data.remainingTicks<=0){
                        target.heal((float) data.healPerNTicks);
                        innerIterator.remove();
                    }

                }
                if(entry.getValue().isEmpty()){
                    outerIterator.remove();
                }
            }
        });
    }
    public static void giveHealBuff(LivingEntity target, int duration,int tickDelta ,double expectedHealAmount, String skillId){
        float healPerNTicks= (float) (expectedHealAmount * tickDelta)/ duration;
        HealData healData= new HealData(target, duration, tickDelta, healPerNTicks, expectedHealAmount, skillId );
        if(!healMap.isEmpty()){
            if(healMap.containsKey(target.getUuid())){
                List<HealData> healDataList = healMap.get(target.getUuid());
                if(healDataList == null){
                    healMap.computeIfAbsent(target.getUuid(), k -> new ArrayList<>()).add(healData);
                }else{
                    boolean found = false;
                    Iterator<HealData> dataIter=  healDataList.iterator();
                    while(dataIter.hasNext()){
                        HealData data= dataIter.next();
                        if(data.skillId.equals(skillId)){
                            if(data.remainingTicks <= duration){
                                data.remainingTicks = duration;
                                found = true;
                            }
                        }
                    }
                    if(!found){
                        healMap.computeIfAbsent(target.getUuid(), k -> new ArrayList<>()).add(healData);
                    }
                }
            }
        }else{
            healMap.computeIfAbsent(target.getUuid(), k -> new ArrayList<>()).add(healData);
        }
    }
    public static List<HealData> getHealData(LivingEntity target){
        if(healMap.containsKey(target.getUuid())){
            return healMap.get(target.getUuid());
        }
        return new ArrayList<>();
    }

    public static Map<String, Double> healDataForHUD(PlayerEntity player){
        List<HealData> dataList = getHealData(player);
        Map<String, Double> map = new HashMap<>();
        if(dataList == null || dataList.isEmpty()){
            return map;
        }
        for(HealData data: dataList){
            map.put(data.skillId, (data.getRemainingTicks() * data.getHealAmount())/ data.getDuration());
        }
        return map;
    }
}
