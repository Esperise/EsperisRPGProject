package com.altale.esperis.client.item.toolTipManager;

public enum TooltipStatType {
    ATK("⚔"),
    DEF("🛡"),
    MAX_HEALTH("♥"),
    STR("STR"),
    DEX("DEX"),
    LUK("LUK"),
    DUR("DUR"),
    SPD("👢"),
    ATTACK_SPEED("공격 속도"),
    CRIT("치명타확률"),
    CRIT_DAMAGE("치명타데미지"),
    FinalDamagePercent("최종데미지"),
    DefPenetrate("방어력관통"),
    ACC("명중률"),
    AVD("회피율");
    private final String toolTipStatDisplay;
    TooltipStatType(String tooltipStatTypeString) { this.toolTipStatDisplay = tooltipStatTypeString; }
    public String getToolTipStatDisplay() { return toolTipStatDisplay; }
}
