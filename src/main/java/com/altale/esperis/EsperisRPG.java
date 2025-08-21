package com.altale.esperis;
import com.altale.esperis.combat.AvdDamage;
import com.altale.esperis.combat.CalculateDamage;
import com.altale.esperis.commands.ModCommands;
import com.altale.esperis.items.ModItems;
import com.altale.esperis.player_data.equipmentStat.DetectPlayerEquipmentChange;
import com.altale.esperis.player_data.level_data.KillOtherEntityEXP;
import com.altale.esperis.player_data.level_data.PlayerCustomExpSystem;
import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.level_data.PlayerLevelComponentImp;
import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import com.altale.esperis.player_data.money_data.PlayerMoneyComponentImp;

import com.altale.esperis.player_data.skill_data.PlayerSkillComponent;
import com.altale.esperis.player_data.skill_data.PlayerSkillComponentImp;
import com.altale.esperis.player_data.skill_data.skillKeybind.SkillKeyBindingPacketReceiver;
import com.altale.esperis.player_data.stat_data.ApplyStat2Ability;
import com.altale.esperis.player_data.stat_data.StatComponents.*;
import com.altale.esperis.player_data.stat_data.StatManager;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.serverSide.Utilities.TickHandler;
import com.altale.esperis.serverSide.packet.EquipmentAdditionalStatRerollRequestSender;
import com.altale.esperis.serverSide.packet.ShowRerollGuiRequestReceiver;
import com.altale.esperis.serverSide.packet.StatAddRequestReceiver;
import com.altale.esperis.serverSide.packet.StatUpdateRequestReceiver;
import com.altale.esperis.skills.statSkills.durSkill.DashLandingHandler;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.buff.HealBuff;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import com.altale.esperis.skills.statSkills.lukStatSkill.DoubleStep;
import com.altale.esperis.skills.test1;
import com.altale.esperis.skills.coolTime.CoolTimeTickManager;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.Random;

public class EsperisRPG implements ModInitializer , EntityComponentInitializer {
    private static final Random random = new Random();
    public static final String MODID = "mymod";
    public static final Logger LOGGER = (Logger) LogManager.getLogger(MODID);
    @Override
    public void onInitialize() {
//        SwordDashHandler.register();
        DashLandingHandler.register();
        TickHandler.register();
        DotDamageVer2.register();
        KnockedAirborneVer2.register();
        DoubleStep.register();
        test1.register();
        CoolTimeTickManager.register();
        ModCommands.register();
        AbsorptionBuff.register();
        HealBuff.register();
        StatManager.register();
        AbilityBuff.register();

        //stat 실 적용

            ApplyStat2Ability.register();
        //

        PlayerCustomExpSystem.register();
        StatUpdateRequestReceiver.register();
        StatAddRequestReceiver.register();

        AvdDamage.register();
        CalculateDamage.register();

        //ITEM
        ModItems.registerAll();

        //exp
        KillOtherEntityEXP.register();

        //skill
        SkillKeyBindingPacketReceiver.register();

        //
        ShowRerollGuiRequestReceiver.register();
        EquipmentAdditionalStatRerollRequestSender.register();
        DetectPlayerEquipmentChange.register();

        //delayedTask
        DelayedTaskManager.register();

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
                PlayerEquipmentStatComponent.KEY,
                PlayerEquipmentStatComponentImp::new,
                RespawnCopyStrategy.ALWAYS_COPY
        );
        entityComponentFactoryRegistry.registerForPlayers(
                PlayerFinalStatComponent.KEY,
                PlayerFinalStatComponentImp::new,
                RespawnCopyStrategy.ALWAYS_COPY
        );
        entityComponentFactoryRegistry.registerForPlayers(
                PlayerSkillComponent.KEY,
                PlayerSkillComponentImp::new,
                RespawnCopyStrategy.ALWAYS_COPY
        );entityComponentFactoryRegistry.registerForPlayers(
                BaseAbilityComponent.KEY,
                BaseAbilityComponentImp::new,
                RespawnCopyStrategy.ALWAYS_COPY
        );
    }
}
