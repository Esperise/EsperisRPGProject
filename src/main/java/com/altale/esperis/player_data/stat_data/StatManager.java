package com.altale.esperis.player_data.stat_data;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class StatManager {
    public static void register(){
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            Iterable<ServerWorld> worlds = server.getWorlds();
            for(ServerWorld world : worlds){
                List<ServerPlayerEntity> players= world.getPlayers();
                for(ServerPlayerEntity player : players){
                    if(world.getTime() %20 == 0){

                    }
                }

            }
        });
    }
}
