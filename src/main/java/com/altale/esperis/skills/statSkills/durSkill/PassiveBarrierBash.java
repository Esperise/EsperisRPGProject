package com.altale.esperis.skills.statSkills.durSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.buff.AbilityBuff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;


import java.util.List;

public class PassiveBarrierBash {
    public static void PassiveBarrierBash(ServerWorld world, ServerPlayerEntity player, float barrierAmount) {
        PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        float atk = (float) finalStatComponent.getFinalStat(StatType.ATK);

        Box box= player.getBoundingBox().expand(4.0f,1f, 4.0f);
        List<Entity> entities = player.getWorld().getOtherEntities(player, box);
        for(Entity entity : entities) {
            if(entity instanceof LivingEntity livingTarget){
                livingTarget.damage(player.getWorld().getDamageSources().playerAttack(player), (atk*0.8f) +  (barrierAmount * 0.3f));
                if(livingTarget instanceof PlayerEntity playerTarget){
                    AbilityBuff.giveBuff(playerTarget, SkillsId.DUR_150.getSkillName(), StatType.SPD,10,-15,0,1);
                }else{
                    livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10, 0));
                }
            }
        }
        world.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE,
                SoundCategory.PLAYERS,
                3.0f,
                0.5f
        );
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d pos = player.getPos();
            serverWorld.spawnParticles(ParticleTypes.EXPLOSION,
                    pos.x, pos.y, pos.z, 1, 0, 0.4, 0, 0.0);
        }


    }
}
