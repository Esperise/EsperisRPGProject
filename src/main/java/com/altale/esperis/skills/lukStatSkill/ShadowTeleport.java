package com.altale.esperis.skills.lukStatSkill;

import com.altale.esperis.serverSide.GetEntityLookingAt;

import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import com.altale.esperis.skills.debuff.DotTypeVer2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class ShadowTeleport {
    public static void doShadowTeleportPlayer(ServerPlayerEntity player, ServerWorld serverWorld) {
        if(CoolTimeManager.isOnCoolTime(player, "shadowTeleport")) {
            CoolTimeManager.showRemainCoolTime(player, "shadowTeleport");
        }else{
            CoolTimeManager.setCoolTime(player,"shadowTeleport",500);
            Entity target =GetEntityLookingAt.getEntityLookingAt(player, 17.0f,0.3);
            if(target instanceof LivingEntity){
                Vec3d playerLookVec= player.getRotationVec(1.0f);
                Vec3d playerCameraPos= player.getCameraPosVec(1.0f);
                Vec3d end= playerCameraPos.add(playerLookVec.multiply(17.0f));
//                for(double i=0; i<=15.0f;i+=0.05){
//                    Vec3d pos = playerCameraPos.add(playerLookVec.multiply(i));
//                    serverWorld.spawnParticles(new DustParticleEffect(new Vector3f(0.0f, 0.0f, 0.0f),0.6f), pos.x, pos.y, pos.z, 25, 1.0, 1.0, .10, 0);
//                }
                serverWorld.spawnParticles(ParticleTypes.WITCH, playerCameraPos.x,playerCameraPos.y, playerCameraPos.z, 1500, 0.75, 1.0, 0.75, 0);
                ((LivingEntity) target).addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS,  20,1));
                serverWorld.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR,
                        SoundCategory.PLAYERS,
                        15.0f,
                        0.8f
                );
                Vec3d targetLookVec= target.getRotationVec(1.0f);
                Vec3d oppositeLookVec= targetLookVec.multiply(-1.0f);
                player.teleport(serverWorld
                        , target.getX()+(1* oppositeLookVec.x)
                        , target.getY()
                        , target.getZ()+((1* oppositeLookVec.z))
                        , target.getYaw()
                        , target.getPitch());
                if(DotDamageVer2.isDotDamage((LivingEntity) target)){
                    DotDamageVer2.instantDotDamage((LivingEntity) target,player,0.2);
                    CoolTimeManager.specificCoolTimePercentReduction(player, "shadowTeleport",20);
                }
                else{
                    AbsorptionBuff.giveAbsorptionBuff(serverWorld, player,"shadowTeleport",15,30);
                }
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY,  40,2));
            }
            else{
                CoolTimeManager.specificCoolTimePercentReduction(player, "shadowTeleport",20);
            }
        }
    }
}
