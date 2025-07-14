package com.altale.esperis;
import com.altale.esperis.commands.ModCommands;
import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.level_data.PlayerLevelComponentImp;
import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import com.altale.esperis.player_data.money_data.PlayerMoneyComponentImp;
import com.altale.esperis.player_data.stat_data.PlayerEquipmentStatComponent;
import com.altale.esperis.player_data.stat_data.PlayerEquipmentStatComponentImp;
import com.altale.esperis.player_data.stat_data.PlayerPointStatComponent;
import com.altale.esperis.player_data.stat_data.PlayerPointStatComponentImp;
import com.altale.esperis.serverSide.TickHandler;
import com.altale.esperis.skillTest1.DashLandingHandler;
import com.altale.esperis.skillTest1.SwordDashHandler;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import com.altale.esperis.skills.lukStatSkill.DoubleStep;
import com.altale.esperis.skills.test1;
import com.altale.esperis.skills.coolTime.CoolTimeTickManager;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.Random;

public class EsperisRPG implements ModInitializer , EntityComponentInitializer {
    private static final Random random = new Random();
    @Override
    public void onInitialize() {
        SwordDashHandler.register();
        DashLandingHandler.register();
        TickHandler.register();
        DotDamageVer2.register();
        KnockedAirborneVer2.register();
        DoubleStep.register();
        test1.register();
        CoolTimeTickManager.register();
        ModCommands.register();
        AbsorptionBuff.register();



        System.out.println("[EsperisRPG] 모드 초기화 완료!");
    }
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerForPlayers(
                PlayerMoneyComponent.KEY,
                PlayerMoneyComponentImp::new,
                RespawnCopyStrategy.ALWAYS_COPY
        );
        entityComponentFactoryRegistry.registerForPlayers(
                PlayerLevelComponent.KEY,
                PlayerLevelComponentImp::new,
                RespawnCopyStrategy.ALWAYS_COPY
        );
        entityComponentFactoryRegistry.registerForPlayers(
                PlayerPointStatComponent.KEY,
                PlayerPointStatComponentImp::new,
                RespawnCopyStrategy.ALWAYS_COPY

        );
        entityComponentFactoryRegistry.registerForPlayers(
                PlayerPointStatComponent.KEY,
                PlayerPointStatComponentImp::new,
                RespawnCopyStrategy.ALWAYS_COPY
        );
        entityComponentFactoryRegistry.registerForPlayers(
                PlayerEquipmentStatComponent.KEY,
                PlayerEquipmentStatComponentImp::new,
                RespawnCopyStrategy.ALWAYS_COPY
        );
    }
}
