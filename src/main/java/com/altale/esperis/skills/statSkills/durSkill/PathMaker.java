package com.altale.esperis.skills.statSkills.durSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.serverSide.Utilities.ParticleHelper;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.IntConsumer;

public class PathMaker {
    public static final String skillName= SkillsId.DUR_75.getSkillName();
    public static final int airborneDelay = 2;
    public static final int airborneDuration = 50 - airborneDelay;
    public static final float xzRange= 5.5f;
    public static final float baseDamage = 12f;
    public static final float hpCoeffi = 0.04f;
    public static final float defCoeffi = 0.06f;
    public static final float allOutAtkCoeffi = 2.3f;
    public static final int cooltime = 200;
    public static void pathMaker( ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, skillName)){

        }else if(!CoolTimeManager.isOnCoolTime(player, skillName) && player.isOnGround()){
            PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
            float atk = (float) playerFinalStatComponent.getFinalStat(StatType.ATK);
            float def = (float) playerFinalStatComponent.getFinalStat(StatType.DEF);
            float hp = (float) playerFinalStatComponent.getFinalStat(StatType.MAX_HEALTH);
            CoolTimeManager.setCoolTime(player,skillName, 200);
            float yaw   = player.getYaw();
            int repeats;
            float damage ;
            float power;
            boolean allOutAttack;
            if(AbilityBuff.hasBuff(player, SkillsId.DUR_175.getSkillName())){
                CoolTimeManager.ccCoolTime(player, 10);
                damage= (baseDamage + atk* allOutAtkCoeffi);
                CoolTimeManager.setCoolTime(player,skillName, (int) (cooltime * 0.6));
                allOutAttack=true;
                repeats = 5;
                power =1.2f;
            } else {
                CoolTimeManager.ccCoolTime(player, 20);
                allOutAttack = false;
                repeats= 10;
                damage = baseDamage + (def * defCoeffi) + (hp * hpCoeffi);
                power =0.5f;
            }
            AbilityBuff.giveBuff(player, skillName,StatType.SPD, 24, -100, 0, 0);
            double playerY = player.getPos().getY();
            IntConsumer task= step-> {
                if(step == 0){
                    Vec3d look= player.getRotationVec(1.0f);
                    Box box = player.getBoundingBox().expand(xzRange, 1, xzRange);
                    player.setVelocity(0f, 0f, 0f);
                    player.velocityModified = true;
                    Vec3d velocity = new Vec3d(look.x * power,0, look.z * power);
                    player.addVelocity(velocity.x, velocity.y, velocity.z);
                    player.velocityModified = true;
                }else if(step < repeats-1 ){
                    ParticleHelper.spawnFrontShieldWallHorizontal(
                            (ServerWorld) player.getWorld(), player,
                            ParticleTypes.ELECTRIC_SPARK,
                            3.6,   // radius
                            42,    // arcSamples
                            2,     // radialRings
                            5,     // layers(세로 줄 수)
                            0.22,  // layerSpacing(줄 간격)
                            0.15,  // taper(위로 갈수록 15% 줄어듦)
                            1.5,   // ahead(전방 1칸)
                            0.2    // baseYOffset(지면 위 0.2)
                    );
                    Vec3d playerPos = player.getPos();
                    player.setNoGravity(true);
                    player.networkHandler.requestTeleport(playerPos.x, playerY, playerPos.z, yaw, 0);
                    Vec3d look= player.getRotationVec(1.0f);
                    Box box = player.getBoundingBox().expand(xzRange, 1, xzRange);
                    Vec3d velocity = new Vec3d(look.x * power,0, look.z * power);
                    player.addVelocity(velocity.x, velocity.y, velocity.z);
                    player.velocityModified = true;
                    if(step % 3 == 1){
                        List<Entity> entityList = player.getWorld().getOtherEntities(player, box);
                        for(Entity entity : entityList){
                            if(entity instanceof LivingEntity livingTarget){
                                player.getWorld().playSound(
                                        null,
                                        player.getX(),
                                        player.getY(),
                                        player.getZ(),
                                        SoundEvents.ENTITY_ARROW_HIT_PLAYER,
                                        SoundCategory.PLAYERS,
                                        15.0f,
                                        1.0f
                                );
                                if (allOutAttack) {
                                    livingTarget.damage(livingTarget.getWorld().getDamageSources().playerAttack(player), damage);
                                    player.heal(damage * 0.1f);
                                }else{
                                    livingTarget.damage(livingTarget.getWorld().getDamageSources().playerAttack(player), damage);
                                    KnockedAirborneVer2.giveKnockedAirborneVer2(livingTarget, airborneDuration, airborneDelay);
                                }
                            }

                        }
                    }
                }else if(step == repeats - 1 ){
                    player.setNoGravity(false);
                    player.setVelocity(0f, 0f, 0f);
                    player.velocityModified = true;
                }


            };
            Runnable task2 = ()->{
                for(int j = 0; j < 3; j++){
                    player.getWorld().playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.ITEM_SHIELD_BLOCK,
                            SoundCategory.PLAYERS,
                            15.0f,
                            1.0f
                    );
                }


            };
            DelayedTaskManager.addTask(world, player, task, 2 ,skillName+"effect",repeats);
            DelayedTaskManager.addTask(world, player, task2, 2 ,skillName+"sound",repeats);

        }

    }
}
