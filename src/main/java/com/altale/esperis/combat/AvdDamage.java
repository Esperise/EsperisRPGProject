package com.altale.esperis.combat;

import com.altale.esperis.CallBack.AvdCallback;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.concurrent.ThreadLocalRandom;

public class AvdDamage {
    public static void register(){
        AvdCallback.EVENT.register(
                ((source, target, amount) -> {

                    Entity attackerEntity = source.getAttacker();

                    if(attackerEntity instanceof LivingEntity attacker){
                        if(attacker.hasStatusEffect((StatusEffects.BLINDNESS))){
                            return 0.0F;
                        }
                        double size= Math.pow(target.getWidth(),2)*target.getHeight();
                        double attackerAcc = 0.025;
                        double targetAvd=Math.max(0.0, (0.3-size/5));
                        boolean attackerIsPlayer= false;
                        boolean targetIsPlayer = false;
                        if(attackerEntity instanceof PlayerEntity attackerPlayer){
                            attackerIsPlayer=true;
                            PlayerFinalStatComponent attackerComponent = PlayerFinalStatComponent.KEY.get(attackerPlayer);
                            attackerAcc=attackerComponent.getFinalStat(StatType.ACC);
                        }
                        if(target instanceof PlayerEntity targetPlayer){
                            targetIsPlayer=true;
                            PlayerFinalStatComponent targetComponent = PlayerFinalStatComponent.KEY.get(targetPlayer);
                            targetAvd=targetComponent.getFinalStat(StatType.AVD);
                        }
                        ThreadLocalRandom random=ThreadLocalRandom.current();
                        if(random.nextDouble() <=  Math.max(0.0, targetAvd - attackerAcc)){
                            target.getWorld().playSound(
                                    null,
                                    target.getX(), target.getY(), target.getZ(),
                                    SoundEvents.BLOCK_FIRE_EXTINGUISH,
                                    SoundCategory.PLAYERS,
                                    3.6F,0.4F
                            );
                            ((ServerWorld) target.getWorld()).spawnParticles(
                                    ParticleTypes.END_ROD,
                                    target.getX(), target.getY(), target.getZ(),
                                    35, 0.75,2.4,0.75,0
                            );
                            if(targetIsPlayer){
                                ((PlayerEntity) target).sendMessage(Text.literal("공격 회피"),true);
                            }
                            if(attackerIsPlayer){
                                ((PlayerEntity) attacker).sendMessage(Text.literal("공격 빗나감"),true);
                            }
                            return 0.0F;

                        }
                        return amount;//회피 실패
                    }
                    else{
                        if(target instanceof PlayerEntity targetPlayer){
                                PlayerFinalStatComponent targetComponent = PlayerFinalStatComponent.KEY.get(targetPlayer);
                                double targetAvd=targetComponent.getFinalStat(StatType.AVD);
                                ThreadLocalRandom random=ThreadLocalRandom.current();
                                if(random.nextDouble() <=  Math.max(0.0, targetAvd)){
                                    target.getWorld().playSound(
                                            null,
                                            target.getX(), target.getY(), target.getZ(),
                                            SoundEvents.BLOCK_SMOKER_SMOKE,
                                            SoundCategory.PLAYERS,
                                            3.6F,0.4F
                                    );
                                    ((ServerWorld) target.getWorld()).spawnParticles(
                                            ParticleTypes.END_ROD,
                                            target.getX(), target.getY(), target.getZ(),
                                            25, 0.3,2.4,0.3,0
                                    );
                                    ((PlayerEntity) target).sendMessage(Text.literal("회피"), true);
                                    return 0.0F;//공격자가 없는 피해(화상, void, 용암 , 익사, 등)에 대한 회피
                                }
                            }

                        } return amount;//플레이어가 아닌 대상에 대한 공격자가 없는 피해는  회피를 계산하지 않음
                }));

    }
}
