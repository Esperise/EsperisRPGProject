package com.altale.esperis;
import com.altale.esperis.serverSide.TickHandler;
import com.altale.esperis.serverSide.packet.CoolTimeS2CPacket;
import com.altale.esperis.skillTest1.DashLandingHandler;
import com.altale.esperis.skillTest1.SwordDashHandler;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import com.altale.esperis.skills.lukStatSkill.DobleStep;
import com.altale.esperis.skills.test1;
import com.altale.esperis.skills.coolTime.CoolTimeTickManager;
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

public class EsperisRPG implements ModInitializer {
    private static final Random random = new Random();
    @Override
    public void onInitialize() {
        SwordDashHandler.register();
        DashLandingHandler.register();
        TickHandler.register();
        DotDamageVer2.register();
        KnockedAirborneVer2.register();
        DobleStep.register();
        test1.register();
        CoolTimeTickManager.register();



        System.out.println("[EsperisRPG] 모드 초기화 완료!");
        System.out.println("[EsperisRPG] 모드 초기화 완료!");
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            Block brokenBlock = state.getBlock();
            if(brokenBlock == Blocks.DIRT || brokenBlock == Blocks.GRASS_BLOCK) {
                if (random.nextFloat() < 0.10f) {
                    if (player instanceof ServerPlayerEntity serverPlayer){
                        serverPlayer.giveItemStack(new ItemStack(Items.DIAMOND));

                        serverPlayer.sendMessage(Text.of("💎 다이아 획득!"), false);
                    }
                    if(player instanceof ServerPlayerEntity serverPlayer){
                        serverPlayer.giveItemStack(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
                        serverPlayer.sendMessage(Text.of("황금사과 획득"), false);

                        world.playSound(
                            null,
                            serverPlayer.getBlockPos(),
                            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                            SoundCategory.PLAYERS,
                            1.0F,
                            1.0F
    );
                    }
                }


            }
        });
    }
}
