package com.altale.esperis.player_data.skill_data;

import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.dexStatSkill.DexJump;
import com.altale.esperis.skills.lukStatSkill.DoubleStep;
import com.altale.esperis.skills.lukStatSkill.ShadowTeleport;
import com.altale.esperis.skills.lukStatSkill.TripleJump;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class SkillManager {
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
        switch(skillName){
            case "트리플점프" -> TripleJump.tripleJump(player, world);
            case "더블스텝" -> DoubleStep.doubleStep(player, world);
            case "그림자이동" -> ShadowTeleport.doShadowTeleportPlayer(player, world);

        }
    }
    public static void durSkill(ServerPlayerEntity player ,SkillsId skillId){
        String skillName= skillId.getSkillName();
        ServerWorld world= player.getServerWorld();
        switch(skillName){

        }
    }
}
