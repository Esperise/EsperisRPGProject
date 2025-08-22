package com.altale.esperis.skills.debuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Math.max;

public class KnockedAirborneVer2 {
    public static class AirborneData{
        int duration;
        int delay;
        AirborneData(int duration, int delay){
            this.duration = duration;
            this.delay = delay;
        }
        public int getDuration(){
            return duration;
        }
        public int getDelay(){
            return delay;
        }
    }
    private static final Map<LivingEntity, Integer> airborneMap = new HashMap<>();
    private static final Map<LivingEntity, AirborneData> delayedAirborneMap = new HashMap<>();


    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // 1. 지연된 공중 고정 처리
            Iterator<Map.Entry<LivingEntity, AirborneData>> delayIter = delayedAirborneMap.entrySet().iterator();
            while (delayIter.hasNext()) {
                Map.Entry<LivingEntity, AirborneData> entry = delayIter.next();
                LivingEntity entity = entry.getKey();
                AirborneData data = entry.getValue();
                int ticksLeft = data.delay - 1;
                data.delay --;
                // airborneMap 안에서 처리
                if (!entity.isAlive()) {
                    delayIter.remove();
                    continue;
                }

                if (ticksLeft <= 0) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, data.duration,9));
                    entity.setNoGravity(true);
                    entity.setVelocity(Vec3d.ZERO);
                    airborneMap.put(entity, data.duration );
                    delayIter.remove();
                }
//                else {
//                    delayedAirborneMap.put(entity, ticksLeft);
//                }
            }

            // 2. 실제 공중 고정 유지 및 파티클
            Iterator<Map.Entry<LivingEntity, Integer>> iterator = airborneMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<LivingEntity, Integer> entry = iterator.next();
                LivingEntity entity = entry.getKey();
                int ticksLeft = entry.getValue() - 1;
                if (ticksLeft > 0) {
                    entity.setVelocity(Vec3d.ZERO);
                    entity.setVelocity(Vec3d.ZERO); // 외부 넉백 무효화
                    entity.velocityModified = true;
                    entity.setNoGravity(true);
                }
                if (!entity.isAlive()) {
                    entity.setNoGravity(false);
                    iterator.remove();
                    continue;
                }

                // 파티클
                if(ticksLeft % 5 ==0){
                    if (entity.getWorld() instanceof ServerWorld serverWorld) {
                        Vec3d pos = entity.getPos();
                        serverWorld.spawnParticles(
                                ParticleTypes.CLOUD,
                                pos.x, pos.y, pos.z,
                                8,
                                0.8, 0.2, 0.8,
                                0.1
                        );
                        entity.getWorld().playSound(
                                null,
                                entity.getX(),
                                entity.getY(),
                                entity.getZ(),
                                SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS,
                                SoundCategory.PLAYERS,
                                1.0f,
                                0.6f

                        );
                    }
                }

                if (ticksLeft <= 0) {
                    entity.setNoGravity(false);
                    iterator.remove();
                } else {
                    airborneMap.put(entity, ticksLeft);
                }
            }
        });
    }

    public static void giveKnockedAirborneVer2(Entity entity, int duration, int delay) {
        if (!(entity instanceof LivingEntity living)) return;
        boolean found = false;
        if(delayedAirborneMap.containsKey(living)){
            AirborneData data = delayedAirborneMap.get(living);
            found = true;
            if(data.duration  >= duration){
                return;
            }

        }
        if(!found){
            living.setVelocity(new Vec3d(0, 1.0, 0));
            living.velocityModified = true;
        }
        living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, duration+delay,99));
        if(living instanceof PlayerEntity player){
            CoolTimeManager.ccCoolTime((ServerPlayerEntity) player, duration+delay);
        }
        AirborneData newData=new AirborneData(duration, delay);
        delayedAirborneMap.put(living, newData);
    }
    public static boolean hasKnockedAirborne(LivingEntity target) {
        return delayedAirborneMap.containsKey(target) || airborneMap.containsKey(target);
    }
    public static AirborneData getAirborne(LivingEntity target) {
        if(hasKnockedAirborne(target)){
            return delayedAirborneMap.get(target);
        }
        return new AirborneData(0,0);
    }
}


