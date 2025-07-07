package com.altale.esperis.skillTest1;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.List;
public class DashLandingHandler {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (PlayerFallHandler.shouldIgnoreFall(player)) {
                    if (player.isOnGround()) {
                        // 파티클 효과
                        ((ServerWorld) player.getWorld()).spawnParticles(
                                ParticleTypes.SMALL_FLAME,
                                player.getX(), player.getY(), player.getZ(),
                                500, 5.0, 0.1, 5.0, 0.05
                        );

                        // 반경 3칸 내 다른 엔티티 피해
                        List<Entity> nearby = player.getWorld().getOtherEntities(player, player.getBoundingBox().expand(5));
                        for (Entity entity : nearby) {
                            if (entity instanceof LivingEntity living && entity != player) {
                                DamageSource source = ((ServerWorld) player.getWorld()).getDamageSources().playerAttack(player);
                                living.damage(source, 1.0f);
                                DotDamageVer2.instantDotDamage(living, player, 0);
                                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,  100,1));
//                                AbsorptionBuff.giveAbsorptionBuff((ServerWorld) living.getWorld(),living,"DashLanding",500,200);


                                Vec3d currentVelocity = entity.getVelocity();
                                KnockedAirborneVer2.giveKnockedAirborneVer2(entity,player,60,4);
//                                DotDamage.giveDotDamage(living, player, 40,2,0.1F);
//                                AbsorptionBuff.giveAbsorptionBuff((ServerWorld) player.getWorld(),player,"DashLanding",5,30);

                            }
                        }
                    player.getWorld().playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.BLOCK_ANVIL_PLACE,
                            SoundCategory.PLAYERS,
                            1.0f,
                            1.0f

                    );
                        PlayerFallHandler.consumeIgnoreFall(player);
                    }
                }
            }
        });
    }
}
