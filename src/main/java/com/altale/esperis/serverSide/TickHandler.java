package com.altale.esperis.serverSide;

import com.altale.esperis.serverSide.packet.AbsorptionSyncS2CPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;

public class TickHandler {
    private static final Map<ServerWorld, Integer> worldTickCounters= new HashMap<>();
    public static void register(){
        ServerTickEvents.END_WORLD_TICK.register(world -> {

            int count = worldTickCounters.getOrDefault(world, 0 )+1;
            if(count >= 10){
                count = 0;
                //0.5초 마다 서버에서 실행할 거 기술
                for (ServerPlayerEntity player :world.getPlayers()){
                    Entity targetEntity= GetEntityLookingAt.getEntityLookingAt(player, 17, 1.3);
                    if(targetEntity instanceof LivingEntity living){
                        float targetEntityAbsorption= living.getAbsorptionAmount();
                        AbsorptionSyncS2CPacket.send(player, living.getId(), targetEntityAbsorption);
                    }
                }

            }
            worldTickCounters.put(world, count);
        });
    }
}
