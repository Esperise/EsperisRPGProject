package com.altale.esperis.skills.statSkills.strSkill;

import com.altale.esperis.serverSide.Utilities.ArcPointEmitter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class HorizenSweepEffects {


    /* -----------------------------------------------------------
       1) 부채꼴 스윕: step 진행에 따라 호가 펼쳐지며 파티클을 뿌리는 IntConsumer
       ----------------------------------------------------------- */

    /**
     * step이 0→totalSteps까지 증가할 때,
     * 플레이어 시야 기준 우측 halfAngleDeg → 좌측 halfAngleDeg 로 호(arc)가 점차 그려집니다.
     * - 시작/끝 반지름(둘레의 양옆 선)은 옵션으로 1회만 그립니다.
     * - 슬래시 연출에 적합: 이전 step 이후 추가 개방분에만 파티클을 뿌립니다.
     *
     * @param world       ServerWorld
     * @param player      기준 플레이어(생성 시점의 위치/방향을 캡쳐합니다)
//     * @param particle    파티클
     * @param radius      반지름 (예: 8.0)
     * @param halfAngleDeg 좌/우 반각 (예: 45.0 → 총 90°)
     * @param yOffset     눈높이 기준 오프셋 (예: -0.4)
     * @param arcSpacing  호 상의 점 간격(블록) (예: 0.35)
     * @param raySpacing  반지름 선상의 점 간격(블록) (예: 0.35)
     * @param totalSteps  전체 진행 스텝 수(예: 18 → 약 0.9초 @20tps)
     * @param drawStartRay 시작 반지름을 1회만 그릴지
     * @param drawEndRay   끝 반지름을 완료 시 1회만 그릴지
     * @return IntConsumer: 매 tick마다 accept(step) 호출
     */
    public static IntConsumer sectorSweepXZ(
            ServerWorld world,
            ServerPlayerEntity player,
            ParticleEffect particle,
            double radius,
            double halfAngleDeg,
            double yOffset,
            double arcSpacing,
            double raySpacing,
            int totalSteps,
            boolean drawStartRay,
            boolean drawEndRay
    ) {
        // 생성 시점의 원점/방향을 고정(슬래시 시작 순간 기준으로 그려짐)
        final Vec3d look = horizontalUnit(player.getRotationVec(1.0F));
        final double base = Math.atan2(look.z, look.x); // 라디안
        final double half = Math.toRadians(halfAngleDeg);

        final Vec3d center = player.getPos().add(0, player.getEyeY() - player.getY() + yOffset, 0);
        final double start = base - half;           // 우측 끝
        final double end   = base + half;           // 좌측 끝
        final double sweep = end - start;

        // 내부 상태(클로저): 직전까지 그린 arc의 끝각
        final double[] prevEnd = new double[] { start };
        final boolean[] startRayDone = new boolean[] { false };
        final boolean[] endRayDone   = new boolean[] { false };

        final double minArcStepRad = Math.max(arcSpacing / Math.max(radius, 1e-6), 1e-4);

        return (int step) -> {
            double t = MathHelper.clamp(step / (double) Math.max(1, totalSteps), 0.0, 1.0);
            double curEnd = start + sweep * t;

            // (옵션) 시작 반지름은 첫 호출 때 1회만
            if (drawStartRay && !startRayDone[0]) {
                emitRay(world, particle, center, start, radius, raySpacing);
                startRayDone[0] = true;
            }

            // 이전 프레임 이후 새로 열린 각도 구간만 그리기
            if (curEnd > prevEnd[0] + 1e-7) {
                emitArc(world, particle, center, radius, prevEnd[0], curEnd, minArcStepRad);
                prevEnd[0] = curEnd;
            }

            // (옵션) 완료 시 끝 반지름 1회만
            if (drawEndRay && !endRayDone[0] && t >= 1.0 - 1e-9) {
                emitRay(world, particle, center, end, radius, raySpacing);
                endRayDone[0] = true;
            }
        };
    }
    public static IntConsumer sectorSweepXZ(
            ServerWorld world,
            ServerPlayerEntity player,
            ParticleEffect fallbackParticle,  // 기본 한 점 뿌릴 때 사용
            double radius,
            double halfAngleDeg,
            double yOffset,
            double arcSpacing,
            double raySpacing,
            int totalSteps,
            boolean drawStartRay,
            boolean drawEndRay,
            @Nullable ArcPointEmitter emitter   // ★ 추가
    ) {
        final Vec3d look = horizontalUnit(player.getRotationVec(1.0F));
        final double base = Math.atan2(look.z, look.x);
        final double half = Math.toRadians(halfAngleDeg);
        final Vec3d center = player.getPos().add(0, player.getEyeY() - player.getY() + yOffset, 0);

        final double start = base - half;
        final double end   = base + half;
        final double sweep = end - start;

        final double[] prevEnd = new double[] { start };
        final boolean[] startRayDone = new boolean[] { false };
        final boolean[] endRayDone   = new boolean[] { false };
        final double minArcStepRad = Math.max(arcSpacing / Math.max(radius, 1e-6), 1e-4);

        return (int step) -> {
            double t = MathHelper.clamp(step / (double) Math.max(1, totalSteps), 0.0, 1.0);
            double curEnd = start + sweep * t;

            if (drawStartRay && !startRayDone[0]) {
                emitRay(world, fallbackParticle, center, start, radius, raySpacing);
                startRayDone[0] = true;
            }

            if (curEnd > prevEnd[0] + 1e-7) {
                emitArc(world, fallbackParticle, center, radius, prevEnd[0], curEnd, minArcStepRad, emitter);
                prevEnd[0] = curEnd;
            }

            if (drawEndRay && !endRayDone[0] && t >= 1.0 - 1e-9) {
                emitRay(world, fallbackParticle, center, end, radius, raySpacing);
                endRayDone[0] = true;
            }
        };
    }

    /**
     * 위의 풀 옵션이 번거로우면 간단 버전도 제공합니다.
     */
//    public static IntConsumer sectorSweepXZ(
//            ServerWorld world,
//            ServerPlayerEntity player,
//            ParticleEffect particle,
//            double radius,
//            double halfAngleDeg,
//            double yOffset,
//            double spacing,
//            int totalSteps
//    ) {
//        return sectorSweepXZ(world, player, particle, radius, halfAngleDeg, yOffset, spacing, spacing, totalSteps, true, true);
//    }

    /* -----------------------------------------------------------
       2) 현재 진행(step)에 대응하는 부채꼴 AABB(Box) 도우미
       ----------------------------------------------------------- */

    /**
     * sectorSweepXZ와 동일한 파라미터/캡쳐 방식으로, 특정 step에 해당하는
     * "현재까지 열린 부채꼴"을 덮는 AABB를 계산합니다.
     * - 후보 엔티티 1차 필터용으로 사용하고, 실제 판정은 각/거리로 한 번 더 체크하세요.
     */
    public static Box sectorSweepBoxAtStep(
            Vec3d center,         // sectorSweepXZ 생성 시 썼던 center
            double baseAngleRad,  // 생성 시의 baseAngle (atan2)
            double halfRad,       // 생성 시의 halfRad
            double radius,
            double yMin, double yMax,
            int step, int totalSteps
    ) {
        double t = MathHelper.clamp(step / (double) Math.max(1, totalSteps), 0.0, 1.0);
        double start = baseAngleRad - halfRad;
        double curEnd = start + (halfRad * 2.0) * t;

        return computeSectorBoundingBoxPartial(center, start, curEnd, radius, yMin, yMax);
    }

    /* -----------------------------------------------------------
       내부 유틸
       ----------------------------------------------------------- */

    private static void emitArc(ServerWorld world, ParticleEffect fallback, Vec3d c,
                                double r, double a0, double a1, double dAngle,
                                @Nullable ArcPointEmitter emitter) {
        double length = Math.max(0.0, a1 - a0);
        int steps = Math.max(1, (int) Math.ceil(length / dAngle));
        double stepAng = length / steps;

        for (int i = 0; i <= steps; i++) {
            double a = a0 + stepAng * i;
            Vec3d p = new Vec3d(c.x +( r/6 * Math.cos(a)), c.y, c.z +( r/6 * Math.sin(a)));

            // i == steps → 이번 프레임 "새로 열린 호의 선두"
            if (emitter != null && i == steps) {
                Vec3d outward = new Vec3d(Math.cos(a), 0, Math.sin(a)); // 중심→p 방향 단위벡터(XZ)
                emitter.emit(world, p, outward);
            } else {
                world.spawnParticles(fallback, c.x +( r * Math.cos(a)), c.y, c.z +( r * Math.sin(a)), 5, 0.1, 0, 0.1, 0);
            }
        }
    }
    private static void emitArc(ServerWorld world, ParticleEffect fallback, Vec3d c,
                                double r, double a0, double a1, double dAngle) {
        double length = Math.max(0.0, a1 - a0);
        int steps = Math.max(1, (int) Math.ceil(length / dAngle));
        double stepAng = length / steps;

        for (int i = 0; i <= steps; i++) {
            double a = a0 + stepAng * i;
            Vec3d p = new Vec3d(c.x +( r/6 * Math.cos(a)), c.y, c.z +( r/6 * Math.sin(a)));

            // i == steps → 이번 프레임 "새로 열린 호의 선두"
                world.spawnParticles(fallback, c.x +( r * Math.cos(a)), c.y, c.z +( r * Math.sin(a)), 1, 0, 0, 0, 0);

        }
    }
    public static ArcPointEmitter makeDustStreakEmitter(double sideOffset, PlayerEntity player) {
        return (wld, point, outward) -> {
            // lateral: outward를 XZ에서 오른쪽 90도 회전
            Vec3d up = new Vec3d(0, 1, 0);
            Vec3d out = outward.normalize();                  // 칼 길이(끝) 방향
            Vec3d right = up.crossProduct(out).normalize();   // out과 직교, 수평 '오른쪽'
            double rollRad = 0.0;                             // 0=완전 수평(눕힘), 90°=세움
            Vec3d widthAxis = right.multiply(Math.cos(rollRad))
                    .add(up.multiply(Math.sin(rollRad)))
                    .normalize();

            // 손잡이 좌/우 치우침(폭 축 기준)
            Vec3d offsetOrigin = point.add(widthAxis.multiply(sideOffset));

            // -------- 프로필 파라미터 --------
            double L         = 8.0;    // 전체 길이
            double handleLen = 0.80;   // 손잡이 길이
            double guardLen  = 0.25;   // 가드(손잡이 직후)
            double bladeLen  = Math.max(0.0, L - handleLen - guardLen);

            // 반폭(half width)
            double handleHalfW = 0.20;
            double guardHalfW  = 0.60;
            double baseHalfW   = 0.35;
            double tipHalfW    = 0.05;

            // 샘플 밀도
            double lengthStep = 0.05;  // 길이 방향 간격
            double widthStep0 = 0.07;  // 폭 방향 기본 간격

            // smoothstep: [0,1] -> [0,1]
            java.util.function.DoubleUnaryOperator smoothstep = t -> {
                double x = Math.max(0.0, Math.min(1.0, t));
                return x * x * (3.0 - 2.0 * x);
            };

            // -------- 파티클 배치 --------
            for (double d = 0.0; d <= L; d += lengthStep) {
                Vec3d centerLine = offsetOrigin.add(out.multiply(d));

                // d에서의 반폭 halfW (손잡이→가드→블레이드 테이퍼)
                double halfW;
                if (d <= handleLen) {
                    halfW = handleHalfW;
                } else if (d <= handleLen + guardLen) {
                    double t = (d - handleLen) / Math.max(1e-6, guardLen);
                    double s = smoothstep.applyAsDouble(t);
                    // handleHalfW → guardHalfW 로 점점 넓어짐
                    halfW = handleHalfW + (guardHalfW - handleHalfW) * s;
                } else {
                    double t = (d - handleLen - guardLen) / Math.max(1e-6, bladeLen);
                    double s = smoothstep.applyAsDouble(t);
                    // baseHalfW → tipHalfW 로 점점 좁아짐
                    halfW = baseHalfW + (tipHalfW - baseHalfW) * s;
                }

                // 반폭 크기에 비례해 폭 샘플 간격 조절
                double widthStep = Math.max(0.03, Math.min(widthStep0, halfW / 5.0));

                for (double w = -halfW; w <= halfW; w += widthStep) {
                    Vec3d pos = centerLine.add(widthAxis.multiply(w));

                    // 색/강도 규칙은 기존 로직 유지 (Y 퍼짐은 0 → 눕힌 형태 유지)
                    if (d < 1.25) {
                        wld.spawnParticles(
                                new net.minecraft.particle.DustParticleEffect(new org.joml.Vector3f(0f, 0f, 0f), 0.5f),
                                pos.x, pos.y, pos.z,
                                1, 0, 0, 0, 0
                        );
                    } else if (d < 1.75) {
                        wld.spawnParticles(
                                new net.minecraft.particle.DustParticleEffect(new org.joml.Vector3f(0f, 0f, 0f), 0.5f),
                                pos.x, pos.y, pos.z,
                                1, 0, 0, 0, 0
                        );
                    } else {
                        wld.spawnParticles(
                                new net.minecraft.particle.DustParticleEffect(new org.joml.Vector3f(1f, 1f, 1f), 0.5f),
                                pos.x, pos.y, pos.z,
                                1, 0, 0, 0, 0
                        );
                    }
                }
            }
        };
    }


    private static void emitRay(ServerWorld world, ParticleEffect particle, Vec3d c,
                                double angle, double r, double spacing) {
        int samples = Math.max(1, (int) Math.ceil(r / Math.max(spacing, 1e-3)));
        for (int i = 0; i <= samples; i++) {
            double rr = (r * i) / samples;
            Vec3d p = new Vec3d(c.x + rr * Math.cos(angle), c.y, c.z + rr * Math.sin(angle));
            world.spawnParticles(particle, p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
    }

    private static Vec3d horizontalUnit(Vec3d v) {
        Vec3d h = new Vec3d(v.x, 0.0, v.z);
        double len = h.length();
        return len < 1e-6 ? new Vec3d(1, 0, 0) : h.multiply(1.0 / len);
    }

    public static Box computeSectorBoundingBoxPartial(
            Vec3d center, double startAngle, double endAngle,
            double radius, double yMin, double yMax
    ) {
        // start→end 사이에 포함되는 극각(0, 90, 180, 270deg)도 후보에 포함
        List<Vec3d> pts = new ArrayList<>();
        pts.add(pointOnCircle(center, radius, startAngle));
        pts.add(pointOnCircle(center, radius, endAngle));

        double[] cardinals = new double[]{0, Math.PI * 0.5, Math.PI, Math.PI * 1.5};
        for (double c : cardinals) {
            if (angleInRange(c, startAngle, endAngle)) {
                pts.add(pointOnCircle(center, radius, c));
            }
        }
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

    private static boolean angleInRange(double t, double start, double end) {
        t = normalizeAngle(t);
        start = normalizeAngle(start);
        end = normalizeAngle(end);
        if (end < start) end += Math.PI * 2.0;
        if (t < start) t += Math.PI * 2.0;
        return t <= end + 1e-9;
    }

    private static double normalizeAngle(double a) {
        double t = a % (Math.PI * 2.0);
        return t < 0 ? t + Math.PI * 2.0 : t;
    }

    private static Vec3d pointOnCircle(Vec3d c, double r, double a) {
        return new Vec3d(c.x + r * Math.cos(a), c.y, c.z + r * Math.sin(a));
    }
}
