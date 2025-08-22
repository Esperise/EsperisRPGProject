package com.altale.esperis.skills.statSkills.strSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.ArcPointEmitter;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.visualEffect.RandomStraight3DLines;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import java.util.List;
import java.util.function.IntConsumer;

import static com.altale.esperis.skills.statSkills.strSkill.HorizenSweepEffects.makeDustStreakEmitter;

public class HorizenSweep {
    public static final String skillName= SkillsId.STR_25.getSkillName();
    public static final float atkCoeffi = 0.75f;
    public static final float baseDamage= 5.0f;
    public static final float barrierAtkCoeffi = 0.1f;
    public static final float baseBarrierAmount = 2f;
    public static final int cooltime = 100;
    public static final float delayReduceCoeffi = 100/5.0f;
    public static final float cooltimeReduceCoeffi = 100/3.0f;
    public static final int maxReducedCoolTime = 50;
    public static final int maxReducedDelay = 5;
    public static final BlockStateParticleEffect particle= new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.CHERRY_LEAVES.getDefaultState());

    public static void doHorizenSweep(ServerPlayerEntity player, ServerWorld world) {
        PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        float atk = (float) finalStatComponent.getFinalStat(StatType.ATK);
        float as= (float) finalStatComponent.getFinalStat(StatType.ATTACK_SPEED);
        int spd = (int) ((as- 1) * delayReduceCoeffi) ;//5%당 0.05초 감소(딜레이)
        int totalSteps = Math.max( maxReducedDelay , 20- spd);
        double radius = 8.0;
        double halfAngleDeg = 60.0;
        double yOffset = -0.4;
        ServerWorld sWorld = (ServerWorld) player.getWorld();
        Vec3d look = player.getRotationVec(1.0F);
        Vec3d lookXZ = new Vec3d(look.x, 0, look.z).normalize();
        double baseAngle = Math.atan2(lookXZ.z, lookXZ.x);
        double halfRad   = Math.toRadians(halfAngleDeg);
        Vec3d center     = player.getPos().add(0, player.getEyeY() - player.getY() + yOffset, 0);
        ArcPointEmitter emitter = makeDustStreakEmitter(/*sideOffset*/ 0.0, player);

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
        double start = baseAngle - halfRad;
        double curEnd = baseAngle + halfRad;
        Box totalBox = HorizenSweepEffects.computeSectorBoundingBoxPartial(center, start, curEnd, radius, center.y - 2.5, /*yMax*/ center.y + 2.5);
        List<Entity> totalEntityList = player.getWorld().getOtherEntities(player, totalBox);
        int targets = totalEntityList.size();
        if( targets>0 ){
            AbsorptionBuff.giveAbsorptionBuff(world, player, skillName, (baseBarrierAmount+barrierAtkCoeffi)*targets , 40);
        }
// 2) 스케줄에 넘길 action: 매 틱 현재 step을 스윕에 전달 + 박스 계산
        IntConsumer action = step -> {
            sweep.accept(step); // ★ 핵심: 반환된 Consumer에 step을 전달
            Box curBox = HorizenSweepEffects.sectorSweepBoxAtStep(
                    center, baseAngle, halfRad, 8,
                    /*yMin*/ center.y - 3.5, /*yMax*/ center.y + 3.5,
                    /*step*/ step, /*totalSteps*/ totalSteps
            );
            List<Entity> entityList= player.getWorld().getOtherEntities(player, curBox);
            for(Entity entity : entityList){
                if(entity instanceof LivingEntity livingTarget && livingTarget.isAlive()){
                    if(totalEntityList.contains(entity)){
                        livingTarget.damage(livingTarget.getWorld().getDamageSources().playerAttack(player), baseDamage + atk* atkCoeffi);
                        for(int i =0 ; i< 3; i++){
                            player.getWorld().playSound(
                                    null,
                                    player.getX(),
                                    player.getY(),
                                    player.getZ(),
                                    SoundEvents.ENTITY_GENERIC_SMALL_FALL,
                                    SoundCategory.PLAYERS,
                                    15.0f,
                                    0.7f
                            );
                        }
                        RandomStraight3DLines.spawnRandomStraightLines(world, livingTarget, particle ,1, 0.5, 0.5, -0.4, 10, 0.2,true,0);
                        totalEntityList.remove(entity);
                    }
                }
            }
        };


// 3) 등록: repeats는 totalSteps와 같게(전개 완료)
        DelayedTaskManager.addTask(sWorld, player, action, /*tickInterval*/ 1, skillName, /*repeats*/ totalSteps);
        IntConsumer sweepeffect2 = HorizenSweepEffects.sectorSweepXZ(
                sWorld, player,
                ParticleTypes.CHERRY_LEAVES,
                /*radius*/ radius-1,
                /*halfAngleDeg*/ halfAngleDeg,
                /*yOffset*/ yOffset,
                /*spacing(arc)*/ 0.35,
                /*spacing(ray)*/ 0.35,
                /*totalSteps*/ totalSteps,
                /*drawStartRay*/ false,
                /*drawEndRay*/ false
        );
        DelayedTaskManager.addTask(sWorld, player, sweepeffect2, /*tickInterval*/ 1, skillName+"effect2", /*repeats*/ totalSteps);
        IntConsumer sweepeffect3 = HorizenSweepEffects.sectorSweepXZ(
                sWorld, player,
                ParticleTypes.CHERRY_LEAVES,
                /*radius*/ radius-2,
                /*halfAngleDeg*/ halfAngleDeg,
                /*yOffset*/ yOffset,
                /*spacing(arc)*/ 0.35,
                /*spacing(ray)*/ 0.35,
                /*totalSteps*/ totalSteps,
                /*drawStartRay*/ false,
                /*drawEndRay*/ false
        );
        DelayedTaskManager.addTask(sWorld, player, sweepeffect3, /*tickInterval*/ 1, skillName+"effect3", /*repeats*/ totalSteps);
        IntConsumer sweepeffect4 = HorizenSweepEffects.sectorSweepXZ(
                sWorld, player,
                ParticleTypes.CHERRY_LEAVES,
                /*radius*/ radius-4,
                /*halfAngleDeg*/ halfAngleDeg,
                /*yOffset*/ yOffset,
                /*spacing(arc)*/ 0.35,
                /*spacing(ray)*/ 0.35,
                /*totalSteps*/ totalSteps,
                /*drawStartRay*/ false,
                /*drawEndRay*/ false
        );
        DelayedTaskManager.addTask(sWorld, player, sweepeffect4, /*tickInterval*/ 1, skillName+"effect4", /*repeats*/ totalSteps);

    }
    public static void horizenSweep(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, skillName)){

        }else{
            PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(player);
            float as= (float) finalStatComponent.getFinalStat(StatType.ATTACK_SPEED);
            int spd = (int) ((as- 1) * delayReduceCoeffi) ;//5%당 0.05초 감소(딜레이)
            CoolTimeManager.setCoolTime(player,skillName , Math.max( maxReducedCoolTime ,cooltime -(int) ((as- 1) * cooltimeReduceCoeffi)));//쿹타임: 3%당 0.05초, 120%에서 최대
            doHorizenSweep(player, world);
        }
    }

}
