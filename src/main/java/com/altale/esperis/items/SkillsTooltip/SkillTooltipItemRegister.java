package com.altale.esperis.items.SkillsTooltip;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.statSkills.strSkill.StrJump;
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
            StrJump.cooltime,
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
