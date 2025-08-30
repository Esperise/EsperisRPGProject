package com.altale.esperis.player_data.skill_data;

import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.statSkills.dexStatSkill.DexJump;
import com.altale.esperis.skills.statSkills.dexStatSkill.FastAccurateAdvanced;
import com.altale.esperis.skills.statSkills.dexStatSkill.Snipe;
import com.altale.esperis.skills.statSkills.dexStatSkill.TripleShot;
import com.altale.esperis.skills.statSkills.durSkill.*;
import com.altale.esperis.skills.statSkills.lukStatSkill.*;
import com.altale.esperis.skills.statSkills.strSkill.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class SkillManager {
    //SkillsId 등록,


    public static void executeSkill(ServerPlayerEntity player , SkillsId skillId){
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
        switch(skillId){
            case STR_1 -> StrJump.strJump(player,world);
            case STR_25 -> HorizenSweep.horizenSweep(player, world );
            case STR_75 -> WindSlash.windSlash(player, world );
            case STR_125 -> GrandStarfall.grandStarfall(player, world );
            case STR_175 -> LastBreath.lastBreath(player, world );
        }
    }
    public static void dexSkill(ServerPlayerEntity player ,SkillsId skillId){
        String skillName= skillId.getSkillName();
        ServerWorld world= player.getServerWorld();
        switch(skillId){
            case DEX_1 -> DexJump.dexJump(player, world);
            case DEX_25 -> TripleShot.tripleShot(player, world);
            case DEX_75 -> FastAccurateAdvanced.fastAccurateAdvanced(player, world);
            case DEX_125 -> Snipe.snipe(player, world);
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
        switch(skillId){
            case DUR_1 -> DurJump.durJump(player, world);
            case DUR_25 -> GroundSlam.GroundSlam(player, world);
            case DUR_75 -> PathMaker.pathMaker(player, world);
            case DUR_125 -> AbsoluteZero.earthQuake(player, world);
            case DUR_175 -> AllOutAttack.AllOutAttack(player, world);
        }
    }
}
