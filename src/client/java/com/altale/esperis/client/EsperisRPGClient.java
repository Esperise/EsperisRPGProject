package com.altale.esperis.client;
import com.altale.esperis.client.cache.AbsorptionCacheCleaner;
import com.altale.esperis.client.HUD.*;
import com.altale.esperis.client.cache.CoolTimeTextCache;
import com.altale.esperis.client.item.tooltip.InstHealPotionTooltip;
import com.altale.esperis.client.packet.AbsorptionSyncReceiver;
import com.altale.esperis.client.packet.CoolTimePacketReceiver;
import com.altale.esperis.client.screen.InventoryStatScreen;
import com.altale.esperis.client.screen.InventoryStatTest;
import com.altale.esperis.items.itemFunction.PotionInstantHeal;
import com.altale.esperis.serverSide.packet.CoolTimeS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class EsperisRPGClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CoolTimeHUD.register();
        HealthBarOverlay.register();
        AbsorptionSyncReceiver.register();
        AbsorptionCacheCleaner.register();
        CoolTimePacketReceiver.register();
        LevelHUD.register();
        LookingEntityHealthHUD.register();
        MoneyHUD.register();
//        InventoryScreenHUD.register();
        InventoryReceipeAdditionalButton.register();
        InventoryStatTest.register();

        InstHealPotionTooltip.registerTooltip();


        //반드시 맨 아래 두기!
        CurrentTimeHUD.register();
    }
}
