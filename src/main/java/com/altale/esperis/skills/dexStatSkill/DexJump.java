package com.altale.esperis.skills.dexStatSkill;

import com.altale.esperis.serverSide.GetEntityLookingAt;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import com.altale.esperis.skills.visualEffect.DrawCircle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class DexJump {
    public static void dexJump(ServerPlayerEntity player, ServerWorld world) {
        long now = world.getTime();
        if(CoolTimeManager.isOnCoolTime(player, "dexJump")){

        }
        else{
            CoolTimeManager.setCoolTime(player, "dexJump", 150);
            doDexJump(player, world);
        }

    }
    private static void doDexJump(ServerPlayerEntity player, ServerWorld world) {
        LivingEntity targetEntity = (LivingEntity) GetEntityLookingAt.getEntityLookingAt(player,4.0F,0.3F);
        Vec3d look= player.getRotationVec(1.0f);
        if( targetEntity != null){
            CoolTimeManager.setCoolTime(player, "dexJump", 300);
            targetEntity.setVelocity(Vec3d.ZERO);
            double power = 0.4;
            Vec3d velocity= new Vec3d(look.x * power ,0.30,look.z* power);
            targetEntity.addVelocity(velocity.x, velocity.y, velocity.z);
            targetEntity.velocityModified = true;
            KnockedAirborneVer2.giveKnockedAirborneVer2(targetEntity,player, 3,3);//0.3초 에어본
            double playerPower = -1.6;
            Vec3d playerVelocity = new Vec3d(look.x * playerPower, 0.65, look.z * playerPower);
            player.addVelocity(playerVelocity.x, playerVelocity.y, playerVelocity.z);
            player.velocityModified = true;
            DrawCircle.spawnCircle(player, world, 2.0, 2.0, 30, 0,1,0
                    ,0,0,0,0,1,0,1.0f,20);
//            Vec3d eye = player.getCameraPosVec(1.0F);
//            Vec3d dir = player.getRotationVec(1.0F).normalize();
//            Vec3d lateral = dir.crossProduct(new Vec3d(0, 1, 0)).normalize();
//            Vec3d offsetEye = eye.add(lateral);
//            double distance = 3.0; // 플레이어로부터의 거리
//            double radius = 1.0;   // 원 반지름
//
//            Vec3d center = eye.add(look.multiply(distance)); // 원 중심
//
//            // look에 수직인 두 벡터 구하기
//            Vec3d up = new Vec3d(0, 1, 0);
//            Vec3d right = look.crossProduct(up).normalize();
//            Vec3d normalUp = look.crossProduct(right).normalize();
//
//            int points = 30; // 원 둘레 점 개수
//
//            for (int i = 0; i < points; i++) {
//                double angle = (2 * Math.PI / points) * i;
//                double x = Math.cos(angle) * radius;
//                double y = Math.sin(angle) * radius;
//
//                // 원의 평면 위 점
//                Vec3d point = center.add(right.multiply(x)).add(normalUp.multiply(y));
//
//                world.spawnParticles(
//                        new DustParticleEffect(new Vector3f(0.0f, 1.0f, 0.0f), 1.0f), // 빨강색
//                        point.x, point.y, point.z,
//                        10, 0, 0, 0, 0);
//            }
        }
        else{

            double power= 2.0;
            Vec3d velocity = new Vec3d(look.x * power, Math.max(0.55, look.y), look.z * power);
            player.addVelocity(velocity.x, velocity.y, velocity.z);
            player.velocityModified = true;
            if (player.getWorld() instanceof ServerWorld serverWorld){
                Vec3d pos = player.getPos();
                serverWorld.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                        pos.x, pos.y, pos.z, 20, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }
}
