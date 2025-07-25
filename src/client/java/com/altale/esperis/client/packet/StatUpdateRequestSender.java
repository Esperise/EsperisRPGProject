package com.altale.esperis.client.packet;

import com.altale.esperis.player_data.stat_data.StatType;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class StatUpdateRequestSender {
    public static final Identifier ID = new Identifier("esperis","stat_update_request");
    public static void sendStatUpdateRequest(){
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if(clientPlayer == null){ return;}
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(clientPlayer.getUuid());
        ClientPlayNetworking.send(ID, buf);
    }

}
