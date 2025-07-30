package com.altale.esperis.client.packet;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ShowRerollGuiRequestSender {
    public static final Identifier ID = new Identifier("esperis", "show_reroll_stat_gui");
    public static void sendShowRerollGuiReqeust(){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(player.getUuid());
        ClientPlayNetworking.send(ID, buf);
    }
}
