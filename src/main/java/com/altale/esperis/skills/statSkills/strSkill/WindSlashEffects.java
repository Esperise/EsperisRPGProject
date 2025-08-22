package com.altale.esperis.skills.statSkills.strSkill;

import net.minecraft.server.world.ServerWorld;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
public class WindSlashEffects {

    public static final class SectorRingSweep {
        private SectorRingSweep(){}

        /**
         * step=0 → r: (0,1], step=1 → r: (1,2], … 식으로
         * "현재 step에서 늘어난 반지름 구간(링 밴드)"에만 파티클을 뿌립니다.
         *
         * @param world       ServerWorld
         * @param player      기준 플레이어 (시작 시점 위치/방향을 캡처하여 고정 궤적)
         * @param particle    파티클
         * @param halfAngleDeg 좌/우 반각 (22.75 추천)
         * @param maxRadius   최종 반지름(예: 12) → repeats= maxRadius 로 호출
         * @param yOffset     눈높이 기준 오프셋 (예: -0.4)
         * @param arcSpacing  호(각) 방향 포인트 간격(블록 단위) (예: 0.35)
         * @param radialStep  반지름(두께) 방향 포인트 간격(예: 0.2)
         * @return IntConsumer : 매 tick마다 accept(step) 호출 (0부터 시작)
         */
        public static java.util.function.IntConsumer growingSectorBands(
                ServerWorld world, ServerPlayerEntity player, ParticleEffect particle,
                double halfAngleDeg, int maxRadius, double yOffset,
                double arcSpacing, double radialStep
        ) {
            Vec3d look = player.getRotationVec(1.0F);
            Vec3d dirXZ = new Vec3d(look.x, 0, look.z).normalize();
            double baseAngle = Math.atan2(dirXZ.z, dirXZ.x);
            double halfRad = Math.toRadians(halfAngleDeg);
            Vec3d center = player.getPos().add(0, player.getEyeY() - player.getY() + yOffset, 0);

            double start = baseAngle - halfRad;
            double end   = baseAngle + halfRad;

            return (int step) -> {
                int r0i = step;
                int r1i = step + 1;
                if (r1i > maxRadius) return;

                double r0 = Math.max(0.0, r0i);
                double r1 = Math.max(r0 + 1e-6, r1i);

                emitAnnularBand(world, particle, center, start, end, r0, r1, arcSpacing, radialStep);
            };
        }

        /* ===================== AABB 유틸 ===================== */

        /** 현재 step의 링 밴드((step, step+1])를 덮는 AABB (바깥 반지름을 기준으로 박스 계산) */
        public static Box annularBandBoxAtStep(
                Vec3d center, double baseAngle, double halfRad,
                int step, int maxRadius,
                double yMin, double yMax
        ) {
            int rOuter = Math.min(maxRadius, step + 1);
            double r = Math.max(1e-6, rOuter);
            double start = baseAngle - halfRad;
            double end   = baseAngle + halfRad;

            java.util.List<Vec3d> pts = new java.util.ArrayList<>();
            pts.add(point(center, r, start));
            pts.add(point(center, r, end));
            double[] cardinals = {0, Math.PI*0.5, Math.PI, Math.PI*1.5};
            for (double c : cardinals) if (angleInRange(c, start, end)) pts.add(point(center, r, c));
            pts.add(center);

            double minX = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
            for (Vec3d v : pts) {
                if (v.x < minX) minX = v.x;
                if (v.z < minZ) minZ = v.z;
                if (v.x > maxX) maxX = v.x;
                if (v.z > maxZ) maxZ = v.z;
            }
            return new Box(minX, yMin, minZ, maxX, yMax, maxZ);
        }

        /** 전체 부채꼴(반지름 = outerRadius, 총각 = 2*halfRad)을 덮는 AABB */
        public static Box sectorBoxTotal(
                Vec3d center, double baseAngle, double halfRad,
                double outerRadius, double yMin, double yMax
        ) {
            double start = baseAngle - halfRad;
            double end   = baseAngle + halfRad;

            java.util.List<Vec3d> pts = new java.util.ArrayList<>();
            pts.add(point(center, outerRadius, start));
            pts.add(point(center, outerRadius, end));
            double[] cardinals = {0, Math.PI*0.5, Math.PI, Math.PI*1.5};
            for (double c : cardinals) if (angleInRange(c, start, end)) pts.add(point(center, outerRadius, c));
            pts.add(center);

            double minX = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
            for (Vec3d v : pts) {
                if (v.x < minX) minX = v.x;
                if (v.z < minZ) minZ = v.z;
                if (v.x > maxX) maxX = v.x;
                if (v.z > maxZ) maxZ = v.z;
            }
            return new Box(minX, yMin, minZ, maxX, yMax, maxZ);
        }

        /* ===================== 내부 구현 ===================== */

        private static void emitAnnularBand(
                ServerWorld world, ParticleEffect particle, Vec3d c,
                double startAng, double endAng,
                double r0, double r1,
                double arcSpacing, double radialStep
        ) {
            double dAng = Math.max(arcSpacing / Math.max(r1, 1e-6), 1e-4);
            double sweep = Math.max(0, endAng - startAng);
            int arcSamples = Math.max(1, (int)Math.ceil(sweep / dAng));
            double dr = Math.max(1e-3, radialStep);

            for (int i = 0; i <= arcSamples; i++) {
                double a = startAng + (sweep * i / arcSamples);
                for (double r = r0 + dr; r <= r1; r += dr) {
                    Vec3d p = point(c, r, a);
                    world.spawnParticles(particle, p.x, p.y, p.z, 5, 0.1, 0.1, 0.1, 0.1);
                }
            }
        }

        private static Vec3d point(Vec3d c, double r, double ang) {
            return new Vec3d(c.x + r * Math.cos(ang), c.y, c.z + r * Math.sin(ang));
        }

        private static boolean angleInRange(double t, double start, double end) {
            t = norm(t); start = norm(start); end = norm(end);
            if (end < start) end += Math.PI * 2.0;
            if (t < start) t += Math.PI * 2.0;
            return t <= end + 1e-9;
        }
        private static double norm(double a) {
            double t = a % (Math.PI * 2.0);
            return t < 0 ? t + Math.PI * 2.0 : t;
        }
    }

}
