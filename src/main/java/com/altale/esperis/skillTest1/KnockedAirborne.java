package com.altale.esperis.skillTest1;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.lwjgl.system.linux.XGenericEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Math.max;

public class KnockedAirborne {
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
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 50,5));
                    entity.setNoGravity(true);
                    entity.setVelocity(Vec3d.ZERO);
                    airborneMap.put(entity, 50); // 고정 20틱 등록
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
                    entity.timeUntilRegen=8;
                    entity.hurtTime=8;
                    float entityHealth = entity.getMaxHealth();
                    entity.damage(entity.getRecentDamageSource(),max(0.15f,entityHealth/500));

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
                if (entity.getWorld() instanceof ServerWorld serverWorld) {
                    Vec3d pos = entity.getPos();
                    serverWorld.spawnParticles(
                        ParticleTypes.CRIT,
                        pos.x, pos.y, pos.z,
                        2,
                        0.8, 1.0, 0.8,
                        0.1
                    );
                    serverWorld.spawnParticles(
                        ParticleTypes.FALLING_DRIPSTONE_LAVA,
                        pos.x, pos.y, pos.z,
                        5,
                        0.5, 0.8, 0.5,
                        0.3
                    );
                    serverWorld.spawnParticles(
                            new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f),0.5f),
                        pos.x, pos.y, pos.z,
                        20,
                        0.6, 0.8, 0.6,
                        0.1
                    );
                    entity.getWorld().playSound(
                            null,
                            entity.getX(),
                            entity.getY(),
                            entity.getZ(),
                            SoundEvents.BLOCK_GLASS_BREAK,
                            SoundCategory.PLAYERS,
                            1.4f,
                            1.0f

                    );
                }

                if (ticksLeft <= 0) {
                    entity.setNoGravity(false);
                    float entityMaxHealth = entity.getMaxHealth();
                    float lostHealth = entityMaxHealth -entity.getHealth();
                    float entityLossHealthCoefficient = (float) ((lostHealth / entityMaxHealth)*1.5 +1);
//                    System.out.println(max(8.0f *entityLossHealthCoefficient,(entityMaxHealth*entityLossHealthCoefficient/10)));
                    entity.damage(entity.getRecentDamageSource(),max(8.0f *entityLossHealthCoefficient,(entityMaxHealth*entityLossHealthCoefficient/10)));
                    if (entity.getWorld() instanceof ServerWorld serverWorld) {
                        Vec3d pos = entity.getPos();
                        serverWorld.spawnParticles(
                                ParticleTypes.FALLING_DRIPSTONE_LAVA,
                                pos.x, pos.y, pos.z,
                                (int)(12*((lostHealth / entityMaxHealth)*30 +1)),
                                0.7, 0.75, 0.7,
                                0.1
                        );
                        serverWorld.spawnParticles(
                                new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f),0.5f),
                                pos.x, pos.y, pos.z,
                                (int)(150*((lostHealth / entityMaxHealth)*30 +1)),
                                1.0, 0.7, 1.0,
                                0.01
                        );}

                    entity.getWorld().playSound(
                            null,
                            entity.getX(),
                            entity.getY(),
                            entity.getZ(),
                            SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST,
                            SoundCategory.PLAYERS,
                            1.4f,
                            1.0f

                    );

                    iterator.remove();
                } else {
                    airborneMap.put(entity, ticksLeft);
                }
            }
        });
    }

    public static void giveKnockedAirborne(Entity entity, ServerPlayerEntity player) {
        if (!(entity instanceof LivingEntity living)) return;

        // 1. 위로 띄우기만 하고 고정은 지연시킴
        living.setVelocity(new Vec3d(0, 0.6, 0));
        living.velocityModified = true;

        // 0.5초 후 고정
        delayedAirborneMap.put(living, 10);
    }
}

