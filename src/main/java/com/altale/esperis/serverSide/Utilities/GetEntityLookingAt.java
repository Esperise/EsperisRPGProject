package com.altale.esperis.serverSide.Utilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;

public class GetEntityLookingAt {
    //TODO gpt가 만들어 준거 분석하기
    /**
     * 플레이어가 바라보는 위치에서, 블록에 가려지지 않고
     * 가장 가까운 LivingEntity를 반환합니다.
     */
    public static Entity getEntityLookingAt(ServerPlayerEntity player, double maxDistance, double width) {
        // 1) 플레이어 카메라 위치와 시선 방향 계산
        Vec3d cameraPos = player.getCameraPosVec(1.0F);
        Vec3d lookVec   = player.getRotationVec(1.0F);
        Vec3d reachPos  = cameraPos.add(lookVec.multiply(maxDistance));

        // 2) 블록 레이캐스트: 가장 먼저 맞는 블록까지의 거리
        RaycastContext blockCtx = new RaycastContext(
                cameraPos,
                reachPos,
                RaycastContext.ShapeType.COLLIDER,  // 블록 경계 기준
                RaycastContext.FluidHandling.NONE,
                player
        );
        BlockHitResult blockHit = player.getWorld().raycast(blockCtx);
        double blockDistance = blockHit.getType() == HitResult.Type.MISS
                ? maxDistance
                : blockHit.getPos().distanceTo(cameraPos);

        // 3) 시야 범위 AABB 내의 엔티티 목록
        Box box = player.getBoundingBox().stretch(lookVec.multiply(maxDistance));
        List<Entity> entities = player.getWorld()
                .getOtherEntities(player, box, e -> e instanceof LivingEntity && e.isAlive());

        // 4) 블록 거리 이내에서 가장 가까운 엔티티 찾기
        LivingEntity closestEntity = null;
        double closestDist = blockDistance;  // 블록까지의 거리보다 멀면 무시

        for (Entity e : entities) {
            Box    eBox = e.getBoundingBox().expand(width);
            Vec3d  hit  = eBox.raycast(cameraPos, reachPos).orElse(null);
            if (hit == null) continue;

            double dist = cameraPos.distanceTo(hit);
            if (dist < closestDist) {
                closestDist   = dist;
                closestEntity = (LivingEntity) e;
            }
        }

        return closestEntity;
    }
}
