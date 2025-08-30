package com.altale.esperis.client;
import com.altale.esperis.client.SkillKeyBinding.SkillKeyBinding;
import com.altale.esperis.client.cache.AbsorptionCacheCleaner;
import com.altale.esperis.client.HUD.*;
import com.altale.esperis.client.item.tooltip.*;
import com.altale.esperis.client.packet.AbsorptionSyncReceiver;
import com.altale.esperis.client.packet.CoolTimePacketReceiver;
import com.altale.esperis.client.packet.CurrentBuffS2CPacketReceiver;
import com.altale.esperis.client.packet.ScopePacketReceiver;
import com.altale.esperis.client.screen.InventoryStatTest;
import com.altale.esperis.client.screen.RerollAdditionalStatScreen;
import com.altale.esperis.screenHandlers.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class EsperisRPGClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        //버프 패킷 수신
        CurrentBuffS2CPacketReceiver.register();
        ScopePacketReceiver.register();
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
        Scope.register();
        PotionTooltip.registerTooltip();
        GenericItemTooltip.register();
        SpecialBow1Tooltip.registerTooltip();
        TomoriTooltip.registerTooltip();
        ExpCouponTooltip.registerTooltip();
        ShopItemTooltip.registerTooltip();
        //gui
        HandledScreens.register(ModScreenHandlers.ADDITIONAL_STAT_MAKER , RerollAdditionalStatScreen::new);
        //skillKeyBinding

        SkillKeyBinding.register();
        //버프 시간
        CurrentBuffHUD.register();

        //반드시 맨 아래 두기!
        CurrentTimeHUD.register();
    }
}
