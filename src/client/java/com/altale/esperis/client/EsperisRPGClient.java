package com.altale.esperis.client;
import com.altale.esperis.client.cache.AbsorptionCache;
import com.altale.esperis.client.cache.AbsorptionCacheCleaner;
import com.altale.esperis.client.healthHUD.*;
import com.altale.esperis.client.packet.AbsorptionSyncReceiver;
import com.altale.esperis.serverSide.packet.AbsorptionSyncS2CPacket;
import net.fabricmc.api.ClientModInitializer;

public class EsperisRPGClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
//        HealthHUDOverlay.register();
        LookingEntityHealthHUD.register();
        HealthBarOverlay.register();
        AbsorptionSyncReceiver.register();
        AbsorptionCacheCleaner.register();
    }
}
