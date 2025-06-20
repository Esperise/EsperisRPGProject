package com.altale.esperis.serverSide.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AbsorptionSyncS2CPacket {
    public static final Identifier ID = new Identifier("esperis", "absorption_sync");
        public static void send(ServerPlayerEntity player, int entityId, float absorption) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entityId);
        buf.writeFloat(absorption);

        ServerPlayNetworking.send(player, ID, buf);
//            System.out.println(player);
//            System.out.println(ID);
//            System.out.println(buf);
    }
}
