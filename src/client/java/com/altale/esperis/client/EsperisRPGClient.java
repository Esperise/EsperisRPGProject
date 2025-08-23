package com.altale.esperis.client;
import com.altale.esperis.client.SkillKeyBinding.SkillKeyBinding;
import com.altale.esperis.client.cache.AbsorptionCacheCleaner;
import com.altale.esperis.client.HUD.*;
import com.altale.esperis.client.item.tooltip.*;
import com.altale.esperis.client.packet.AbsorptionSyncReceiver;
import com.altale.esperis.client.packet.CoolTimePacketReceiver;
import com.altale.esperis.client.screen.InventoryStatTest;
import com.altale.esperis.client.screen.RerollAdditionalStatScreen;
import com.altale.esperis.player_data.equipmentStat.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

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

        PotionTooltip.registerTooltip();
        SpecialBow1Tooltip.registerTooltip();
        GenericItemTooltip.register();
        TomoriTooltip.registerTooltip();
        ExpCouponTooltip.registerTooltip();
        //gui
        HandledScreens.register(ModScreenHandlers.ADDITIONAL_STAT_MAKER_SCREEN_HANDLER , RerollAdditionalStatScreen::new);
        //skillKeyBinding

        SkillKeyBinding.register();
        //버프 시간
        CurrentBuffHUD.register();

        //반드시 맨 아래 두기!
        CurrentTimeHUD.register();
    }
}
