package com.altale.esperis.skills.statSkills.strSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.ArcPointEmitter;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.serverSide.Utilities.HorizenSweepEffects;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.IntConsumer;

import static com.altale.esperis.serverSide.Utilities.HorizenSweepEffects.makeDustStreakEmitter;

public class HorizenSweep {
    public static final String skillName= SkillsId.STR_25.getSkillName();
    public static final float atkCoeffi = 1.0f;
    public static final float baseDamage= 5.0f;
    public static final float barrierAtkCoeffi = 0.1f;
    public static final float baseBarrierAmount = 1.5f;
    public static final int cooltime = 80;
    public static void horizenSweep(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, skillName)){

        }else{
        CoolTimeManager.setCoolTime(player,skillName , cooltime);
            PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(player);
            float atk = (float) finalStatComponent.getFinalStat(StatType.ATK);
            float as= (float) finalStatComponent.getFinalStat(StatType.ATTACK_SPEED);
            int spd = (int) (as- 1) * 10 ;
            int totalSteps = Math.max( 5 , 10- spd);
            double radius = 1.0;
            double halfAngleDeg = 80.0;
            double yOffset = -0.4;
            ServerWorld sWorld = (ServerWorld) player.getWorld();
            Vec3d look = player.getRotationVec(1.0F);
            Vec3d lookXZ = new Vec3d(look.x, 0, look.z).normalize();
            double baseAngle = Math.atan2(lookXZ.z, lookXZ.x);
            double halfRad   = Math.toRadians(halfAngleDeg);
            Vec3d center     = player.getPos().add(0, player.getEyeY() - player.getY() + yOffset, 0);
            ArcPointEmitter emitter = makeDustStreakEmitter(/*sideOffset*/ 0.0);

// 1) 스윕 Consumer를 '한 번만' 생성
            IntConsumer sweep = HorizenSweepEffects.sectorSweepXZ(
                    sWorld, player,
                    ParticleTypes.CHERRY_LEAVES,
                    /*radius*/ radius,
                    /*halfAngleDeg*/ halfAngleDeg,
                    /*yOffset*/ yOffset,
                    /*spacing(arc)*/ 0.35,
                    /*spacing(ray)*/ 0.35,
                    /*totalSteps*/ totalSteps,
                    /*drawStartRay*/ false,
                    /*drawEndRay*/ false,
                    emitter
            );

// 2) 스케줄에 넘길 action: 매 틱 현재 step을 스윕에 전달 + 박스 계산
            IntConsumer action = step -> {
                sweep.accept(step); // ★ 핵심: 반환된 Consumer에 step을 전달

                Box curBox = HorizenSweepEffects.sectorSweepBoxAtStep(
                        center, baseAngle, halfRad, 8,
                        /*yMin*/ center.y - 0.5, /*yMax*/ center.y + 0.5,
                        /*step*/ step, /*totalSteps*/ totalSteps
                );
                int count = 0;
                List<Entity> entityList= player.getWorld().getOtherEntities(player, curBox);
                for(Entity entity : entityList){
                    if(entity instanceof LivingEntity livingTarget && livingTarget.isAlive()){
                        livingTarget.damage(livingTarget.getWorld().getDamageSources().playerAttack(player), baseDamage + atk* atkCoeffi);
                        count++;
                        player.getWorld().playSound(
                                null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundEvents.ENTITY_IRON_GOLEM_DEATH,
                                SoundCategory.PLAYERS,
                                15.0f,
                                0.7f
                        );
                    }
                }
                if(count > 0){
                    AbsorptionBuff.giveAbsorptionBuff(world, player, skillName, (baseBarrierAmount+barrierAtkCoeffi) * count, 40);
                }
            };

// 3) 등록: repeats는 totalSteps와 같게(전개 완료)
            DelayedTaskManager.addTask(sWorld, player, action, /*tickInterval*/ 1, skillName, /*repeats*/ totalSteps);



        }
    }

}
