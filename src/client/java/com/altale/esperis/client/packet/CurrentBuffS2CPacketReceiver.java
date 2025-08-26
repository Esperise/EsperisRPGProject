package com.altale.esperis.client.packet;

import com.altale.esperis.client.cache.CurrentBuffsCache;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CurrentBuffS2CPacketReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(CurrentBuffS2CPacketReceiver.class);
    public static final Identifier ID = new Identifier("esperis", "current_buffs");
    public static final Identifier BUFF_ID = new Identifier("esperis", "current_buffs");
    public static void register(){
        ClientPlayNetworking.registerGlobalReceiver(BUFF_ID, ((minecraftClient, clientPlayNetworkHandler, buf, packetSender) -> {
            if(buf==null) return;
            Map<String, Map<Integer, Integer>> abilityBuffs = buf.readMap(
                    java.util.HashMap::new,
                    PacketByteBuf::readString,
                    b -> b.readMap(java.util.HashMap::new, PacketByteBuf::readVarInt, PacketByteBuf::readVarInt)
            );

            Map<String, Double> healBuffs = buf.readMap(
                    java.util.HashMap::new,
                    PacketByteBuf::readString, PacketByteBuf::readDouble
            );

            minecraftClient.execute(() -> {
                CurrentBuffsCache.setBuffsMap(abilityBuffs);
                CurrentBuffsCache.setHealBuffsMap(healBuffs);
            });
        }
                ));
    }
}
