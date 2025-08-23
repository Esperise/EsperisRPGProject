package com.altale.esperis.items;

import com.altale.esperis.items.itemFunction.Artifact.Tomori;
import com.altale.esperis.items.itemFunction.ExpCoupon;
import com.altale.esperis.items.itemFunction.MoneyItem;
import com.altale.esperis.items.itemFunction.HealingPotion;
import com.altale.esperis.items.itemFunction.SpecialBowItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
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
    public static final Item RED_POTION = register("potions/red_potion", new HealingPotion( new FabricItemSettings(),7,0.02f,200, 10,200,"빨간 포션"));
    public static final Item GOLDEN_POTION = register("potions/golden_potion", new HealingPotion( new FabricItemSettings(),9,0.032f,200, 10,200,"금빛 포션"));
    public static final Item BLUE_POTION = register("potions/orange_potion", new HealingPotion( new FabricItemSettings(),13,0.038f,200, 10,200,"주황 포션"));
    public static final Item ORANGE_POTION = register("potions/blue_potion", new HealingPotion( new FabricItemSettings(),8,0.035f,60, 2,300,"파란 포션"));
    public static final Item PURPLE_POTION = register("potions/purple_potion", new HealingPotion( new FabricItemSettings(),12,0.045f,60, 2,300,"보라 포션"));
    public static final Item ELIXIR = register("potions/elixir", new HealingPotion( new FabricItemSettings(),50,0.05f,1, 1,200,"엘릭서"));
    public static final Item MONEY = register("money", new MoneyItem());
    public static final Item EXP_COUPON = register("exp_coupon/exp_coupon", new ExpCoupon());
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
