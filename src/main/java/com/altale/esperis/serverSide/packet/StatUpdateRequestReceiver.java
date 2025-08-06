package com.altale.esperis.serverSide.packet;

import com.altale.esperis.player_data.stat_data.StatComponents.PlayerPointStatComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerPointStatComponentImp;
import com.altale.esperis.player_data.stat_data.StatManager;
import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class StatUpdateRequestReceiver {
    public static final Identifier ID = new Identifier("esperis", "stat_update_request");

    public static void register(){
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            server.execute(() -> {
                ServerPlayerEntity playerTarget = server.getPlayerManager().getPlayer(uuid);
                if (playerTarget != null) {
                    StatManager.statUpdate(playerTarget);
                    
                }
            });
        });

    }
}
