package com.altale.esperis.skillTest1;
import com.altale.esperis.skillTest1.KnockedAirborne;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.mixin.event.lifecycle.LivingEntityMixin;
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
import net.minecraft.text.Text;
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
                                living.damage(source, 0.0f);
                                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 70,2));
                                Vec3d currentVelocity = entity.getVelocity();
//                                entity.setVelocity(currentVelocity.x, 1.0f, currentVelocity.z);
//                                entity.velocityModified = true;
                                KnockedAirborne.giveKnockedAirborne(entity);
                                player.sendMessage(net.minecraft.text.Text.literal("적 적중시 보호막 획득 및 체력 회복"), true);
                                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 0));
                                player.heal(0.5f);
                            }
                        }
                    player.getWorld().playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
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
