package com.altale.esperis.serverSide.Utilities;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class ParticleHelper {
    /**
     * 플레이어 전방 쪽 수평 반원 파티클
     * @param radius   반원 반지름
     * @param samples  반원 아크 점 개수(클수록 촘촘)
     * @param rings    두께(겹고리 수) 1이면 선
     * @param ahead    플레이어로부터 전방 거리(기본 1.0 추천)
     * @param yOffset  지면 위 높이 오프셋(기본 0.1~0.2 추천)
     */
    public static void spawnFrontSemicircleHorizontal(ServerWorld world, ServerPlayerEntity player,
                                                      double radius, int samples, int rings,
                                                      double ahead, double yOffset) {
        // 1) 전방 벡터의 수평 성분만 사용 (pitch 무시)
        Vec3d f = player.getRotationVec(1.0F);
        Vec3d fwd = new Vec3d(f.x, 0, f.z);
        if (fwd.lengthSquared() < 1.0E-6) {
            // 위/아래만 보고 있을 때 대비: yaw로 대체
            float yaw = player.getYaw();
            double r = Math.toRadians(yaw);
            fwd = new Vec3d(-Math.sin(r), 0, Math.cos(r));
        } else {
            fwd = fwd.normalize();
        }

        // 2) 수평 우측 벡터
        Vec3d right = fwd.crossProduct(new Vec3d(0, 1, 0)).normalize();

        // 3) 반원 중심: 발 기준 + yOffset + 전방 ahead
        Vec3d center = player.getPos().add(0, yOffset, 0).add(fwd.multiply(ahead));

        // 4) 수평 반원: theta ∈ [0, π] (전방 쪽만)
        rings = Math.max(1, rings);
        for (int r = 0; r < rings; r++) {
            double denom = (rings - 1) <= 0 ? 1.0 : (rings - 1);
            double ringRadius = radius * (1.0 - r / denom);  // 바깥→안쪽
            for (int i = 0; i <= samples; i++) {
                double theta = Math.PI * (double) i / samples; // 0..π
                double cs = Math.cos(theta), sn = Math.sin(theta);

                Vec3d p = center
                        .add(right.multiply(ringRadius * cs))
                        .add(fwd.multiply(ringRadius * sn));

                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z,
                        1, 0, 0, 0, 0); // 정확 좌표에 1개씩
            }
        }
    }
    public static void spawnFrontShield(ServerWorld world, ServerPlayerEntity player,
                                        double radius, int samples, int rings) {
        // 1) 기준 벡터: 전방/오른쪽/위(전방에 수직)
        Vec3d fwd = player.getRotationVec(1.0F).normalize();
        Vec3d up = new Vec3d(0, 1, 0);
        // 전방이 거의 위쪽과 평행하면 보조 up을 사용
        if (Math.abs(fwd.dotProduct(up)) > 0.98) up = new Vec3d(0, 0, 1);

        Vec3d right = fwd.crossProduct(up).normalize();
        Vec3d upOrtho = right.crossProduct(fwd).normalize(); // 전방에 수직인 "세로" 축

        // 2) 반원 중심: 몸 중앙 높이에서 전방으로 1블록
        Vec3d center = player.getPos()
                .add(0, player.getHeight() * 0.5, 0)
                .add(fwd.multiply(1.0));

        // 3) 반원 그리기: θ ∈ [-π/2, +π/2]
        // rings로 "두께"를 조금 주고 싶다면 안쪽으로 여러 고리 뿌림
        rings = Math.max(1, rings);
        for (int r = 0; r < rings; r++) {
            double ringRadius = radius * (1.0 - (double) r / (rings - 1 == 0 ? 1 : rings - 1)); // 바깥→안쪽
            for (int i = 0; i <= samples; i++) {
                double t = (double) i / samples;                 // 0..1
                double theta = Math.PI * (t - 0.5);              // -π/2..+π/2
                double cs = Math.cos(theta), sn = Math.sin(theta);

                Vec3d p = center
                        .add(right.multiply(ringRadius * cs))
                        .add(upOrtho.multiply(ringRadius * sn));

                world.spawnParticles(ParticleTypes.END_ROD, p.x, p.y, p.z,
                        1, 0, 0, 0, 0); // 정확 좌표에 1개씩
            }
        }
    }
    public static void spawnFrontShieldWallHorizontal(ServerWorld world, ServerPlayerEntity player,
                                                      ParticleEffect effect,
                                                      double radius, int arcSamples, int radialRings,
                                                      int layers, double layerSpacing, double taper,
                                                      double ahead, double baseYOffset) {
        // 1) 전방 수평 벡터(fwd), 오른쪽 벡터(right)
        Vec3d f = player.getRotationVec(1.0F);
        Vec3d fwd = new Vec3d(f.x, 0, f.z);
        if (fwd.lengthSquared() < 1.0E-6) {
            // 위/아래만 보는 상황 보정
            double r = Math.toRadians(player.getYaw());
            fwd = new Vec3d(-Math.sin(r), 0, Math.cos(r));
        } else {
            fwd = fwd.normalize();
        }
        Vec3d right = fwd.crossProduct(new Vec3d(0, 1, 0)).normalize();

        // 2) 각 레이어(높이)별로 반원 생성
        layers = Math.max(1, layers);
        radialRings = Math.max(1, radialRings);

        Vec3d base = player.getPos();

        for (int ly = 0; ly < layers; ly++) {
            double t = (layers <= 1) ? 0.0 : (double) ly / (layers - 1); // 0..1
            double scale = Math.max(0.0, 1.0 - taper * t);               // 위로 갈수록 살짝 줄이기
            double rLayer = radius * scale;

            // 레이어 중심: 전방 ahead + Y(baseYOffset + ly*layerSpacing)
            Vec3d center = base
                    .add(0, baseYOffset + ly * layerSpacing, 0)
                    .add(fwd.multiply(ahead));

            // 두께용 겹고리(바깥→안쪽)
            for (int ring = 0; ring < radialRings; ring++) {
                double denom = (radialRings - 1) <= 0 ? 1.0 : (radialRings - 1);
                double ringRadius = rLayer * (1.0 - ring / denom);

                // 반원: theta ∈ [0, π] (전방쪽만)
                for (int i = 0; i <= arcSamples; i++) {
                    double theta = Math.PI * (double) i / arcSamples; // 0..π
                    double cs = Math.cos(theta), sn = Math.sin(theta);

                    Vec3d p = center
                            .add(right.multiply(ringRadius * cs))
                            .add(fwd.multiply(ringRadius * sn));

                    world.spawnParticles(effect, p.x, p.y, p.z, 1, 0, 0, 0, 0);
                }
            }
        }
    }
    public static void drawCircleXZ(ServerWorld world, Vec3d center,
                                    ParticleEffect effect, double radius, int samples) {
        if (radius <= 0 || samples <= 0) return;
        for (int i = 0; i < samples; i++) {
            double t = (double) i / samples;
            double theta = t * MathHelper.TAU;          // 0..2π
            double x = center.x + radius * Math.cos(theta);
            double z = center.z + radius * Math.sin(theta);
            world.spawnParticles(effect, x, center.y +radius*0.1 , z, 3, 0, 0, 0, 0);
        }
    }
    public static IntConsumer expandingCircleXZ(ServerWorld world, ServerPlayerEntity player,
                                                ParticleEffect effect,
                                                double baseRadius, double perStep,
                                                double yOffset, int samples) {
        return (int step) -> {
            double radius = Math.max(0.0, baseRadius + perStep * step);
            Vec3d center = player.getPos().add(0, yOffset, 0); // 발 기준 + yOffset
            drawCircleXZ(world, center, effect, radius, samples);
        };
    }

}

