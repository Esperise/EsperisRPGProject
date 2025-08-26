package com.altale.esperis.skills.statSkills.dexStatSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAt;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import com.altale.esperis.skills.visualEffect.DrawCircle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class DexJump {
    public static final String skillName = SkillsId.DEX_1.getSkillName();
    public static void dexJump(ServerPlayerEntity player, ServerWorld world) {
        long now = world.getTime();
        if(CoolTimeManager.isOnCoolTime(player, skillName)){

        }
        else{
            CoolTimeManager.setCoolTime(player, skillName, 80);
            doDexJump(player, world);
        }

    }
    private static void doDexJump(ServerPlayerEntity player, ServerWorld world) {
        LivingEntity targetEntity = (LivingEntity) GetEntityLookingAt.getEntityLookingAt(player,3.5F,0.4F);
        Vec3d look= player.getRotationVec(1.0f);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        double spd= playerFinalStatComponent.getFinalStat(StatType.SPD);
        if( targetEntity != null){
            CoolTimeManager.setCoolTime(player, skillName, 160);
            targetEntity.setVelocity(Vec3d.ZERO);
            double power = 0.4 * (1+(spd/2));
            Vec3d velocity= new Vec3d(look.x * power ,0.30,look.z* power);
            targetEntity.addVelocity(velocity.x, velocity.y, velocity.z);
            targetEntity.velocityModified = true;
            KnockedAirborneVer2.giveKnockedAirborneVer2(targetEntity, 4,2);//0.3초 에어본
            double playerPower = -1-((spd)/2);
            Vec3d playerVelocity = new Vec3d(look.x * playerPower, 0.45, look.z * playerPower);
            player.addVelocity(playerVelocity.x, playerVelocity.y, playerVelocity.z);
            player.velocityModified = true;
            DrawCircle.spawnCircle(player, world, 2.0, 2.0, 30, 0,1,0
                    ,0,0,0,0,1,0,1.0f,20);
        }
        else{
            double power= 1.3 * (1+(spd/2));
            Vec3d velocity = new Vec3d(look.x * power, Math.min(0.35 * (1+(spd/2)), look.y), look.z * power);
            player.addVelocity(velocity.x, velocity.y, velocity.z);
            player.velocityModified = true;
            if (player.getWorld() instanceof ServerWorld serverWorld){
                Vec3d pos = player.getPos();
                serverWorld.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                        pos.x, pos.y, pos.z, 20, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }
}
