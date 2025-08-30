package com.altale.esperis.skills.statSkills.durSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.serverSide.Utilities.ParticleHelper;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class AllOutAttack {
    public static final String skillName= SkillsId.DUR_175.getSkillName();
    public static final double reduceDefPercent= 60;
    public static final double reduceHPPercent= 35;
    public static final double atkBuffCoeffi = 0.05;
    public static final double allSpdBuffPercent = 50;
    public static final double defPenBuff= 0.2;
    public static final int buffDuration = 400;
    public static final int cooltime = 2400;
    public static final int cooltimeReduce = 200;
    public static void AllOutAttack(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, skillName)){

        }
        else{
            PlayerFinalStatComponent stat = PlayerFinalStatComponent.KEY.get(player);
            double hp = stat.getFinalStat(StatType.MAX_HEALTH);
            double def = stat.getFinalStat(StatType.DEF);
            double atkBuff = (hp*(reduceHPPercent/100) + def * (reduceDefPercent/100)) * atkBuffCoeffi;
            System.out.println( atkBuff);
            CoolTimeManager.allCoolTimeReduction(player, cooltimeReduce);
            CoolTimeManager.setCoolTime(player, skillName, cooltime);
            AbilityBuff.giveBuff(player, skillName, StatType.DEF,buffDuration,reduceDefPercent*(-1),0,1);
            AbilityBuff.giveBuff(player, skillName, StatType.MAX_HEALTH,buffDuration,reduceHPPercent*(-1),0,1);
            AbilityBuff.giveBuff(player, skillName, StatType.ATK,buffDuration,0 ,atkBuff,1);
            AbilityBuff.giveBuff(player, skillName, StatType.SPD,buffDuration,allSpdBuffPercent,0,  1);
            AbilityBuff.giveBuff(player, skillName, StatType.ATTACK_SPEED,buffDuration,allSpdBuffPercent,0,  1);
            AbilityBuff.giveBuff(player, skillName, StatType.DefPenetrate,buffDuration,0, defPenBuff, 1);
            Runnable task= ()->{
                if (player.getWorld() instanceof ServerWorld serverWorld) {
                    Vec3d pos = player.getPos();
                    serverWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER,
                            pos.x, pos.y, pos.z, 2, 0.5, 0.3, 0.5, 0);
                    serverWorld.spawnParticles(ParticleTypes.FLAME,
                            pos.x, pos.y, pos.z, 8, 0.5, 1, 0.5, 0);
                }
            };
            DelayedTaskManager.addTask(world, player, task, 5, skillName, 240/5);


        }
    }
}
