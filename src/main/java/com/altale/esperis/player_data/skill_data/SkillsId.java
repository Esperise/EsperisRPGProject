package com.altale.esperis.player_data.skill_data;

import com.altale.esperis.player_data.stat_data.StatType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public enum SkillsId {

    STR_1("str1", StatType.STR, 20),








    LUK_1("트리플점프", StatType.LUK, 5),
    LUK_20("더블스텝",StatType.LUK, 20),
    LUK_60("그림자이동",StatType.LUK, 60);


    public static final SkillsId[] STR_SKILLS = {};
    public static final SkillsId[] DEX_SKILLS = {};
    public static final SkillsId[] LUK_SKILLS = {LUK_1, LUK_20,LUK_60};
    public static final SkillsId[] DUR_SKILLS = {};
    public static final SkillsId[] PASSIVE_SKILLS={};
    public static final SkillsId[] ACTIVE_SKILLS={};
    public static final SkillsId[] KEYDOWN_SKILLS={};


    private final String skillName;
    private final StatType skillStatType;
    private final int skillRequiredLevel;

    SkillsId(String string1, StatType statType, int i) {
        this.skillName = string1;
        this.skillStatType = statType;
        this.skillRequiredLevel = i;
    }
    public String getSkillName(){
        return skillName;
    }
    public StatType getSkillStatType(){
        return skillStatType;
    }
    public int getSkillRequiredLevel(){
        return skillRequiredLevel;
    }



    private static final Map<String, SkillsId> BY_NAME = new HashMap<>();

    static {
        for (SkillsId id : values()) {
            BY_NAME.put(id.skillName, id);
        }
    }//Enum이 처음 JVM에 로드될때 한번 실행됨


    public static SkillsId getSkillIdByName(String skillName) {
//        return BY_NAME.get(skillName);
        return Optional.ofNullable(BY_NAME.get(skillName))
                .orElseThrow(() -> new IllegalArgumentException("Invalid skillName: " + skillName));
    }

    public static SkillsId[] getStatTypeSkillsId(StatType statType) {
        switch (statType.name()) {
            case "STR" -> {
                return getStrSkills();
            }
            case "DEX" -> {
                return getDexSkills();
            }
            case "LUK" -> {
                return getLukSkills();
            }
            case "DUR" -> {
                return getDurSkills();
            }
            default -> {
                return new SkillsId[]{};
            }
        }
    }

    public static SkillsId[] getStrSkills(){
        return STR_SKILLS.clone();
    }
    public static SkillsId[] getDexSkills(){
        return DEX_SKILLS.clone();
    }
    public static SkillsId[] getLukSkills(){
        return LUK_SKILLS.clone();
    }
    public static SkillsId[] getDurSkills(){
        return DUR_SKILLS.clone();
    }
    public static Set<SkillsId> getPassiveSkills(){
        return Set.of(PASSIVE_SKILLS.clone());
    }
    public static Set<SkillsId> getActiveSkills(){
        return Set.of(ACTIVE_SKILLS.clone());
    }
    public static Set<SkillsId> getKeydownSkills(){
        return Set.of(KEYDOWN_SKILLS.clone());
    }

}
