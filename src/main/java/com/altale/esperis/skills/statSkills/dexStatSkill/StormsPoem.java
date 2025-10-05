package com.altale.esperis.skills.statSkills.dexStatSkill;

import com.altale.esperis.items.itemFunction.SpecialBowItem;
import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.skill_data.passive.PassiveSkillManager;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAt;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAtDistance;
import com.altale.esperis.serverSide.Utilities.ParticleHelper;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.function.IntConsumer;

public class StormsPoem {
    public static final String skillName = SkillsId.DEX_175.getSkillName();
    public static final int cooltime = 2000;
    public static final float additionalShotAsCoefficient = 0.1f;
    public static final int baseShot = 1;
    public static final int maxShotCount= 40;
    public static final double maxDistance = 40;
    public static final float baseDamage = 1;
    public static final float atkCoeffi =0.15f;
    public static final float slowPercent = -60;
    public static final float baseHitHeal = 2.3f;
    public static final float hitHealAtkCoefficient = 0.1f;
//    public static final float baseShotBarrier = 2f;
//    public static final float baseShotBarrierAtkCoefficient = 0.12f;
    public static final int bowHitCooltimeReduceTick = 30;
    public static void stormsPoem(ServerPlayerEntity player, ServerWorld world){
        if(CoolTimeManager.isOnCoolTime(player, skillName)){

        }else{
            ItemStack hand = player.getMainHandStack();
            if (!(hand.getItem() instanceof SpecialBowItem)) {
                player.sendMessage(Text.literal("특수 활을 들고 있어야 사용 가능합니다."), false);
                return;
            }
            CoolTimeManager.setCoolTime(player, skillName, cooltime);
            PlayerFinalStatComponent finalStats = PlayerFinalStatComponent.KEY.get(player);
            float as = (float) finalStats.getFinalStat(StatType.ATTACK_SPEED);
            int repeats = Math.min(maxShotCount, baseShot + (int) (as/additionalShotAsCoefficient));
            float atk = (float) finalStats.getFinalStat(StatType.ATK);
            IntConsumer task = step -> {
                ItemStack currentHand = player.getMainHandStack();
                if (!(currentHand.getItem() instanceof SpecialBowItem bow)) {
                    player.sendMessage(Text.literal("특수 활을 들고 있어야 사용 가능합니다."), false);
                    DelayedTaskManager.deleteTask(world , player, skillName);
                    return;
                }
                player.sendMessage(Text.literal("남은 화살: "+(repeats-step-1)),true);
                ParticleHelper.spawnFrontSemicircleHorizontal(
                        (ServerWorld) player.getWorld(), player,
                        5.6,   // radius
                        80,    // samples
                        2,     // rings(두께)
                        1.0,   // ahead (전방 1칸)
                        1.0   // yOffset (지면 위 0.2)
                );
                AbilityBuff.giveBuff(player, skillName+": 속도 감소", StatType.SPD, 6, slowPercent, 0, 1);
//                AbsorptionBuff.giveAbsorptionBuff(world, player, skillName+step, baseShotBarrier+ atk* baseShotBarrierAtkCoefficient, repeats+1);
                useSpecialBow(player, world,0,0);
                bow.incUsage(currentHand);

            };
            DelayedTaskManager.addTask(world, player, task, 2, skillName, repeats);
        }
    }
    public static void useSpecialBow(ServerPlayerEntity player, ServerWorld world, double addtionalDamage, double additionalDamagePercent) {
        PlayerFinalStatComponent finalStatComponent= PlayerFinalStatComponent.KEY.get(player);
        float dex = (float) finalStatComponent.getFinalStat(StatType.DEX);
        Entity target = GetEntityLookingAt.getEntityLookingAt(player, maxDistance, 0.2 + dex*0.01 );
        if( target == null  ){ //타겟팅 대상 없음
            specialBowEffects(false,world,player,null);
        } else {
            // 타겟팅 대상에게
            if(target instanceof LivingEntity targetEntity){
                float atk = (float) finalStatComponent.getFinalStat(StatType.ATK);
                player.heal(baseHitHeal + atk*hitHealAtkCoefficient);
                DamageSource src = world.getDamageSources().playerAttack(player);
                targetEntity.timeUntilRegen = 0;
                targetEntity.hurtTime = 0;
                double shotDamage= specialBowDamage(player,addtionalDamage,additionalDamagePercent);
                specialBowEffects(true,world,player,targetEntity);
                PassiveSkillManager.bowHit(player,targetEntity);
                ItemStack stack= player.getStackInHand(Hand.MAIN_HAND);
                int usage =0;
                if(stack.getItem() instanceof SpecialBowItem bow){
                    usage = bow.getUsage(stack);
                    if(usage >=3){
                        shotDamage = PassiveSkillManager.bowHitAddDamage(player, targetEntity, (float) shotDamage);
                    }
                }
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
                SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT,
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
                world.spawnParticles(new DustParticleEffect(new Vector3f(0.9f, 0.9f, 0.7f),0.2f), pos.x, pos.y, pos.z, 5, 0, 0, 0, 0);
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
                int usage =0;
                if(stack.getItem() instanceof SpecialBowItem bow){
                    usage = bow.getUsage(stack);
                }
                Vec3d playerLookVec= player.getRotationVec(1.0f).normalize();
                Vec3d playerCameraPos= player.getCameraPosVec(1.0f);
                double distance = GetEntityLookingAtDistance.getEntityLookingAtDistance((ServerPlayerEntity) player, target);
                for(double i=0; i<=(float) distance ;i+=0.1){
                    Vec3d pos = playerCameraPos.add(playerLookVec.multiply(i));
                    Vec3d pos2 = pos.add(playerLookVec.multiply(Math.min(distance,i+1.5)));
                    if(usage>=3){
//                        world.spawnParticles(new DustParticleEffect(new Vector3f(0.8f, 0.8f, 1.0f),0.2f), pos.x, pos.y, pos.z, 5, 0, 0, 0, -1);
                        world.spawnParticles(
                                ParticleTypes.UNDERWATER,
                                pos2.x, pos2.y, pos2.z,
                                30, 0.1, 0.1, 0.1, 0
                        );
                        world.spawnParticles(
                                ParticleTypes.ENCHANTED_HIT,
                                pos2.x, pos2.y, pos2.z,
                                3, 0.05, 0.05, 0.05, 0
                        );
                    }else{
                        world.spawnParticles(new DustParticleEffect(new Vector3f(0.9f, 0.9f, 0.5f),0.2f), pos.x, pos.y, pos.z, 3, 0, 0, 0, 0);
                        world.spawnParticles(
                                ParticleTypes.WAX_ON,
                                pos2.x, pos2.y, pos2.z,
                                1, 0.03, 0.03, 0.03, 0
                        );
                    }
                }
                player.getWorld().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ITEM_TRIDENT_HIT,
                        SoundCategory.PLAYERS,
                        1.0f,
                        0.4f
                );
            }
        }
    }


}
