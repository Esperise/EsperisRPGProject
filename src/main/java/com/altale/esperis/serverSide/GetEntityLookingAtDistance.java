package com.altale.esperis.serverSide;


import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;


public class GetEntityLookingAtDistance {
    public static double getEntityLookingAtDistance(ServerPlayerEntity player , LivingEntity target) {
        double dx= player.getX() - target.getX();
        double dy= player.getY() - target.getY();
        double dz= player.getZ() - target.getZ();
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
}
