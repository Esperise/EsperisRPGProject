package com.altale.esperis.player_data.skill_data;

import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.statSkills.dexStatSkill.DexJump;
import com.altale.esperis.skills.statSkills.lukStatSkill.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class SkillManager {
    //SkillsId 등록,


    public static void excuteSkill(ServerPlayerEntity player , SkillsId skillId){

        String skillName= skillId.getSkillName();
        StatType skillStatType= skillId.getSkillStatType();
        switch(skillStatType.name()){
            case "STR" -> strSkill(player,skillId);
            case "DEX" -> dexSkill(player,skillId);
            case "LUK" -> lukSkill(player,skillId);
            case "DUR" -> durSkill(player,skillId);
            default -> System.out.println("Invalid skill stat type");
        }
    }
    public static void strSkill(ServerPlayerEntity player ,SkillsId skillId){
        String skillName= skillId.getSkillName();
        ServerWorld world= player.getServerWorld();
        switch(skillName){

        }
    }
    public static void dexSkill(ServerPlayerEntity player ,SkillsId skillId){
        String skillName= skillId.getSkillName();
        ServerWorld world= player.getServerWorld();
        switch(skillName){
            case "dex_jump" -> DexJump.dexJump(player, world);
        }
    }
    public static void lukSkill(ServerPlayerEntity player ,SkillsId skillId){
        String skillName= skillId.getSkillName();
        ServerWorld world= player.getServerWorld();
        switch(skillId){
            case LUK_1 -> TripleJump.tripleJump(player, world);
            case LUK_25 -> DoubleStep.doubleStep(player, world);
            case LUK_75 -> ShadowTeleport.doShadowTeleportPlayer(player, world);
            case LUK_125 -> FatalBlitz.doFatalBlitz(player, world);
            case LUK_175 -> ReadyToDie.doReadyToDie(player, world);
        }
    }
    public static void durSkill(ServerPlayerEntity player ,SkillsId skillId){
        String skillName= skillId.getSkillName();
        ServerWorld world= player.getServerWorld();
        switch(skillName){

        }
    }
}
