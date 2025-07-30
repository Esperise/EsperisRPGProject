package com.altale.esperis.client.item.toolTipManager;

public enum SkillType {
    RIGHT("우클릭"),
    SHIFT_RIGHT("쉬프트 우클릭"),
    PASSIVE("패시브");
    private final String displayName;
    SkillType(String skillType){
        this.displayName= skillType;
    }
    public String getDisplayName(SkillType skillType){ return displayName;}
}

