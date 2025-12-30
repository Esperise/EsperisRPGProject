package com.altale.esperis.skills.statSkills.strSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.skillTest1.KnockedAirborne;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import com.altale.esperis.skills.visualEffect.RandomStraight3DLines;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.IntConsumer;

public class WindSlash {
    public static final String skillName = SkillsId.STR_75.getSkillName();
    public static final float atkCoeffi = 0.7f;
    public static final float baseDamage= 6.0f;
    public static final float HpCoeffi= 0.036f;
    public static final int cooltime = 280;
    public static final float cooltimeReduceCoeffi = 0.3333f;
    public static final int maxReducedCoolTime = 140;
    public static final DefaultParticleType particle= ParticleTypes.ELECTRIC_SPARK;

    public static void windSlash(ServerPlayerEntity player, ServerWorld world) {
        if (CoolTimeManager.isOnCoolTime(player, skillName)) {

        } else {
            HorizenSweep.doHorizenSweep(player, world);
            PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(player);
            float atk = (float) finalStatComponent.getFinalStat(StatType.ATK);
            float as = (float) finalStatComponent.getFinalStat(StatType.ATTACK_SPEED);

            CoolTimeManager.setCoolTime(player, skillName, Math.max(maxReducedCoolTime, (int) (cooltime * (1-((as- 1) * cooltimeReduceCoeffi)))));
            int maxRadius = 12;
            double halfAngleDeg = 80;
            double yOffset = -0.5;

            ServerWorld sWorld = (ServerWorld) player.getWorld();
            Vec3d look = player.getRotationVec(1.0F);
            Vec3d dirXZ = new Vec3d(look.x, 0, look.z).normalize();
            double baseAngle = Math.atan2(dirXZ.z, dirXZ.x);
            double halfRad = Math.toRadians(halfAngleDeg);
            Vec3d center = player.getPos().add(0, player.getEyeY() - player.getY() + yOffset, 0);

// 전체 부채꼴 박스 (초기 후보 수집용)
            Box totalBox = WindSlashEffects.SectorRingSweep.sectorBoxTotal(center, baseAngle, halfRad, maxRadius,
                    center.y - 2.5, center.y + 2.5);
            List<Entity> allCandidates = sWorld.getOtherEntities(player, totalBox);

// 링 밴드 이펙트 생성
            var sweep = WindSlashEffects.SectorRingSweep.growingSectorBands(
                    sWorld, (ServerPlayerEntity) player,
                    ParticleTypes.CHERRY_LEAVES,
                    halfAngleDeg, maxRadius, yOffset,
                    /*arcSpacing*/ 0.5, /*radialStep*/ 1.0
            );

// 매 틱(step: 0..maxRadius-1) 실행 + 해당 링 밴드 박스로 세부 판정
            IntConsumer action = step -> {
                sweep.accept(step);

                Box bandBox = WindSlashEffects.SectorRingSweep.annularBandBoxAtStep(
                        center, baseAngle, halfRad,
                        step, maxRadius,
                        center.y - 2.5, center.y + 2.5
                );

                List<Entity> bandEntities = sWorld.getOtherEntities(player, bandBox, e -> e instanceof LivingEntity);
                // 필요시 각/거리로 링 밴드 내부만 최종 필터링 (정확도↑)
                for (Entity entity : bandEntities) {
                    if (entity instanceof LivingEntity livingTarget && livingTarget.isAlive()) {
                        if (allCandidates.contains(entity)) {
                            float targetMaxHealth = livingTarget.getMaxHealth();
                            livingTarget.damage(livingTarget.getWorld().getDamageSources().playerAttack(player), baseDamage + atk * atkCoeffi + (player.getMaxHealth()*HpCoeffi));
                            for (int i = 0; i < 2; i++) {
                                player.getWorld().playSound(
                                        null,
                                        player.getX(),
                                        player.getY(),
                                        player.getZ(),
                                        SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                                        SoundCategory.PLAYERS,
                                        10.0f,
                                        0.7f
                                );
                                RandomStraight3DLines.spawnRandomStraightLines(world, livingTarget, particle ,1, 0.1, 0.5, -0.4, 6, 0.2,true,0);
                            }
                            KnockedAirborneVer2.giveKnockedAirborneVer2(livingTarget,18,2);
                            allCandidates.remove(entity);
                        }


                    }
                }
            };

            DelayedTaskManager.addTask(sWorld, player, action, /*tickInterval*/ 2, skillName, /*repeats*/ maxRadius);


            for (int i = 0; i < 5; i++) {
                player.getWorld().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ENTITY_GLOW_SQUID_SQUIRT,
                        SoundCategory.PLAYERS,
                        15.0f,
                        0.7f
                );
            }

        }
    }
}
