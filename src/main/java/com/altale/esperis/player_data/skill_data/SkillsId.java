package com.altale.esperis.player_data.skill_data;

import com.altale.esperis.player_data.stat_data.StatType;

import java.util.*;

public enum SkillsId {

    STR_1("힘_더블점프", StatType.STR, 5),
    STR_25("수평 베기", StatType.STR, 25),
    STR_50("전투 몰입", StatType.STR, 50),
    STR_75("바람 가르기", StatType.STR, 75),
    STR_100("생명선", StatType.STR, 100),
    STR_125("유성", StatType.STR, 125),
    STR_150("죽음의 저항", StatType.STR, 150),
    STR_175("최후의 숨결", StatType.STR, 175 ),


    DEX_1("민첩: 더블점프", StatType.DEX, 5),
    DEX_25("트리플 샷", StatType.DEX, 25),
    DEX_50("거인 학살자", StatType.DEX, 50),
    //활 공격 적중시 1스택이 쌓임, 4스택일때 스택을 모두 잃고 6+ 대상 최대체력의 3%의 추가피해
    DEX_75("신속 정확 고급", StatType.DEX, 75),
    //버프스킬, 지속시간 20초 쿨타임 40초, 공격속도가 10%, 명중율이 2%, 치명타확률이 4%증가, 활 적중시 쿨타임이 1초 감소
    DEX_100("집중", StatType.DEX, 100),
    //활 적중시 8초동안 공격속도가 5%증가, 최대 15스택(75%)
    DEX_125("저격", StatType.DEX, 125),
    DEX_150("출혈상", StatType.DEX, 150),
    //패시브: 활 공격 적중시 1스택이 쌓이고 4스택일때 스택을 모두 잃고 2초동안 대상에게 대상 최대체력의 4%의 총 피해를 입히는 출혈 부여 중첩가능
    DEX_175("폭풍의 시", StatType.DEX, 175),


    LUK_1("트리플점프", StatType.LUK, 5),
    LUK_25("트리플 스텝",StatType.LUK, 25),
    LUK_50("치명적 회복",StatType.LUK, 50),
    LUK_75("그림자이동",StatType.LUK, 75),
    LUK_100("출혈 폭발",StatType.LUK, 100),
    LUK_125("페이탈블리츠",StatType.LUK, 125),
    LUK_150("다크사이트",StatType.LUK, 150),
    LUK_175("레디 투 다이",StatType.LUK, 175),


    DUR_1("내구: 더블점프", StatType.DUR, 5),
    DUR_25("지면 타격",StatType.DUR, 25),
    DUR_50("재생의 바람",StatType.DUR, 50),
    DUR_75("길을 여는 자",StatType.DUR, 75),
    DUR_100("방어 태세",StatType.DUR, 100),
    DUR_125("혹한의 타격",StatType.DUR, 125),
    DUR_150("보호막 타격",StatType.DUR, 150),
    DUR_175("총공세",StatType.DUR, 175);


    public static final SkillsId[] STR_SKILLS = {STR_1, STR_25, STR_50, STR_75,STR_100,STR_125,STR_150,STR_175};
    public static final SkillsId[] DEX_SKILLS = {DEX_1, DEX_25, DEX_50, DEX_75, DEX_100, DEX_125, DEX_150, DEX_175};
    public static final SkillsId[] LUK_SKILLS = {LUK_1, LUK_25,LUK_50,LUK_75,LUK_100, LUK_125,LUK_150,LUK_175};
    public static final SkillsId[] DUR_SKILLS = {DUR_1,DUR_25,DUR_50,DUR_75,DUR_100,DUR_125,DUR_150,DUR_175};
    public static final SkillsId[] PASSIVE_SKILLS={
            STR_50,
            STR_100,
            STR_150,
            DEX_50,
            DEX_100,
            DEX_150,
            LUK_50,
            LUK_100,
            LUK_150,
            DUR_50,
            DUR_100,
            DUR_150
    };
    public static final SkillsId[] ACTIVE_SKILLS={
            STR_1,
            STR_25,
            STR_75,
            STR_125,
            STR_175,
            DEX_1,
            DEX_25,
            DEX_75,
            DEX_125,
            DEX_175,
            LUK_1,
            LUK_25,
            LUK_75,
            LUK_125,
            LUK_175,
            DUR_1,
            DUR_25,
            DUR_75,
            DUR_125,
            DUR_175
    };
    public static final SkillsId[] KEYDOWN_SKILLS={};


    private final String skillName;
    private final StatType skillStatType;
    private final int skillRequiredStatAmount;

    SkillsId(String string1, StatType statType, int i) {
        this.skillName = string1;
        this.skillStatType = statType;
        this.skillRequiredStatAmount = i;
    }
    public String getSkillName(){
        return skillName;
    }
    public StatType getSkillStatType(){
        return skillStatType;
    }
    public int getSkillRequiredStatAmount(){
        return skillRequiredStatAmount;
    }



    private static final Map<String, SkillsId> BY_NAME = new HashMap<>();

    static {
        for (SkillsId id : values()) {
            BY_NAME.put(id.skillName, id);
        }
    }//Enum이 처음 JVM에 로드될때 한번 실행됨


    public static SkillsId getSkillIdByName(String skillName) {
        System.out.println("getSKillIdByName Map: "+BY_NAME);
        return BY_NAME.getOrDefault(skillName, null);
//        return Optional.ofNullable(BY_NAME.get(skillName))
//                .orElseThrow(() -> new IllegalArgumentException("Invalid skillName: " + skillName));
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
    public static String[] getStrSkillNames(){
        return  Arrays.stream(getStrSkills()).filter(SkillsId::isActiveSkill).map(SkillsId::getSkillName).toArray(String[]::new);
    }
    public static String[] getDexSkillNames(){
        return  Arrays.stream(getDexSkills()).filter(SkillsId::isActiveSkill).map(SkillsId::getSkillName).toArray(String[]::new);
    }
    public static String[] getLukSkillNames(){
        return  Arrays.stream(getLukSkills()).filter(SkillsId::isActiveSkill).map(SkillsId::getSkillName).toArray(String[]::new);
    }
    public static String[] getDurSkillNames(){
        return  Arrays.stream(getDurSkills()).filter(SkillsId::isActiveSkill).map(SkillsId::getSkillName).toArray(String[]::new);
    }
    public static boolean isActiveSkill(SkillsId skillId){
        return getActiveSkills().contains(skillId);
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
    public static List<SkillsId> getAllSkills(){
        List<SkillsId> skills = new ArrayList<>();
        skills.addAll(Arrays.asList(STR_SKILLS));
        skills.addAll(Arrays.asList(DEX_SKILLS));
        skills.addAll(Arrays.asList(LUK_SKILLS));
        skills.addAll(Arrays.asList(DUR_SKILLS));
        return  skills;
    }

}
