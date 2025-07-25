package com.altale.esperis.player_data.equipmentStat;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    public static final ScreenHandlerType<AdditionalStatMaker> ADDITIONAL_STAT_MAKER_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                            new Identifier("esperis","additional_stat_maker"),
                            new ScreenHandlerType<>((AdditionalStatMaker::new),
                                    FeatureFlags.VANILLA_FEATURES)
            );
    private ModScreenHandlers() {}
}
