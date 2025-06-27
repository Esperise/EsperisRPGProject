package com.altale.esperis.serverSide.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class CoolTimeS2CPacket {
    public static final Identifier COOL_TIME_ID = new Identifier("esperis", "cool_time");
    public static void send(ServerPlayerEntity player, String coolTimeText) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(coolTimeText);
        ServerPlayNetworking.send(player,COOL_TIME_ID,buf);

    }
}
