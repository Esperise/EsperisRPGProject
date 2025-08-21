package com.altale.esperis.skillTest1;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import com.altale.esperis.skills.debuff.DotTypeVer2;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.damage.DamageSource;

public class SwordDashHandler {
    public static void register() {
//        UseItemCallback.EVENT.register((player, world, hand) -> {
//            // 서버에서만 처리
//            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
//                // 다이아몬드 검일 경우
//                if (player.getStackInHand(hand).getItem() == Items.DIAMOND_SWORD && !CoolTimeManager.isOnCoolTime((ServerPlayerEntity) player, "sword_dash")) {
//                    CoolTimeManager.setCoolTime((ServerPlayerEntity) player,"sword_dash",200);
//                    Vec3d look = player.getRotationVec(1.0f);
//                    double power = 2.0; // 이동 속도 (넉백 강도)
//
//                    // 수직 이동을 약간 제한
//                    Vec3d velocity = new Vec3d(look.x * power, Math.max(0.45, look.y), look.z * power);
//                    player.addVelocity(velocity.x, velocity.y, velocity.z);
//                    player.velocityModified = true;
//                                            ((ServerWorld) player.getWorld()).spawnParticles(
//                                ParticleTypes.SMOKE,
//                                player.getX(), player.getY(), player.getZ(),
//                                500, 3.0, 0.2, 3.0, 0.05
//                        );
//                    world.playSound(
//                            null,
//                            player.getX(),
//                            player.getY(),
//                            player.getZ(),
//                            SoundEvents.ENTITY_GENERIC_EXPLODE,
//                            SoundCategory.PLAYERS,
//                            1.0f,
//                            1.0f
//
//                    );
//                    for (Entity entity : world.getOtherEntities(player, player.getBoundingBox().expand(3.0))) {
//                        if (entity instanceof LivingEntity living && entity != player) {
//                            DamageSource source = ((ServerWorld) world).getDamageSources().playerAttack(player);
////                            living.damage(source, 4.0f); // 10 데미지
//                            DotDamageVer2.giveDotDamage(living, player, 200, 20, 25F, DotTypeVer2.Bleed,true,0.1f,"swordDash");
//
//
//                        }
//                    }
//                    PlayerFallHandler.enableIgnoreFall(player);
////                    player.sendMessage(net.minecraft.text.Text.literal("낙하 데미지 무시 1회 적용됨"), true);
//
//
//
//                    // 효과음이나 파티클 추가도 가능
//
//                    return TypedActionResult.success(player.getStackInHand(hand));
//                }
//                else if(CoolTimeManager.isOnCoolTime((ServerPlayerEntity) player, "sword_dash")) {
//                    CoolTimeManager.showRemainCoolTime((ServerPlayerEntity) player, "sword_dash");
//                }
//
//
//            }
//
//            return TypedActionResult.pass(player.getStackInHand(hand));
//        });
    }
}
