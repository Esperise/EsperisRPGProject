package com.altale.esperis.skills.visualEffect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.core.jmx.Server;
import org.joml.Vector3f;

import java.util.Objects;

import static com.google.common.primitives.Doubles.max;

public class DrawCircle {
    public static void spawnCircle(ServerPlayerEntity player, ServerWorld world, double distance, double radius, int points, double vectorX, double vectorY, double vectorZ, float x, float y, float z, float red, float green, float blue, float dustSize, int amount) {
        Vec3d look = player.getRotationVec(1.0F).normalize(); // 시선 방향
        Vec3d eyePos = player.getCameraPosVec(1.0F); // 눈 위치
        Vec3d center = eyePos.add(look.multiply(distance)); // 원 중심
        // look에 수직인 두 벡터 구하기
        Vec3d up = new Vec3d(vectorX, vectorY, vectorZ);
        Vec3d right = look.crossProduct(up).normalize();
        Vec3d normalUp = look.crossProduct(right).normalize();
        // 원 둘레 점 개수
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i;
            double x1 = Math.cos(angle) * radius;
            double y2 = Math.sin(angle) * radius;
            // 원의 평면 위 점
            Vec3d point = center.add(right.multiply(x1)).add(normalUp.multiply(y2));
            world.spawnParticles(
                    new DustParticleEffect(new Vector3f(red, green, blue), dustSize), // 빨강색
                    point.x + x, point.y + y, point.z + z,
                    amount, 0, 0, 0, 0.1);
        }
    }

    public static void spawnSphereAroundBarrier(LivingEntity entity, ServerWorld world, int points
            , float red, float green, float blue
            , float dustSize, int amount) {
        // 1) 구의 중심: 엔티티 발 위치 + 반 높이
        Box box = entity.getBoundingBox();
        double halfHeight = box.getYLength() / 2.0;
        Vec3d center = entity.getPos().add(0, halfHeight, 0);

        // 2) 반지름: 엔티티 높이의 절반 (원하면 변경 가능)
        double radius = halfHeight * 1.5 +0.5;

        // 3) θ 분할 수 (가로 둘레): points * 2 로 더 고해상도
        int sectors = (int) max(points,(points * halfHeight));
        int rings =  (int) max(points,(points * halfHeight));

        // 4) 구면 좌표 공식으로 점 계산
        for (int i = 0; i < rings; i++) {
            double phi = Math.PI * i / (rings - 1);       // 0 ≤ φ ≤ π
            for (int j = 0; j < sectors; j++) {
                double theta = 2 * Math.PI * j / sectors; // 0 ≤ θ < 2π

                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.cos(phi);
                double z = radius * Math.sin(phi) * Math.sin(theta);

                Vec3d point = center.add(x, y, z);
//                world.spawnParticles(
//                        new DustColorTransitionParticleEffect(
//                                new Vector3f(1f, 1f, 1f),  // from color
//                                new Vector3f(0f, 0f, 0f),  // to color
//                                dustSize                       // size
//                        ),
//                        point.x, point.y, point.z,
//                        amount, 0, 0, 0, 0.0
//                );
                world.spawnParticles(
                        new DustParticleEffect(new Vector3f(red, green, blue), (float) max(dustSize,dustSize*(halfHeight*2-1))),
                        point.x, point.y, point.z,
                        amount, 0.0, 0.0, 0.0, 0.0
                );
                //wax_on: 주황색 별 wax_off,ELECTRIC_SPARK: 네더의별 모양 GLOW: 청록/초록색 별 위로 올라감 SCRAPE:glow 유지 END_ROD
                //FIREWORK 아래로 떨어지는 하얀 별 MYCELIUM: 지옥 회색 이펙트 오래 떠다님
                // SCULK_SOUL: 해골 모양 짙은청록 빛남
                //DAMAGE_INDICATOR: 피 닳을때 나오는 어두운 하트
                // FLASH 눈뽕 NAUTILUS 어두운 파랑색 구 떨어짐(작음) SCULK_CHARGE_POP 청록색 거품방울(속 차있음)
                //SNOWFLAKE 눈입자 큼 (떨어짐) =SPIT POOF: spit snowflake 거꾸로     NOTE:음표 SOUL_FIRE_FLAME:푸른 불꽃 SOUL:영혼
                //PORTAL : 아래 조금 떨어지고 좀 유지되는 보라색입자 REVERSE_PORTAL:유지되는 보라색 입자
                //WITCH 위로 올라가는 x UNDERWATER: 작은 보라색 점
//                world.spawnParticles(
//                        ParticleTypes.ELECTRIC_SPARK,
//                        point.x, point.y, point.z,
//                        amount, 0, 0, 0, 0
//                );
            }
        }
        for (int i = 0; i < rings/8; i++) {
            double phi = Math.PI * i / ((double) rings /6 - 1);       // 0 ≤ φ ≤ π
            for (int j = 0; j < sectors/6; j++) {
                double theta = 2 * Math.PI * j / ((double) sectors /6); // 0 ≤ θ < 2π

                double x = (radius+1) * Math.sin(phi) * Math.cos(theta);
                double y = (radius+1) * Math.cos(phi);
                double z = (radius+1) * Math.sin(phi) * Math.sin(theta);

                Vec3d point = center.add(x, y, z);
                                world.spawnParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        point.x, point.y, point.z,
                        amount, 0, 0, 0, 0
                );
            }
        }
    }
    public static void spawnDiamondAround(LivingEntity entity , ServerWorld serverWorld,int points
                                        , float red, float green, float blue
                                        ,float dustSize, int amount){
        Vec3d pos= entity.getPos();
        Vec3d up = new Vec3d(0,1,0);//y축을 가리키는 vector
        Vec3d vectorX= new Vec3d(1,0,0);
        Vec3d vectorZ= new Vec3d(0,0,1);
        // look에 수직인 두 벡터 구하기
        Box box = entity.getBoundingBox();
        double height = box.getYLength();
        double radius;
        double particleDelta= height/ 50;
        for(double d=0; d<=(height/2); d+=particleDelta){
            radius=d+0.05;
            for (int i = 0; i < points; i++) {
                double angle = (2 * Math.PI / points) * i;
                double x1 = Math.cos(angle) * radius;
                double z1 = Math.sin(angle) * radius;
                // 원의 평면 위 점
                Vec3d point = pos.add(vectorX.multiply(x1)).add(vectorZ.multiply(z1));
                serverWorld.spawnParticles(
                        new DustParticleEffect(new Vector3f(red, green, blue), dustSize),
                        point.x , d+point.y, point.z,
                        amount, 0, 0, 0, 0.1);
                serverWorld.spawnParticles(
                        new DustParticleEffect(new Vector3f(red, green, blue), dustSize),
                        point.x , height-d+point.y, point.z,
                        amount, 0, 0, 0, 0.1);
        }
        }

    }

}
