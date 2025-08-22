package com.altale.esperis.skills.visualEffect;
// Fabric/Yarn 1.20.x
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;

import net.minecraft.util.math.random.Random;

public final class RandomStraight3DLines {
    private RandomStraight3DLines(){}

    /**
     * 엔티티 주변에 임의의 각도를 가진 3D 직선을 여러 개 생성.
     *
     * @param world        ServerWorld
     * @param centerEntity 기준 엔티티
     * @param particle     파티클
     * @param lineCount    라인 개수
     * @param innerRadius  시작점 최소 거리(엔티티 중심으로부터)
     * @param outerRadius  시작점 최대 거리
     * @param yOffset      시작점 높이 오프셋(엔티티 위치 기준)
     * @param lineLength   라인 길이
     * @param step         라인 샘플 간격(작을수록 촘촘)
     * @param centered     true면 중심을 기준으로 양방향(중앙 정렬), false면 한쪽 방향으로만
     * @param thickness    선 굵기(0이면 완전 얇은 선). 굵게 보이도록 직선 주변에 미세 산포.
     */
    public static void spawnRandomStraightLines(
            ServerWorld world,
            Entity centerEntity,
            ParticleEffect particle,
            int lineCount,
            double innerRadius,
            double outerRadius,
            double yOffset,
            double lineLength,
            double step,
            boolean centered,
            double thickness
    ) {
        Random rnd =  world.getRandom();
        Vec3d c = centerEntity.getPos().add(0, yOffset, 0);

        for (int i = 0; i < lineCount; i++) {
            // 1) 무작위 3D 방향(구면 균일)
            Vec3d d = randomUnitVector3(rnd);

            // 2) 시작점(엔티티 주변에서 선택)
            //    반경 r을 [inner, outer]에서 랜덤, 방향은 또 하나의 구면 균일 벡터로
            double r = innerRadius + rnd.nextDouble() * Math.max(0.0, outerRadius - innerRadius);
            Vec3d shift = randomUnitVector3(rnd).multiply(r);
            Vec3d base = c.add(shift);

            Vec3d p0;
            if (centered) {
                // 중앙 정렬: 라인이 base를 중심으로 양쪽으로 뻗어 보이게
                p0 = base.subtract(d.multiply(lineLength * 0.5));
            } else {
                // 한쪽 방향으로만
                p0 = base;
            }

            // 3) 두께(선 굵기)용 직교 기저(u, v) 만들기
            Vec3d u, v;
            {
                Vec3d any = Math.abs(d.y) < 0.99 ? new Vec3d(0, 1, 0) : new Vec3d(1, 0, 0);
                u = d.crossProduct(any).normalize();
                v = d.crossProduct(u).normalize();
            }

            // 4) 직선을 따라 샘플링
            for (double t = 0.0; t <= lineLength; t += step) {
                Vec3d pos = p0.add(d.multiply(t));

                // 굵기 > 0이면 라인 주변에 살짝 산포
                if (thickness > 0) {
                    // 원판(반지름 thickness) 내 임의 점
                    double a = rnd.nextDouble() * Math.PI * 2.0;
                    double rad = rnd.nextDouble() * thickness;
                    Vec3d jitter = u.multiply(Math.cos(a) * rad).add(v.multiply(Math.sin(a) * rad));
                    pos = pos.add(jitter);
                }

                world.spawnParticles(particle, pos.x, pos.y, pos.z, 3, 0, 0, 0, 0);
            }
        }
    }

    /** 구면 균일(random direction). u∈[-1,1], a∈[0,2π) */
    private static Vec3d randomUnitVector3(Random rnd) {
        double u = rnd.nextDouble() * 2.0 - 1.0;          // cos(φ)
        double a = rnd.nextDouble() * Math.PI * 2.0;      // θ
        double sqrt = Math.sqrt(Math.max(0.0, 1.0 - u*u));
        double x = sqrt * Math.cos(a);
        double y = u;
        double z = sqrt * Math.sin(a);
        return new Vec3d(x, y, z);
    }
}

