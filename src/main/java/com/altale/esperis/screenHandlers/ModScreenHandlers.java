package com.altale.esperis.screenHandlers;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class ModScreenHandlers {
    public static ScreenHandlerType<AdditionalStatMaker> ADDITIONAL_STAT_MAKER;
//    public static ScreenHandlerType<ShopInventoryScreenHandler> SHOP_INVENTORY_SCREEN;

    public static void register() {
        ADDITIONAL_STAT_MAKER = Registry.register(
                Registries.SCREEN_HANDLER,
                new Identifier("esperis", "additional_stat_maker"),
                new ScreenHandlerType<>(AdditionalStatMaker::new, FeatureFlags.VANILLA_FEATURES)
        );
//        SHOP_INVENTORY_SCREEN= Registry.register(
//                Registries.SCREEN_HANDLER,
//                new Identifier("esperis", "shop_inventory_screen"),
//                new ScreenHandlerType<>(ShopInventoryScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
//        );

    }

    private ModScreenHandlers() {}
}
