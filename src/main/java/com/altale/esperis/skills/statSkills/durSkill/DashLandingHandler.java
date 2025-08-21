package com.altale.esperis.skills.statSkills.durSkill;
import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.serverSide.Utilities.ParticleHelper;
import com.altale.esperis.skillTest1.PlayerFallHandler;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.IntConsumer;

public class DashLandingHandler {
    public static final float baseDamage= 10;
    public static final float hpCoeffi = 0.05f;
    public static final float defCoeffi = 0.08f;
    public static final float alloutAtkCoeffi = 2.45f;
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (PlayerFallHandler.earthQuakeFall(player)) {
                    if (player.isOnGround()) {
                        DelayedTaskManager.deleteTask(player.getServerWorld(), player,SkillsId.STR_75.getSkillName());
                        // 파티클 효과
                        PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(player);
                        float atk = (float) finalStatComponent.getFinalStat(StatType.ATK);
                        float hp = (float) finalStatComponent.getFinalStat(StatType.MAX_HEALTH);
                        float def = (float) finalStatComponent.getFinalStat(StatType.DEF);
                        float damage = baseDamage+ ( hp*hpCoeffi + def*defCoeffi);
                        if(AbilityBuff.hasBuff(player, SkillsId.DUR_175.getSkillName())){
                            damage= baseDamage+ atk * alloutAtkCoeffi;
                        }
                        List<Entity> nearby = player.getWorld().getOtherEntities(player, player.getBoundingBox().expand(15));
                        for (Entity entity : nearby) {
                            if (entity instanceof LivingEntity living) {
                                DamageSource source = ((ServerWorld) player.getWorld()).getDamageSources().playerAttack(player);
                                float distance = player.distanceTo(entity);
                                float finaldamage = damage * Math.max(0.3f, (1.15f - (distance / 16 )) );
                                int airborneDuration = (int) Math.max(10 , Math.min( 4 * distance, 70) );
                                player.sendMessage(Text.literal(String.valueOf(finaldamage)).formatted(Formatting.RED, Formatting.BOLD), false);
                                living.damage(source, finaldamage);
                                KnockedAirborneVer2.giveKnockedAirborneVer2(living,airborneDuration,3);
                            }
                        }
                        IntConsumer action = ParticleHelper.expandingCircleXZ(
                                (ServerWorld) player.getWorld(), player,
                                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()),  // 원하는 파티클로 교체 가능
                                0.5, 0.72, 0.1, 180
                        );
                        Runnable task = ()->{
                            for(int i =0 ; i < 1; i++){
                                player.getWorld().playSound(
                                        null,
                                        player.getX(),
                                        player.getY(),
                                        player.getZ(),
                                        SoundEvents.ENTITY_GENERIC_EXPLODE,
                                        SoundCategory.PLAYERS,
                                        15.0f,
                                        1.0f
                                );
                            }
                        };
                        DelayedTaskManager.addTask(player.getServerWorld(),player, action, 1, "Earth Quake Effects", 20);
                        DelayedTaskManager.addTask(player.getServerWorld(),player, task, 1, "Earth Quake Sounds", 15);

                        PlayerFallHandler.consumeIgnoreFall(player);
                    }
                }
            }
        });
    }
}
