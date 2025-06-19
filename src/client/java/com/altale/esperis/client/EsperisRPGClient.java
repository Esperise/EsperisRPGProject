package com.altale.esperis.client;
import com.altale.esperis.client.healthHUD.*;
import net.fabricmc.api.ClientModInitializer;

public class EsperisRPGClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HealthHUDOverlay.register();
        LookingEntityHealthHUD.register();
    }
}
