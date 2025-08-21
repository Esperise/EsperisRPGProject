package com.altale.esperis.serverSide.Utilities;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

@FunctionalInterface
    public interface ArcPointEmitter {
        /**
         * @param w     ServerWorld
         * @param point 호의 '현재 선두 지점' (y 포함)
         * @param outwardDirXZ XZ 평면에서 중심→point 방향(단위벡터)
         */
        void emit(ServerWorld w, Vec3d point, Vec3d outwardDirXZ);
    }

