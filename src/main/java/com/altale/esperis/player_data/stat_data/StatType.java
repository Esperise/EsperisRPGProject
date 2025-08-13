package com.altale.esperis.player_data.stat_data;

public enum StatType {
    //순서 변경하면 안됨!!!!!!!!!!!!!!!!!!!
    ATK("공격력"),//공격력
    DEF("방어력"),//방어력
    MAX_HEALTH("체력"),
    STR("STR"),
    DEX("DEX"),
    LUK("LUK"),
    DUR("DUR"),
    SPD("이동속도"),
    ATTACK_SPEED("공격속도"),
    CRIT("치명타확률"),//크확
    CRIT_DAMAGE("치명타데미지"),//크뎀
    FinalDamagePercent("최종데미지"),
    DefPenetrate("방어력관통"),
    ACC("명중률"),//명중률
    AVD("회피율");//회피율
    private final String displayName;
    StatType(String displayName){
        this.displayName = displayName;
    }
    public String getDisplayName(){
        return displayName;
    }
    private static final StatType[] NORMAL_STATS = { STR, DEX, LUK, DUR };
    private static final StatType[] SPECIAL_STATS={SPD,ATTACK_SPEED,CRIT,CRIT_DAMAGE,ACC,AVD,DefPenetrate,FinalDamagePercent};
    private static final StatType[] PERCENT_STATS={CRIT,CRIT_DAMAGE,ACC,AVD,DefPenetrate,FinalDamagePercent};
    private static final StatType[] SPEEDS_STATS = {SPD, ATTACK_SPEED};
    private static final StatType[] BASIC_STATS = {ATK, DEF, MAX_HEALTH};
    private static final StatType[] ARMOR_STATS = {DEF, MAX_HEALTH , STR, DEX, LUK, DUR};
    private static final StatType[] WEAPON_STATS = {ATK, STR, DEX, LUK, DUR};
    private static final StatType[] NONE_SPECIAL_STATS = {ATK, DEF, MAX_HEALTH , STR, DEX, LUK, DUR};
    private static final StatType[] Should_Have_Caps={CRIT, AVD, FinalDamagePercent, ACC, DefPenetrate};


    public static StatType[] getNormalStatType(){
        return NORMAL_STATS.clone();
    }
    public static StatType[] getSpecialStatType(){return SPECIAL_STATS.clone();}
    public static StatType[] getBasicStatType(){return BASIC_STATS.clone();}
    public static StatType[] getArmorStatType(){return ARMOR_STATS.clone();}
    public static StatType[] getWeaponStats(){return WEAPON_STATS.clone();}
    public static StatType[] getNoneSpecialStats(){return NONE_SPECIAL_STATS.clone();}
    public static StatType[] getPercentStats(){ return PERCENT_STATS.clone(); }
    public static StatType[] getSpeedsStats(){ return SPEEDS_STATS.clone(); }
    public static StatType[] getCapsStats(){ return Should_Have_Caps.clone(); }
}

