package com.altale.esperis.items.SkillsTooltip;

public enum SkillType {
    Active_Skill("액티브"),
    Passive_Skill("패시브");
    private final String displayName;
    SkillType(String displayName){ this.displayName = displayName;}
    public String getDisplayName() { return displayName; }
}
