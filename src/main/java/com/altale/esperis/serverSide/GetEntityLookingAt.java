package com.altale.esperis.serverSide;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class GetEntityLookingAt {
    public static Entity getEntityLookingAt(ServerPlayerEntity player, double maxDistance, double width) {
        Vec3d playerCameraPos= player.getCameraPosVec(1.0F);
        Vec3d playerLookVec = player.getRotationVec(1.0F);
        Vec3d end= playerCameraPos.add(playerLookVec.multiply(maxDistance));
        Box box = player.getBoundingBox().stretch(playerLookVec.multiply(maxDistance));
        List<Entity> entities = player.getWorld().getOtherEntities(player, box, (entity) -> {
            return  entity instanceof LivingEntity && entity.isAlive();
        });
        LivingEntity closestEntity = null;
            double closestDist = maxDistance;

            for (Entity entity : entities) {
                Box entityBox = entity.getBoundingBox().expand(width);
                Vec3d intersection = entityBox.raycast(playerCameraPos, end).orElse(null);

                if (intersection != null) {
                    double dist = playerCameraPos.distanceTo(intersection);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closestEntity = (LivingEntity) entity;
                    }
                }
            }
            return closestEntity;
    }
}
