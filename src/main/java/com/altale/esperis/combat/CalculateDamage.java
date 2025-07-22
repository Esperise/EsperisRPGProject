package com.altale.esperis.combat;

import com.altale.esperis.CallBack.CalculateDamageCallBack;
import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class CalculateDamage {
    public static void register(){
        CalculateDamageCallBack.EVENT.register(
                ((damageSource, target, damageAmount) ->{
                    System.out.println("원본 데미지: "+damageAmount);
                    Entity attackerEntity = damageSource.getAttacker();
                    if(attackerEntity instanceof LivingEntity attacker){


                        double attackerFinalDamageCoeffi = 0.00;
                        double attackerDefPenetrateCoeffi= 0.00;
                        double attackerCrit=0.1;
                        double attackerCritDmgCoeffi= 2;

                        double targetDef= Math.min(100,Math.max(15.0, 15+Math.round(target.getMaxHealth()*100/(target.getMaxHealth()+50))));

                        int attackerLevel= (int) (Math.min(80,attacker.getMaxHealth() / 2 + 10));
                        int targetLevel= (int) (Math.min(80,target.getMaxHealth() / 2  + 10));
                        boolean attackerIsPlayer = false;
                        boolean targetIsPlayer = false;
                        if(attacker instanceof PlayerEntity attackerPlayer){
                            attackerIsPlayer = true;
                            PlayerFinalStatComponent attackerComponent = PlayerFinalStatComponent.KEY.get(attackerPlayer);
                            PlayerLevelComponent attackerLvComponent = PlayerLevelComponent.KEY.get(attackerPlayer);
                            attackerFinalDamageCoeffi= attackerComponent.getFinalStat(StatType.FinalDamagePercent);
                            attackerCrit= attackerComponent.getFinalStat(StatType.CRIT);
                            attackerCritDmgCoeffi= attackerComponent.getFinalStat(StatType.CRIT_DAMAGE);
                            attackerLevel= attackerLvComponent.getLevel();

                        }
                        if(target instanceof PlayerEntity targetPlayer){
                            targetIsPlayer = true;
                            PlayerFinalStatComponent targetComponent = PlayerFinalStatComponent.KEY.get(targetPlayer);
                            PlayerLevelComponent targetLvComponent = PlayerLevelComponent.KEY.get(targetPlayer);
                            targetDef= targetComponent.getFinalStat(StatType.DEF);
                            targetLevel= targetLvComponent.getLevel();
                        }
                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        double levelDiff=  targetLevel- attackerLevel;
                        double levelCoeff= 1-( 0.01*levelDiff );
                        double targetFinalDef= targetDef*(1-attackerDefPenetrateCoeffi);
                        double damage =  Math.round(damageAmount * ( 1- (targetFinalDef/(targetFinalDef + 100)) ) *(levelCoeff) * (1+ attackerFinalDamageCoeffi)*1000)/1000.0;
                        System.out.println("levelDiff: "+levelDiff);
                        System.out.println("targetFinalDef: "+targetFinalDef);
                        System.out.println("damage: "+damage);
                        if(random.nextDouble() <= attackerCrit){
                            damage*=attackerCritDmgCoeffi;
//
                            if(attackerIsPlayer){
                                ((PlayerEntity) attacker).sendMessage(Text.literal("치명타 피해 입힘"),true);
                            }
                            if(targetIsPlayer){
                                ((PlayerEntity) target).sendMessage(Text.literal("치명타 피해 받음"),true);
                            }
                            target.getWorld().playSound(
                                    null,
                                    target.getX(), target.getY(), target.getZ(),
                                    SoundEvents.ENTITY_IRON_GOLEM_DAMAGE,
                                    SoundCategory.NEUTRAL,
                                    1.6F,0.6F
                            );
                            attacker.getWorld().playSound(
                                    null,
                                    target.getX(), target.getY(), target.getZ(),
                                    SoundEvents.ENTITY_ARROW_HIT_PLAYER,
                                    SoundCategory.PLAYERS,
                                    1.0F,0.6F
                            );
                            ((ServerWorld) target.getWorld()).spawnParticles(
                                    ParticleTypes.ENCHANTED_HIT,
                                    target.getX(), target.getY(), target.getZ(),
                                    20, 0.3,0.3,0.3,0.0
                            );((ServerWorld) target.getWorld()).spawnParticles(
                                    ParticleTypes.CRIT,
                                    target.getX(), target.getY(), target.getZ(),
                                    20, 0.3,0.3,0.3,0.0
                            );
                            return (float) damage;
                        }
                        return (float) damage;
                    }
                    else{
                        return damageAmount;
                    }
                })
        );
    }
}
