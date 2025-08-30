package com.altale.esperis.serverSide.packet;

import com.altale.esperis.screenHandlers.AdditionalStatMaker;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ShowRerollGuiRequestReceiver {
    public static final Identifier ID = new Identifier("esperis", "show_reroll_stat_gui");
    public static final Identifier ID2 = new Identifier("esperis", "additional_stat_maker");
    public static void register(){
        ServerPlayNetworking.registerGlobalReceiver(ID,(server, player, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            server.execute(() -> {
                ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(uuid);
                player.openHandledScreen(new NamedScreenHandlerFactory() {
                    @Override
                    public Text getDisplayName() {
                        return Text.literal("");
                    }

                    @Override
                    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                        return new AdditionalStatMaker(syncId,playerInventory);
                    }
                });
            });
        });
    }
}
