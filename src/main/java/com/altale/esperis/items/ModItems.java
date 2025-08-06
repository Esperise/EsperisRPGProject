package com.altale.esperis.items;

import com.altale.esperis.items.itemFunction.Artifact.Tomori;
import com.altale.esperis.items.itemFunction.MoneyItem;
import com.altale.esperis.items.itemFunction.PotionInstantHeal;
import com.altale.esperis.items.itemFunction.SpecialBowItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModItems {
    public static final String MODID="esperis";
    public static final Map<String, Item> ITEM_MAP = new LinkedHashMap<>();
    public static final Item INSTANT_HEAL_POTION = register("instant_heal_potion", new PotionInstantHeal());
    public static final Item MONEY = register("money", new MoneyItem());
    public static final Item SPECIAL_BOW_1= register("special_bow_1", new SpecialBowItem());
    public static final Item TOMORI = register("tomori", new Tomori());
    public static Item register(String name, Item item){
        ITEM_MAP.put(name, item);
        return item;
    }
    public static void registerAll(){
        ITEM_MAP.forEach((name, item) -> Registry.register(Registries.ITEM, new Identifier(MODID, name), item));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> ITEM_MAP.values().forEach(item -> entries.add(item.getDefaultStack())));
    }
}
