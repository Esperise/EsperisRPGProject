package com.altale.esperis.skills.statSkills.dexStatSkill;

import com.altale.esperis.items.itemFunction.SpecialBowItem;
import com.altale.esperis.player_data.skill_data.PlayerSkillComponent;
import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.skill_data.passive.PassiveSkillManager;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAt;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAtDistance;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.function.IntConsumer;

public class Snipe {
    public static final String skillName = SkillsId.DEX_125.getSkillName();
    public static final int cooltime = 240;
    public static final int maxAimingTime = 120;
    public static final int aimingMaxDamageTime = maxAimingTime / 2;
    public static final float aimingDamagePercentPerSecond = 100;
    public static final float maxDamageMultiplier = 300;
    public static final double maxDistance = 100;
    public static final double baseDamage = 10;
    public static final double atkCoeffi =1.8;
    public static final double maxBaseDamage = 30;
    public static final double maxAtkCoeffi =5.4;
//    public static final double dexCoeffi = 0.02;

    public static void snipe(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, skillName)){
            if( DelayedTaskManager.getCurrentRepeatCount(world, player, skillName) >= 1){
                ItemStack hand = player.getMainHandStack();
                if (!(hand.getItem() instanceof SpecialBowItem bow)) {
                    player.sendMessage(Text.literal("특수 활을 들고 있어야 사용 가능합니다."), false);
                    CoolTimeManager.setCoolTime(player, skillName, 10);
                    return;
                }
                //쿨타임이고 스코프 중인데, 스코프 시간이 0.3초 이상일때
                int currentRepeatCount = DelayedTaskManager.getCurrentRepeatCount(world, player, skillName);
                ScopeS2CPacket.send(player,false, 0);
                DelayedTaskManager.deleteTask(world, player, skillName);
                float currentScopeTime = currentRepeatCount/20.0f;
                float damageMultiplier = (float) MathHelper.clamp(aimingDamagePercentPerSecond * currentScopeTime, 0.1, maxDamageMultiplier);
                useSpecialBow(player, world, 0, damageMultiplier);
            }
        }else{
            ItemStack hand = player.getMainHandStack();
            if (!(hand.getItem() instanceof SpecialBowItem bow)) {
                player.sendMessage(Text.literal("특수 활을 들고 있어야 사용 가능합니다."), false);
                CoolTimeManager.setCoolTime(player, skillName, 10);
                return;
            }
            CoolTimeManager.setCoolTime(player, skillName, cooltime);
            Vec3d pos = player.getPos();
            IntConsumer task = step ->{
                player.teleport(pos.x, pos.y, pos.z);
                ScopeS2CPacket.send(player,true, step);
                if(step == maxAimingTime-1){
                    ScopeS2CPacket.send(player, false, 0);
                    DelayedTaskManager.deleteTask(world, player, skillName);
                }
            };
            DelayedTaskManager.addTask(world, player, task, 1, skillName, maxAimingTime);
        }
    }
    public static void useSpecialBow(ServerPlayerEntity player, ServerWorld world, double addtionalDamage, double additionalDamagePercent) {
        PlayerFinalStatComponent finalStatComponent= PlayerFinalStatComponent.KEY.get(player);
        float dex = (float) finalStatComponent.getFinalStat(StatType.DEX);
        Entity target = GetEntityLookingAt.getEntityLookingAt(player, maxDistance, 0.2 + dex*0.008 );
        ItemStack stack= player.getStackInHand(Hand.MAIN_HAND);
        if( target == null  ){ //타겟팅 대상 없음
            specialBowEffects(false,world,player,null);
        } else {
            // 타겟팅 대상에게
            if(target instanceof LivingEntity targetEntity){
                DamageSource src = world.getDamageSources().playerAttack(player);
                targetEntity.timeUntilRegen = 0;
                targetEntity.hurtTime = 0;
                double shotDamage= specialBowDamage(player,addtionalDamage,additionalDamagePercent);
                specialBowEffects(true,world,player,targetEntity);
                PassiveSkillManager.bowHit(player,targetEntity);
                shotDamage= damageReducedByDistance(player, targetEntity, shotDamage, maxDistance);
                targetEntity.damage(src, (float) shotDamage);
            }
            else{
                specialBowEffects(false,world,player,null);
            }
        }

    }
    public static double specialBowDamage(PlayerEntity user, double additionalDamage, double additionalDamagePercent){
        PlayerFinalStatComponent statComponent = PlayerFinalStatComponent.KEY.get(user);
        double atk= statComponent.getFinalStat(StatType.ATK);
        double dex= statComponent.getFinalStat(StatType.DEX);
        double shotDamage= baseDamage + (atk * atkCoeffi);
        if(additionalDamage != 0.0){
            shotDamage += additionalDamage;
        }
        if(additionalDamagePercent !=  0.0){
            shotDamage = shotDamage * ( 1 + (additionalDamagePercent/100.0f) );
        }
        return shotDamage;
    }
    public static double damageReducedByDistance(PlayerEntity user, LivingEntity target, double shotDamage, double maxDistance){
        double distance = GetEntityLookingAtDistance.getEntityLookingAtDistance((ServerPlayerEntity) user, target);
        double halfOfMaxDistance = maxDistance/2;
        if(distance > halfOfMaxDistance){
            double overDistanceCoefficient = (double)  Math.round(100* 0.02 *(distance - halfOfMaxDistance))/100.0;
            overDistanceCoefficient= Math.max(0.5, overDistanceCoefficient);
            shotDamage *= overDistanceCoefficient;
        }
        return shotDamage;
    }
    public static void specialBowEffects(boolean targeted,ServerWorld world, PlayerEntity player, LivingEntity target){
        player.getWorld().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.PLAYERS,
                1.0f,
                0.4f
        );
        if(!targeted){
            //타겟팅 실패:
            Vec3d playerLookVec= player.getRotationVec(1.0f);
            Vec3d playerCameraPos= player.getCameraPosVec(1.0f);
            Vec3d dir = player.getRotationVec(1F).normalize();
            Vec3d start = player.getCameraPosVec(1.0f);
            for(double i = 0; i<= maxDistance; i+=0.1){
                Vec3d pos = playerCameraPos.add(playerLookVec.multiply(i));
                Vec3d pos2 = pos.add(playerLookVec.multiply(Math.min(maxDistance,i+1.5)));
                if(i< 5 && i> 1){
                    world.spawnParticles(
                            ParticleTypes.SONIC_BOOM,
                            pos2.x, pos2.y, pos2.z,
                            1, 0.03, 0.03, 0.03, 0
                    );
                }
                world.spawnParticles(new DustParticleEffect(new Vector3f(0.0f, 0.0f, 0.0f),0.1f), pos.x, pos.y, pos.z, 5, 0, 0, 0, 0);
                world.spawnParticles(
                        ParticleTypes.CRIT,
                        pos2.x, pos2.y, pos2.z,
                        1, 0.03, 0.03, 0.03, 0
                );
            }
        }else{
            //타겟팅
            if(targeted){
                ItemStack stack= player.getStackInHand(Hand.MAIN_HAND);
                Vec3d playerLookVec= player.getRotationVec(1.0f).normalize();
                Vec3d playerCameraPos= player.getCameraPosVec(1.0f);
                double distance = GetEntityLookingAtDistance.getEntityLookingAtDistance((ServerPlayerEntity) player, target);
                for(double i=0; i<=(float) distance ;i+=0.1){
                    Vec3d pos = playerCameraPos.add(playerLookVec.multiply(i));
                    Vec3d pos2 = pos.add(playerLookVec.multiply(Math.min(distance,i+1.5)));
                    if(i< 5 && i> 1){
                        world.spawnParticles(
                                ParticleTypes.SONIC_BOOM,
                                pos2.x, pos2.y, pos2.z,
                                1, 0.03, 0.03, 0.03, 0
                        );
                    }

                }

            }
        }

        world.spawnParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                player.getX(),
                player.getEyeY(),
                player.getZ(),
                1, 0.03, 0.03, 0.03, 0
        );
    }
}
