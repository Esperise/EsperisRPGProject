package com.altale.esperis.skills.statSkills.strSkill;

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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.util.function.IntConsumer;

public class GrandStarfall {
    public static final String skillName= SkillsId.STR_125.getSkillName();
    public static final float barrierAtkCoeffi= 0.8f;
    public static final float baseBarrierAmount= 10;
    public static final int cooltime = 560;
    public static void grandStarfall(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, skillName)){

        }else{
            if(DelayedTaskManager.getCurrentRepeatCount(world, player, skillName) >= 0){
                if(DelayedTaskManager.getCurrentRepeatCount(world, player, skillName) == 12){
                    return;
                } else if(DelayedTaskManager.getCurrentRepeatCount(world, player, skillName) == 13){
                    return;
                }
                DelayedTaskManager.deleteTask(world, player,skillName);
                DelayedTaskManager.deleteTask(world, player, skillName+"keep");
                IntConsumer landingTask = task-> {
                    if(task ==1){
                        player.setNoGravity(false);
                        PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(player);
                        float atk = (float) finalStatComponent.getFinalStat(StatType.ATK);
                        Vec3d look = player.getRotationVec(1.0f);
                        AbsorptionBuff.giveAbsorptionBuff(world, player, skillName, baseBarrierAmount +atk * barrierAtkCoeffi, 60);
                        Vec3d velocity2;
                        System.out.println(look.y);
                        if(look.y >= 0.0f ){
                            velocity2 = new Vec3d(0,  -10 ,0);
                        }else{
                            velocity2 = new Vec3d(look.x * 4, -10 + (look.y*5) , look.z * 4);
                        }
                        player.addVelocity(velocity2.x, velocity2.y  , velocity2.z);
                        player.velocityModified = true;
                        PlayerFallHandler.enableIgnoreFall(player);
                        CoolTimeManager.setCoolTime(player, skillName, cooltime);
                    }else{
                        if(player.isOnGround()){
                            player.setVelocity(player.getVelocity().multiply(0.5));
                        }
                    }

                };
                DelayedTaskManager.addTask(world, player,landingTask, 2, skillName+" Landing", 8 );

            }else{
                Vec3d velocity = new Vec3d(0, 1.7f, 0);
                player.addVelocity(0, velocity.y, 0);
                player.velocityModified = true;
                int repeats = 60;
                IntConsumer task = step->{
                    if(step <= repeats-1){
                        if(step < repeats-1 ){
                            AbilityBuff.giveBuff(player,skillName, StatType.ATK, 60, 15, 0, 1);
                            player.sendMessage(Text.literal(String.format( "재사용 가능 시간: %.2f ", Math.round(100* ( repeats-step-1)/20.0f)/100f) ), true);
                        }
                            if(!CoolTimeManager.isOnCoolTime(player, skillName) && !player.isOnGround() && step == 10){
                                player.setNoGravity(true);
                            }
                            if(step == 12 && player.hasNoGravity()){
                                Vec3d pos = player.getPos();
                                Runnable keep =()-> player.requestTeleport(pos.x, pos.y, pos.z);
                                DelayedTaskManager.addTask(world, player, keep, 1, skillName+"keep", repeats-12-1);
                            }
                            if(step == repeats-1){
                                player.sendMessage(Text.literal(String.format( "재사용 가능 시간: %.2f ", Math.round(100* ( repeats-step-1)/20.0f)/100f) ), true);
                                DelayedTaskManager.deleteTask(world, player, skillName+"keep");
                                player.setNoGravity(false);
                                CoolTimeManager.setCoolTime(player, skillName, cooltime);
                            }
                    }

                };
                DelayedTaskManager.addTask(world, player, task,1, skillName, repeats);
            }

        }
    }
}
