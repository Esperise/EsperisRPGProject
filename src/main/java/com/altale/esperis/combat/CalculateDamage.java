package com.altale.esperis.combat;

import com.altale.esperis.CallBack.CalculateDamageCallBack;
import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.skill_data.passive.PassiveSkillManager;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class CalculateDamage {
    public static void register(){
        CalculateDamageCallBack.EVENT.register(
                ((damageSource, target, damageAmount) ->{
                    if (!damageSource.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
                        target.damageArmor(damageSource, damageAmount);
                    }
//                    System.out.println("원본 데미지: "+damageAmount);
                    if(target instanceof PlayerEntity player){
                        PassiveSkillManager.hpFallBelowXPercent(player,damageAmount,30);
                    }
                    if(damageSource.equals(target.getDamageSources().generic())) {
                        return damageAmount;
                    }
                    if(damageSource.getAttacker() ==target) return damageAmount;
                    Entity attackerEntity = damageSource.getAttacker();
                    if(attackerEntity instanceof LivingEntity attacker){


                        double attackerFinalDamageCoeffi = 0.00;
                        double attackerDefPenetrateCoeffi= 0.00;
                        double attackerCrit=0.1;
                        double attackerCritDmgCoeffi= 2;

                        double targetDef= Math.min(300,Math.max(15.0, 15+Math.round(target.getMaxHealth()*100/(target.getMaxHealth()+50))));

                        int attackerLevel= (int) (Math.min(100,attacker.getMaxHealth() / 2 + 10));
                        int targetLevel= (int) (Math.min(100,target.getMaxHealth() / 2  + 10));
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
//                            System.out.println("대상 방어력"+targetDef);
                            targetLevel= targetLvComponent.getLevel();
                        }
                        if(!attackerIsPlayer){
                            damageAmount += Math.min(3.0f, damageAmount *2);
                        }
                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        double levelDiff=  targetLevel- attackerLevel;
                        double levelCoeff= 1-( 0.005*levelDiff );
                        double targetFinalDef= targetDef*(1-attackerDefPenetrateCoeffi);
//                        System.out.println(( 1- (targetFinalDef/(targetFinalDef + 100)) ));
                        double damage =  Math.round(damageAmount * ( 1- (targetFinalDef/(targetFinalDef + 100)) ) *(levelCoeff) * (1+ attackerFinalDamageCoeffi)*1000)/1000.0;
//                        System.out.println("렙차: "+levelDiff);
//                        System.out.println("최종 계산된 방어력: "+targetFinalDef);
//                        System.out.println("방어력, 렙차 적용 데미지: "+damage);

                        if(random.nextDouble() <= attackerCrit){
                            damage*=attackerCritDmgCoeffi;
//
                            if(attackerIsPlayer){
                                ((PlayerEntity) attacker).sendMessage(Text.literal("치명타 피해 입힘").formatted(Formatting.BLUE, Formatting.BOLD),true);
                                PassiveSkillManager.criticalFlag((PlayerEntity )attacker, target);
                            }
                            if(targetIsPlayer){
                                ((PlayerEntity) target).sendMessage(Text.literal("치명타 피해 받음").formatted(Formatting.DARK_RED, Formatting.BOLD),true);
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
                        }
                        if(targetIsPlayer){
                            damage= PassiveSkillManager.getDamageFlag((PlayerEntity) target, (float) damage);
                            PassiveSkillManager.hpFallBelowXPercent((PlayerEntity) target, (float) damage,30);
                        }
//                        System.out.println("최종 피해: "+damage);
                        return (float) damage;
                    }
                    else{
                        if(target instanceof PlayerEntity targetPlayer){
                            PlayerFinalStatComponent playerComponent = PlayerFinalStatComponent.KEY.get(targetPlayer);
                            double def = playerComponent.getFinalStat(StatType.DEF);
                            damageAmount = (float) Math.round(damageAmount * ( 1- (def/(def + 100)) )*100)/100;
                            damageAmount = PassiveSkillManager.getDamageFlag((PlayerEntity) target,  damageAmount);
                            PassiveSkillManager.hpFallBelowXPercent(targetPlayer,  damageAmount,30);
//                            System.out.println("최종 피해(낙하데미지 , 화염데미지 등): "+damageAmount);
                        }


                        return damageAmount;
                    }
                })
        );
    }
}
