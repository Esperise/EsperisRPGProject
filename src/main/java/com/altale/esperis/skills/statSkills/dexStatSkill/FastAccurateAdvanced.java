package com.altale.esperis.skills.statSkills.dexStatSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class FastAccurateAdvanced {
    public static final String skillName = SkillsId.DEX_75.getSkillName();
    public static final double spdBuff = 0.1;
    public static final double accuracyBuff = 0.02;
    public static final double critBuff = 0.04;
    public static final int maxStack = 3;
    public static final int duration = 1200;
    public static final int cooltime = 1200;
    public static final int hitCooltimeReduce = 60;

    public static void fastAccurateAdvanced(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, skillName)){

        }else{
            CoolTimeManager.setCoolTime(player,skillName ,cooltime);
            AbilityBuff.giveBuff(player, skillName, StatType.SPD ,duration , 0, spdBuff,maxStack);
            AbilityBuff.giveBuff(player, skillName, StatType.ACC ,duration , accuracyBuff, 0,maxStack);
            AbilityBuff.giveBuff(player, skillName, StatType.CRIT ,duration , 0, critBuff,maxStack);
        }
    }
}
