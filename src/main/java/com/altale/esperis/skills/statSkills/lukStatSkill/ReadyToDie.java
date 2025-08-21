package com.altale.esperis.skills.statSkills.lukStatSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.BaseAbilityComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;


public class ReadyToDie {
    public static void doReadyToDie(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, SkillsId.LUK_175.getSkillName())){

        }else{
            CoolTimeManager.setCoolTime(player, SkillsId.LUK_175.getSkillName(), 2400);
            BaseAbilityComponent baseAbilityComponent= BaseAbilityComponent.KEY.get(player);
            double avd= baseAbilityComponent.getBaseAbility(StatType.AVD);
            AbilityBuff.giveBuff(player,SkillsId.LUK_175.getSkillName(), StatType.AVD, 300, 0,avd*(-1),1 );
            AbilityBuff.giveBuff(player,SkillsId.LUK_175.getSkillName(), StatType.FinalDamagePercent, 300, 0, avd,1 );
            AbilityBuff.giveBuff(player,SkillsId.LUK_175.getSkillName(), StatType.CRIT, 300, 0, avd,1 );


            Runnable task= ()-> {
                if (player.getWorld() instanceof ServerWorld serverWorld) {
                    Vec3d pos = player.getPos();
                    serverWorld.spawnParticles(ParticleTypes.SOUL,
                            pos.x, pos.y, pos.z, 8, 0.5, 0.3, 0.5, 0.3);
                }
            };
            DelayedTaskManager.addTask(world, player, task, 5, SkillsId.LUK_175.getSkillName(),60);
        }
    }

}
