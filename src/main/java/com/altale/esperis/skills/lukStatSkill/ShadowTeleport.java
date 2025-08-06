package com.altale.esperis.skills.lukStatSkill;

import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAt;

import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class ShadowTeleport {
    public static void doShadowTeleportPlayer(ServerPlayerEntity player, ServerWorld serverWorld) {
        if(CoolTimeManager.isOnCoolTime(player, "그림자이동")) {
            CoolTimeManager.showRemainCoolTime(player, "그림자이동");
        }else{
            PlayerFinalStatComponent playerFinalStatComponent= PlayerFinalStatComponent.KEY.get(player);
            double atk= playerFinalStatComponent.getFinalStat(StatType.ATK);
            CoolTimeManager.setCoolTime(player,"그림자이동",500);
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
                    CoolTimeManager.specificCoolTimePercentReduction(player, "그림자이동",20);
                    player.heal(5 + (float) (atk * 1.2));
                }
                else{
                    AbsorptionBuff.giveAbsorptionBuff(serverWorld, player,"그림자이동",15+ player.getMaxHealth()/10,40);
                }
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY,  60,2));
                
            }
            else{
                CoolTimeManager.specificCoolTimePercentReduction(player, "그림자이동",50);
                AbsorptionBuff.giveAbsorptionBuff(serverWorld, player,"그림자이동",5+ player.getMaxHealth()/10,20);
            }
        }
    }
}
