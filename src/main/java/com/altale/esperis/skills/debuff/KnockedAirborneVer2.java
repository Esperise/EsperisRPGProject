package com.altale.esperis.skills.debuff;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
    static class Duration{
        static int duration;
        Duration(int duration){
            this.duration = duration;
        }
    }
    private static final Map<LivingEntity, Integer> airborneMap = new HashMap<>();
    private static final Map<LivingEntity, Integer> delayedAirborneMap = new HashMap<>();


    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // 1. 지연된 공중 고정 처리
            Iterator<Map.Entry<LivingEntity, Integer>> delayIter = delayedAirborneMap.entrySet().iterator();
            while (delayIter.hasNext()) {
                Map.Entry<LivingEntity, Integer> entry = delayIter.next();
                LivingEntity entity = entry.getKey();
                int ticksLeft = entry.getValue() - 1;
                // airborneMap 안에서 처리
                if (!entity.isAlive()) {
                    delayIter.remove();
                    continue;
                }

                if (ticksLeft <= 0) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, Duration.duration,4));
                    entity.setNoGravity(true);
                    entity.setVelocity(Vec3d.ZERO);
                    airborneMap.put(entity, Duration.duration); // 고정 20틱 등록
                    delayIter.remove();
                } else {
                    delayedAirborneMap.put(entity, ticksLeft);
                }
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

    public static void giveKnockedAirborneVer2(Entity entity, ServerPlayerEntity player, int duration) {
        if (!(entity instanceof LivingEntity living)) return;

        // 1. 위로 띄우기만 하고 고정은 지연시킴
        living.setVelocity(new Vec3d(0, 1.7, 0));
        living.velocityModified = true;

        // 0.5초 후 고정
        delayedAirborneMap.put(living, 3);
        Duration airBorneDuration = new Duration(duration);
    }
}


