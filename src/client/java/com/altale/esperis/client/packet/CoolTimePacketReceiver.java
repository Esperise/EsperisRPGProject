package com.altale.esperis.client.packet;

import com.altale.esperis.client.cache.CoolTimeTextCache;
import com.altale.esperis.serverSide.packet.CoolTimeS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class CoolTimePacketReceiver {
    public static final Identifier COOL_TIME_ID = new Identifier("esperis", "cool_time");

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(COOL_TIME_ID, (client, handler, buf, responseSender) -> {
            String coolTimeText = buf.readString();
            client.execute(() -> {
                if(coolTimeText != null) {
                    CoolTimeTextCache.setCoolTimeTextList(coolTimeText);
                }
            });// client.execute end
        });// register end
    }
}
