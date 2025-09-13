package com.altale.esperis.items.SkillsTooltip;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.statSkills.strSkill.HorizenSweep;
import com.altale.esperis.skills.statSkills.strSkill.StrJump;
import com.altale.esperis.skills.statSkills.strSkill.WindSlash;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SkillTooltipItemRegister {
    public static final String MODID = "esperis";
    public static final float emptyValue = 0f;
    public static final Map<StatType, Float> emptyMap = Collections.emptyMap();
    public static final Map<String, Item> MAP = new LinkedHashMap<String, Item>();

    public static final Item STR_1 = register("1", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.STR_1.getSkillName(),
                    StatType.STR,SkillsId.STR_1.getSkillRequiredStatAmount(),
            "전방으로 도약한다.",
            emptyValue, emptyMap,
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            StrJump.cooltime,0,0,
            ""
    ));
    public static final Item STR_25 = register("2", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.STR_25.getSkillName(),
                    StatType.STR,SkillsId.STR_25.getSkillRequiredStatAmount(),
            "전방으로 검을 휘둘러 범위 내의 모든 적에게 _damageFlag_ 의 피해를 입히고," +
                    "_lineBreak_ 적중시킨 대상 하나당 체력을 _healFlag_ 만큼 회복한다.",
            HorizenSweep.baseDamage, Map.of(StatType.ATK, HorizenSweep.atkCoeffi),
            emptyValue,emptyMap,
            HorizenSweep.baseHealAmount,Map.of(StatType.ATK, HorizenSweep.healAtkCoeffi),
            HorizenSweep.cooltime,HorizenSweep.cooltimeReduceCoeffi, 0,
            "공격 속도에 비례하여 시전 속도와 쿨타임이 최대 50%까지 감소한다."
    ));
    public static final Item STR_50 = register("3", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.STR_50.getSkillName(),
                    StatType.STR,SkillsId.STR_50.getSkillRequiredStatAmount(),
            "적에게 피해를 입히면 8초 동안 공격력이 _damageFlag_ (최소 0.1) 증가한다." +
                    "_lineBreak_ 이 효과는 최대 20회 중첩되며 최대 중첩 시 입히는 피해량의 13%를 회복한다.",
            0, Map.of(StatType.ATK, 0.01f),
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            -1,0,0,
            "중첩시 기본 공격력의 1% 와 0.1 중 더 큰 값이 적용된다."
    ));
    public static final Item STR_75 = register("4", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.STR_75.getSkillName(),
            StatType.STR,SkillsId.STR_75.getSkillRequiredStatAmount(),
            "검기를 일으키는 수평베기를 시전한다. _lineBreak_" +
                    "검기에 적중한 대상에게  _damageFlag_  의 피해를 입히고 _lineBreak_" +
                    "대상을 1초동안 공중에 띄운다.",
            6, Map.of(StatType.ATK, 0.85f,StatType.MAX_HEALTH, 0.06f),
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            WindSlash.cooltime,WindSlash.cooltimeReduceCoeffi,0,
            "공격 속도에 비례하여 쿨타임이 최대 50%까지 감소한다."
    ));
    public static final Item STR_100 = register("5", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.STR_100.getSkillName(),
            StatType.STR,SkillsId.STR_100.getSkillRequiredStatAmount(),
            "체력이 30% 이하가 되는 피해를 입을 시 _lineBreak_" +
                    "5초 동안 _barrierFlag_ 의 보호막을 얻으며 _lineBreak_" +
                    "5초에 걸쳐 체력을 _healFlag_ 를 회복한다.",
            emptyValue,emptyMap,
            0,Map.of(StatType.MAX_HEALTH, 0.25f),
            0,Map.of(StatType.MAX_HEALTH, 0.1f),
            1200,0,0,
            ""
    ));

    public static Item register(String name, Item item){
        MAP.put(name, item);
        return item;
    }
    public static void registerAll(){
        MAP.forEach((name, item) -> Registry.register(Registries.ITEM, new Identifier(MODID, name), item));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> MAP.values().forEach(item -> entries.add(item.getDefaultStack())));
    }
}
