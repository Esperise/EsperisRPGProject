package com.altale.esperis.skills.statSkills.durSkill;

import com.altale.esperis.player_data.skill_data.PlayerSkillComponent;
import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.serverSide.Utilities.ParticleHelper;
import com.altale.esperis.skillTest1.PlayerFallHandler;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.IntConsumer;

public class EarthQuake {
    public static final String skillName = SkillsId.DUR_125.getSkillName();
    public static final float jumpPower= 3f;
    public static final int coolTime = 100;
    public static final float alloutBarrierAtkCoeffi = 1f;
    public static final float alloutLandingBarrierAtkCoeffi = 3.5f;
    public static void earthQuake( ServerPlayerEntity player, ServerWorld world) {

        if(CoolTimeManager.isOnCoolTime(player, skillName) && DelayedTaskManager.getCurrentRepeatCount(world, player, skillName) >= 1){
            int chargedTick = DelayedTaskManager.getCurrentRepeatCount(world, player, skillName);
            DelayedTaskManager.deleteTask(world, player, skillName);
            float radius = 1+ (chargedTick * 0.45f);
            PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(player);
            float hp =(float) finalStatComponent.getFinalStat(StatType.MAX_HEALTH);
            float def =(float) finalStatComponent.getFinalStat(StatType.DEF);
            float atk =(float) finalStatComponent.getFinalStat(StatType.ATK);
            Box box = player.getBoundingBox().expand(radius, 10, radius);
            List<Entity> entities = player.getWorld().getOtherEntities(player, box);
            Vec3d pos = player.getEyePos();
            float damage = 10 + (hp* 0.007f * chargedTick) + (def * 0.015f * chargedTick);
            for(Entity entity : entities) {
                if (entity instanceof LivingEntity livingTarget) {
                    livingTarget.damage(livingTarget.getWorld().getDamageSources().playerAttack(player), damage);
                    if(livingTarget instanceof PlayerEntity playerTarget) {
                        CoolTimeManager.ccCoolTime((ServerPlayerEntity) playerTarget, 80);
                    }
                }
            }
            world.spawnParticles(ParticleTypes.SNOWFLAKE, pos.x,pos.y, pos.z, 500 *(int) radius, radius, 0.5, radius, 0);
            CoolTimeManager.setCoolTime(player, skillName, coolTime);
            Runnable deleteTask = ()->{
                DelayedTaskManager.deleteTask(world, player, skillName);
            };
            DelayedTaskManager.addTask(world, player,deleteTask, 1, skillName+"delete", 5 );

        }else{

            PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(player);
            float atk = (float) finalStatComponent.getFinalStat(StatType.ATK);
            boolean allOutAttack = false;
            float landingBarrier= player.getMaxHealth() * 0.3f;
            if(AbilityBuff.hasBuff(player, SkillsId.DUR_175.getSkillName())){
                allOutAttack = true;
                AbsorptionBuff.giveAbsorptionBuff(world, player, skillName, atk* alloutBarrierAtkCoeffi, 20);
                landingBarrier=atk* alloutLandingBarrierAtkCoeffi;
            }
            List<Entity> nearby = player.getWorld().getOtherEntities(player, player.getBoundingBox().expand(10));
            for (Entity entity : nearby) {
                if(entity instanceof LivingEntity livingTarget){
                    if(livingTarget instanceof PlayerEntity playerTarget){
                        AbilityBuff.giveBuff(playerTarget, skillName, StatType.SPD, 40, -30, 0, 1);
                    }else{
                        livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 1, false, false));
                    }
                }
            }


//            Vec3d velocity = new Vec3d(0, jumpPower, 0);
//            player.addVelocity(0, velocity.y, 0);
//            player.velocityModified = true;
            int repeats = 101;

            float finalLandingBarrier = landingBarrier;
            Vec3d pos = player.getPos();
            PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
            float hp =(float) playerFinalStatComponent.getFinalStat(StatType.MAX_HEALTH);
            float def =(float) playerFinalStatComponent.getFinalStat(StatType.DEF);
            IntConsumer task = step->{
                float radius = 1+ (step * 0.14f);
                if(step <= repeats-2){
                    player.sendMessage(Text.literal(String.format( "재사용 가능 시간: %.2f ", Math.round(100* ( repeats-step-1)/20.0f)/100f) ), true);
                    player.sendMessage(Text.literal( "radius "+ radius ), false);
                    player.requestTeleport(pos.x, pos.y, pos.z);
                    ParticleHelper.drawCircleXZ(world, pos, ParticleTypes.SNOWFLAKE, radius, (int) radius * 24);
                    Box box = player.getBoundingBox().expand(15, 4, 15);
                    List<Entity> entities = player.getWorld().getOtherEntities(player, box);
                    for(Entity entity : entities){
                        if(entity instanceof LivingEntity livingTarget){
                            livingTarget.setFrozenTicks(livingTarget.getFrozenTicks() + 10);
                            if(livingTarget instanceof PlayerEntity playerTarget){
                                AbilityBuff.giveBuff(playerTarget, skillName, StatType.SPD, 40, -1, 0, 96);
                            }else{
                                livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 5, false, false));
                            }
                        }
                    }
                    world.spawnParticles(ParticleTypes.SNOWFLAKE, pos.x,pos.y, pos.z, 100 *(int) radius, radius, 5.0, radius, 0);
                    player.requestTeleportAndDismount(pos.x, pos.y, pos.z);
                    if(step % 20 == 1){
                        String buffName= skillName+step;
                        AbsorptionBuff.giveAbsorptionBuff(world, player, buffName, finalLandingBarrier/5, 120);
                        CoolTimeManager.ccCoolTime( player, 30);
                    }
                }
                else if(step == repeats -1 ){
                    Box box = player.getBoundingBox().expand(radius, 5, radius);
                    List<Entity> entities = player.getWorld().getOtherEntities(player, box);
                    float damage = 10 + (hp* 0.007f * step) + (def * 0.015f * step);
                    for(Entity entity : entities) {
                        if (entity instanceof LivingEntity livingTarget) {
                            livingTarget.damage(livingTarget.getWorld().getDamageSources().playerAttack(player), damage);
                        }
                    }
                    world.spawnParticles(ParticleTypes.SNOWFLAKE, pos.x,pos.y, pos.z, 500 *(int) radius, radius, 0.5, radius, 0);

                }
            };
            DelayedTaskManager.addTask(world, player, task, 1, skillName, repeats);
        }
    }
}
