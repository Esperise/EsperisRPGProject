package com.altale.esperis.client.SkillTooltips;

public enum SkillTooltipType {
    Active_Skill("버프 스킬"),
    Passive_Skill("패시브 스킬");
    private final String displayName;
    SkillTooltipType(String displayName){ this.displayName = displayName;}
    public String getDisplayName() { return displayName; }
}
