package com.altale.esperis.skills;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Math.max;

public class DotDamage {
    private static int dotDuration;
    private static float damagePerTick;

    private static final Map<LivingEntity,  HashMap<LivingEntity, Integer>> dotDamageMap= new HashMap<>();
    public static void register(){
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            Iterator<Map.Entry<LivingEntity, HashMap<LivingEntity, Integer>>> dotTargetEntityIter= dotDamageMap.entrySet().iterator();
            while (dotTargetEntityIter.hasNext()) {
                Map.Entry<LivingEntity, HashMap<LivingEntity, Integer>> outEntry= dotTargetEntityIter.next();
                LivingEntity dotDamageTargetEntity = outEntry.getKey();

                HashMap<LivingEntity, Integer> DamageSourceAndDurationMap= outEntry.getValue();
                if(DamageSourceAndDurationMap.isEmpty()){
                    dotTargetEntityIter.remove();
                    dotDamageMap.remove(dotDamageTargetEntity);
                }
                Iterator<Map.Entry<LivingEntity, Integer>> DamageSourceAndDurationIter= DamageSourceAndDurationMap.entrySet().iterator();
                while(DamageSourceAndDurationIter.hasNext()){
                    //innerEntry: <대상 entity, 지속 시간>
                    Map.Entry<LivingEntity, Integer> innerEntry= DamageSourceAndDurationIter.next();
                    int dotDamageDuration= innerEntry.getValue() - 1;
                    LivingEntity dotDamageSourceEntity= innerEntry.getKey();

                    DamageSource dotDamageSource=dotDamageTargetEntity.getWorld().getDamageSources().generic();//일단 기본 값으로 만듦
                    if(dotDamageSourceEntity instanceof PlayerEntity){
                        dotDamageSource = dotDamageTargetEntity.getWorld().getDamageSources().playerAttack((PlayerEntity) dotDamageSourceEntity);

                    }
                    else{
                        dotDamageSource = dotDamageTargetEntity.getWorld().getDamageSources().mobAttack(dotDamageSourceEntity);

                    }
                    if((dotDamageDuration> 0) && (dotDamageDuration % 4 == 0)){
                        dotDamageTargetEntity.timeUntilRegen=4;
                        dotDamageTargetEntity.hurtTime=4;
                        dotDamageTargetEntity.damage(dotDamageSource, damagePerTick);


                    }
                    if(!dotDamageTargetEntity.isAlive()){//대상 사망시 Iter 삭제
                        dotTargetEntityIter.remove();
                        continue;
                    }
                    if (dotDamageTargetEntity.getWorld() instanceof ServerWorld serverWorld) {
                        Vec3d pos = dotDamageTargetEntity.getPos();
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
                        dotDamageTargetEntity.getWorld().playSound(
                                null,
                                pos.x,
                                pos.y,
                                pos.z,
                                SoundEvents.BLOCK_GLASS_BREAK,
                                SoundCategory.PLAYERS,
                                1.4f,
                                1.0f

                        );
                    }
                    if(dotDamageDuration <=0){
                        float entityMaxHealth = dotDamageTargetEntity.getMaxHealth();
                        float lostHealth = entityMaxHealth -dotDamageTargetEntity.getHealth();
                        float entityLossHealthCoefficient = (float) ((lostHealth / entityMaxHealth)*1.5 +1);
//                    System.out.println(max(8.0f *entityLossHealthCoefficient,(entityMaxHealth*entityLossHealthCoefficient/10)));
                        dotDamageTargetEntity.damage(dotDamageSource,max(8.0f *entityLossHealthCoefficient,(entityMaxHealth*entityLossHealthCoefficient/10)));
                        if (dotDamageTargetEntity.getWorld() instanceof ServerWorld serverWorld) {
                            Vec3d pos = dotDamageTargetEntity.getPos();
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
                            );

                            dotDamageTargetEntity.getWorld().playSound(
                                    null,
                                    pos.x,
                                    pos.y,
                                    pos.z,
                                    SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST,
                                    SoundCategory.PLAYERS,
                                    1.4f,
                                    1.0f

                            );
                        }
                        DamageSourceAndDurationIter.remove();


                    }
                    else{
                        dotDamageMap.get(dotDamageTargetEntity).put(dotDamageTargetEntity, dotDamageDuration);
                    }
                }

            }

        });
    }
    public static void giveDotDamage(LivingEntity dotDamageTargetEntity, LivingEntity dotDamageSourceEntity, int duration){
        dotDuration = duration;
        if(dotDamageMap.containsKey(dotDamageTargetEntity)){
            dotDamageMap.get(dotDamageTargetEntity).put(dotDamageSourceEntity, dotDuration);
        }
        else{
            dotDamageMap.put(dotDamageTargetEntity, new HashMap<>());
            dotDamageMap.get(dotDamageTargetEntity).put(dotDamageSourceEntity, dotDuration);
        }
    }
}
