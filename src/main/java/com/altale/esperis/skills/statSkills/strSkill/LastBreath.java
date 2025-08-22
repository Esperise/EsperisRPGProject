package com.altale.esperis.skills.statSkills.strSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAt;
import com.altale.esperis.skills.SkillsInterface;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import com.altale.esperis.skills.visualEffect.RandomStraight3DLines;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

public class LastBreath implements SkillsInterface {
    public static final String skillName = SkillsId.STR_175.getSkillName();
    public static final int cooltime = 800;
    public static final  float atkCoeffi = 0.18f;//4번 적용됨, 총 1 + 평타 2-3번 가능 = 3.4
    public static final  float barrierAtkCoeffi = 0.4f;
    public static final  int keepAirTime = 40;
    public static final BlockStateParticleEffect particle= new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.CHERRY_LEAVES.getDefaultState());
    public static final DefaultParticleType particle2= ParticleTypes.ELECTRIC_SPARK;
    public static void lastBreath(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, skillName)){

        }else{
            doLastBreath(player, world);
        }
    }
    private static void doLastBreath(ServerPlayerEntity player, ServerWorld world) {
        Entity target = GetEntityLookingAt.getEntityLookingAt(player, 20, 1.0);
        if(target instanceof LivingEntity playerLookingAt) {
            //순간이동후 지속 데미지 주고 return
            if(KnockedAirborneVer2.hasKnockedAirborne((LivingEntity) target)) {
                lastBreathEffect(world, playerLookingAt, player);
            }
            return;
        }
        //바라보는 대상이 에어본 상태가 아니거나 , 없으면 주변 20칸에서 에어본 상태 적 서칭-> 가장 가까운 대상에게 순간이동후 데미지
        Box box = player.getBoundingBox().expand(20, 8, 20);
        Map<LivingEntity, Float> targetDistanceMap = new HashMap<LivingEntity, Float>();
        List<Entity> entityList = player.getWorld().getOtherEntities(player, box);

        for (Entity entity : entityList) {
            if(entity instanceof LivingEntity livingTarget){
                if(KnockedAirborneVer2.hasKnockedAirborne(livingTarget)){
                    float distance = player.distanceTo(livingTarget);
                    targetDistanceMap.put(livingTarget,distance);
                }
            }
        }
        if(targetDistanceMap.isEmpty()){
            player.sendMessage(Text.literal("범위 내에 공중에 뜸 상태인 대상이 없습니다.").formatted(Formatting.BOLD,Formatting.DARK_RED), true);
            return;
        }
        Map.Entry<LivingEntity, Float> best =
                targetDistanceMap.entrySet().stream()
                        .filter(e -> e.getValue() != null)           // 값이 null일 수 있으면
                        .min(Map.Entry.comparingByValue())           // Float 비교
                        .orElse(null);

        LivingEntity nearest = best != null ? best.getKey()   : null;
        float        minDist = best != null ? best.getValue() : Float.MAX_VALUE;
        if(nearest != null){
            lastBreathEffect(world, nearest, player);
        }
    }

    @Override
    public void doSkill(ServerPlayerEntity player, ServerWorld world) {

    }
    private static void lastBreathEffect(ServerWorld world, LivingEntity target, PlayerEntity player){
        AbilityBuff.giveBuff(player, skillName, StatType.DefPenetrate, 120, 0,0.3,1 );
        Vec3d nearestPos = target.getPos();
        Vec3d nearestLookPos = target.getRotationVec(1f);
        Vec3d oppositeLookVec= nearestLookPos.multiply(-1.0f);
        float targetYaw = target.getYaw(1.0f);
        float targetPitch = target.getPitch(1.0f);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        float atk = (float) playerFinalStatComponent.getFinalStat(StatType.ATK);
        IntConsumer task = step->{
            if(step != keepAirTime-1 ){
                if(step % 9 == 1){
                    AbsorptionBuff.giveAbsorptionBuff(world, player, skillName,atk*barrierAtkCoeffi , keepAirTime+100 );
                    target.damage(target.getWorld().getDamageSources().playerAttack(player),atk*atkCoeffi );
                    RandomStraight3DLines.spawnRandomStraightLines(world, target, particle ,1, 1.5, 1.5, -0.4, 6, 1,true,0);
                    for (int i = 0; i < 2; i++) {
                        player.getWorld().playSound(
                                null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundEvents.ENTITY_IRON_GOLEM_REPAIR,
                                SoundCategory.PLAYERS,
                                10.0f,
                                0.7f
                        );
                    }
                }
                if(step % 8 == 1){
                    RandomStraight3DLines.spawnRandomStraightLines(world, target, particle2 ,2, .5, .5, -0.3, 6, 0.2,true,0);
                }
                target.teleport(world, nearestPos.x, nearestPos.y, nearestPos.z,
                        java.util.Collections.emptySet() , targetYaw, targetPitch);
                player.requestTeleport(target.getX()+(1* oppositeLookVec.x),
                        target.getY() + 1.3 ,
                        target.getZ()+((1* oppositeLookVec.z))
                );
                if(player.getBlockStateAtPos().shouldSuffocate(world, player.getBlockPos())){
                    player.requestTeleport(target.getX()+(1* oppositeLookVec.x),
                            target.getY()  ,
                            target.getZ()+((1* oppositeLookVec.z))
                    );
                }
            }else{
                target.setVelocity(0, -5, 0);
                player.setVelocity(0, -5, 0);
                target.velocityModified = true;
                player.velocityModified = true;
                world.playSound(
                        null,
                        nearestPos.getX(),
                        nearestPos.getY(),
                        nearestPos.getZ(),
                        SoundEvents.ENTITY_GENERIC_EXPLODE,
                        SoundCategory.PLAYERS,
                        10,0.4f
                );
            }
        };
        DelayedTaskManager.addTask(world, player, task, 1, skillName, keepAirTime);
        CoolTimeManager.setCoolTime((ServerPlayerEntity) player, skillName, cooltime);
    }
}