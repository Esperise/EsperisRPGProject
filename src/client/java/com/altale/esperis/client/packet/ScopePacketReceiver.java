package com.altale.esperis.client.packet;

import com.altale.esperis.client.cache.CurrentBuffsCache;
import com.altale.esperis.client.cache.UseScopeCache;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ScopePacketReceiver {
    public static final Identifier USE = new Identifier("esperis", "use_scope");
    public static void register(){
        ClientPlayNetworking.registerGlobalReceiver(USE, ((minecraftClient, clientPlayNetworkHandler, buf, packetSender) -> {
            if(buf==null) return;
            boolean useScope = buf.readBoolean();
            int scopeTime = buf.readInt();


            minecraftClient.execute(() -> {
                if(!useScope){
                    UseScopeCache.clear();
                }else{
                    UseScopeCache.setScopeInfo(useScope, scopeTime);
                }

            });
        }
        ));
    }
}
