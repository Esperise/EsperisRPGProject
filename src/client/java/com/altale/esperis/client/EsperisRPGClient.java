package com.altale.esperis.client;
import com.altale.esperis.client.cache.AbsorptionCacheCleaner;
import com.altale.esperis.client.HUD.*;
import com.altale.esperis.client.cache.CoolTimeTextCache;
import com.altale.esperis.client.packet.AbsorptionSyncReceiver;
import com.altale.esperis.client.packet.CoolTimePacketReceiver;
import com.altale.esperis.serverSide.packet.CoolTimeS2CPacket;
import net.fabricmc.api.ClientModInitializer;

public class EsperisRPGClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CoolTimeHUD.register();
        LookingEntityHealthHUD.register();
        HealthBarOverlay.register();
        AbsorptionSyncReceiver.register();
        AbsorptionCacheCleaner.register();
        CoolTimePacketReceiver.register();


    }
}
