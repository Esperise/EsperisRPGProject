package com.altale.esperis.client.packet;

import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class StatAddRequestSender {
    public static final Identifier ID2 = new Identifier("esperis","stat_add_request");
    public static void sendAddStatRequest(StatType statType, int value){
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if(clientPlayer == null){ return;}
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(clientPlayer.getUuid());
        buf.writeEnumConstant(statType);
        buf.writeInt(value);
        System.out.println("UUID: " + clientPlayer.getUuid());
        System.out.println("전송: "+statType.name()+" "+ value);
        ClientPlayNetworking.send(ID2, buf);
    }
}
