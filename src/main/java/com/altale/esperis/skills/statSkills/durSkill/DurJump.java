package com.altale.esperis.skills.statSkills.durSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAt;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import com.altale.esperis.skills.visualEffect.DrawCircle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class DurJump {
    public static final String skillName= SkillsId.DUR_1.getSkillName();
    public static final float baseBarrier= 2;
    public static final float barrierHPCoeffi = 0.03f;
    public static final float barrierAlloutAtkCoeffi = 0.6f;
    public static final int barrierDuration = 20;

    public static void durJump(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, skillName)){

        }
        else{
            CoolTimeManager.setCoolTime(player, skillName, 60);
            doDurJump(player, world);
        }

    }
    private static void doDurJump(ServerPlayerEntity player, ServerWorld world) {
        Vec3d look= player.getRotationVec(1.0f);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        float hp = (float) playerFinalStatComponent.getFinalStat(StatType.MAX_HEALTH);
        double spd= playerFinalStatComponent.getFinalStat(StatType.SPD);
        double power= 0.6+((1+spd)/2);
        Vec3d velocity = new Vec3d(look.x * power, Math.min(0.35+(power/4), look.y), look.z * power);
        float barrierAmount = baseBarrier+(hp * barrierHPCoeffi) ;
        if(AbilityBuff.hasBuff(player, SkillsId.DUR_175.getSkillName())){
            float atk = (float) playerFinalStatComponent.getFinalStat(StatType.ATK);
            barrierAmount= baseBarrier+ (atk * barrierAlloutAtkCoeffi);
            power= 1.5f;
            velocity = new Vec3d(look.x * power, Math.min(0.35+(power/2), look.y), look.z * power);
        }
        AbsorptionBuff.giveAbsorptionBuff((ServerWorld) player.getWorld(), player, skillName, barrierAmount ,barrierDuration);
        player.addVelocity(velocity.x, velocity.y, velocity.z);
        player.velocityModified = true;
        DrawCircle.spawnCircle(player, world, 1.5, 1.5, 50, 0,-1,0,
                0,0,0,0.6f,0.6f,0.6f,1.0f,50);
    }
}
