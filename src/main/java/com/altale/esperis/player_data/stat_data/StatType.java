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
    SPD("속도"),
    CRIT("치명타확률"),//크확
    CRIT_DAMAGE("치명타데미지"),//크뎀
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
    public static StatType[] getNormalStatType(){
        return NORMAL_STATS.clone();
    }
}

