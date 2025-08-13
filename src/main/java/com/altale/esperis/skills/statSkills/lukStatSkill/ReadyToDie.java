package com.altale.esperis.skills.statSkills.lukStatSkill;

import com.altale.esperis.player_data.stat_data.StatComponents.BaseAbilityComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;


public class ReadyToDie {
    public static void doReadyToDie(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, "레디 투 다이")){

        }else{
            CoolTimeManager.setCoolTime(player, "Ready To Die", 2400);
            BaseAbilityComponent baseAbilityComponent= BaseAbilityComponent.KEY.get(player);
            double avd= baseAbilityComponent.getBaseAbility(StatType.AVD);
            AbilityBuff.giveBuff(player,"레디 투 다이", StatType.AVD, 300, 0,avd*(-1),1 );
            AbilityBuff.giveBuff(player,"레디 투 다이", StatType.FinalDamagePercent, 300, 0, avd,1 );
            AbilityBuff.giveBuff(player,"레디 투 다이", StatType.CRIT, 300, 0, avd,1 );
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                Vec3d pos = player.getPos();
                serverWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER,
                        pos.x, pos.y, pos.z, 10, 0.5, 0.8, 0.5, 0.3);
                serverWorld.spawnParticles(ParticleTypes.FLAME,
                        pos.x, pos.y, pos.z, 20, 1, 1, 1, 0.3);
            }
        }
    }

}
