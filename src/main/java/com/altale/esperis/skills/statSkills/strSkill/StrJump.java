package com.altale.esperis.skills.statSkills.strSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.visualEffect.DrawCircle;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class StrJump {
    public static final int buffDuration= 60;
    public static final double defBuff = 5;
    public static final double defPercentBuff = 8;
    public static final int cooltime = 80;
    public static final String skillName = SkillsId.STR_1.getSkillName();

    public static void strJump(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, skillName)){

        }
        else{
            CoolTimeManager.setCoolTime(player, skillName, cooltime);
            Vec3d look= player.getRotationVec(1.0f);
            PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
            double spd= playerFinalStatComponent.getFinalStat(StatType.SPD);
            double power= 0.9+((1+spd)/2);
            Vec3d velocity = new Vec3d(look.x * power, Math.min(0.4+(power/2), look.y), look.z * power);
            player.addVelocity(velocity.x, velocity.y, velocity.z);
            player.velocityModified = true;
            AbilityBuff.giveBuff(player,skillName, StatType.DEF, buffDuration, defPercentBuff, defBuff,1);
            DrawCircle.spawnCircle(player, world, 1.5, 1.5, 50, 0,-1,0,
                    0,0,0,0.8f,0.4f,0.4f,1.0f,50);
        }
        }

}
