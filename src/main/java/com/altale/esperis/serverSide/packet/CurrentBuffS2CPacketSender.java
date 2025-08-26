package com.altale.esperis.serverSide.packet;

import com.altale.esperis.skills.buff.HealBuff;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class CurrentBuffS2CPacketSender {
    private static final Logger LOG = LoggerFactory.getLogger(CurrentBuffS2CPacketSender.class);
    public static final Identifier BUFF_ID = new Identifier("esperis", "current_buffs");
    public static void send(ServerPlayerEntity player, Map<String, Map<Integer, Integer>> buffMap
                            , Map<String, Double> healBuffMap) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeMap(buffMap,
                PacketByteBuf::writeString,
                (b, inner) ->
                        b.writeMap(inner,
                                PacketByteBuf::writeVarInt, PacketByteBuf::writeVarInt
                )
        );
        buf.writeMap(healBuffMap,
                PacketByteBuf::writeString, PacketByteBuf::writeDouble);
        ServerPlayNetworking.send(player,BUFF_ID,buf);

    }
}
