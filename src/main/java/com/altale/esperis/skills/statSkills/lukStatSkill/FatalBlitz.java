package com.altale.esperis.skills.statSkills.lukStatSkill;

import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import com.altale.esperis.skills.debuff.DotTypeVer2;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Vector3f;

import java.util.List;

public class FatalBlitz {
    private static final double maxDistance = 5.0;
    public static void fatalBlitz(ServerPlayerEntity player, ServerWorld world) {


    }
    public static void doFatalBlitz(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, "페이탈블리츠")){

        }else{
            CoolTimeManager.setCoolTime(player, "페이탈블리츠", 120);
            CoolTimeManager.specificCoolTimeReduction(player, "그림자이동", 80);
            PlayerFinalStatComponent playerFinalStatComponent= PlayerFinalStatComponent.KEY.get(player);
            double atk= playerFinalStatComponent.getFinalStat(StatType.ATK);
            Vec3d cameraPos = player.getCameraPosVec(1.0F);
            Vec3d lookVec   = player.getRotationVec(1.0F).normalize();
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
            double activeDistance = Math.min(maxDistance, blockDistance);
            Box box = player.getBoundingBox().stretch(lookVec.multiply(activeDistance)).expand(2.0);
            Vec3d lateral = lookVec.crossProduct(new Vec3d(0, -1, 0));// 오른쪽(바라보는 방향 x축/y축 직각
            Vec3d normalUp = lookVec.crossProduct(lateral);//보는 방향에 보는방향의 오른쪽(x혹은 z직각)을 외적 -> 보는 방향의 y수직 직각 벡터
            boolean hit= false;
            List<Entity> entities= player.getWorld().getOtherEntities(player, box, entity -> entity instanceof LivingEntity&& entity.isAlive());
            for(Entity e : entities){
                if(e instanceof LivingEntity livingEntity){
                    hit= true;
                    System.out.println(livingEntity);
                    DotDamageVer2.giveDotDamage(livingEntity, player, 60,10 ,(float) (5+ (atk*0.8)),  DotTypeVer2.Bleed, true,0.1f,"페이탈블리츠" );
                    player.getWorld().playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                            SoundCategory.PLAYERS,
                            3.0f,
                            1.0f

                    );

                    double theta = Math.toRadians(45);
                    double cos = Math.cos(theta);
                    double sin = Math.sin(theta);
                    double dot = lookVec.dotProduct(normalUp);
                    Vec3d term1 = normalUp.multiply(cos);
                    Vec3d term2 = lookVec.crossProduct(normalUp).multiply(sin);
                    Vec3d term3 = lookVec.multiply(dot * (1 - cos));
                    Vec3d rotated = term1.add(term2).add(term3);

                    double theta2 = Math.toRadians(135);
                    cos = Math.cos(theta2);
                    sin = Math.sin(theta2);
                    dot = lookVec.dotProduct(normalUp);

                    term1 = normalUp.multiply(cos);
                    term2 = lookVec.crossProduct(normalUp).multiply(sin);
                    term3 = lookVec.multiply(dot * (1 - cos));
                    Vec3d rotated2 = term1.add(term2).add(term3);

                    Vec3d worldUp = new Vec3d(0, 1, 0);
                    if (Math.abs(lookVec.dotProduct(worldUp)) > 0.99) {
                        worldUp = new Vec3d(1, 0, 0);
                    }

                    // 4) perp1 = lookVec × worldUp, perp2 = lookVec × perp1
                    Vec3d perp1 = lookVec.crossProduct(worldUp).normalize();
                    Vec3d perp2 = lookVec.crossProduct(perp1).normalize();

                    // 5) X의 중앙 위치 (플레이어 앞 DISTANCE 만큼)
                    Vec3d center = livingEntity.getCameraPosVec(1.0F);

                    // 6) 절반 길이만큼 양쪽으로 STEPS 개수 파티클 뿌리기
                    for (int i = -25; i <= 25; i++) {
                        double t = (double)i / 25  * 1.5;
                        // perp1 축을 따라
                        Vec3d p1 = center.add(rotated.multiply(t));
                        // perp2 축을 따라
                        Vec3d p2 = center.add(rotated2.multiply(t));

                        // 원하는 파티클 타입으로 변경 가능 (예: DUST, FLAME, END_ROD 등)
                        world.spawnParticles(new DustParticleEffect(
                                new Vector3f(1.0f, 0.0f, 0.0f),0.5f),
                                p1.x, p1.y+0.3, p1.z,
                                25, 0, 0, 0, 0);
                        world.spawnParticles(new DustParticleEffect(
                                new Vector3f(1.0f, 0.0f, 0.0f),0.5f),
                                p2.x, p2.y+0.3, p2.z,
                                25, 0, 0, 0, 0);

                    }
                }
            }
            player.teleport(
                    player.getX() + (lookVec.x * activeDistance),
                    player.getY() ,
                    player.getZ() + (lookVec.z * activeDistance)
            );
//            Vec3d center = cameraPos.add(lookVec.multiply(activeDistance+1));
            double theta = Math.toRadians(45);
            double cos = Math.cos(theta);
            double sin = Math.sin(theta);
            double dot = lookVec.dotProduct(normalUp);
            Vec3d term1 = normalUp.multiply(cos);
            Vec3d term2 = lookVec.crossProduct(normalUp).multiply(sin);
            Vec3d term3 = lookVec.multiply(dot * (1 - cos));
            Vec3d rotated = term1.add(term2).add(term3);

            double theta2 = Math.toRadians(135);
            cos = Math.cos(theta2);
            sin = Math.sin(theta2);
            dot = lookVec.dotProduct(normalUp);

            term1 = normalUp.multiply(cos);
            term2 = lookVec.crossProduct(normalUp).multiply(sin);
            term3 = lookVec.multiply(dot * (1 - cos));
            Vec3d rotated2 = term1.add(term2).add(term3);

            Vec3d worldUp = new Vec3d(0, 1, 0);
            if (Math.abs(lookVec.dotProduct(worldUp)) > 0.99) {
                worldUp = new Vec3d(1, 0, 0);
            }

            // 4) perp1 = lookVec × worldUp, perp2 = lookVec × perp1
            Vec3d perp1 = lookVec.crossProduct(worldUp).normalize();
            Vec3d perp2 = lookVec.crossProduct(perp1).normalize();

            // 5) X의 중앙 위치 (플레이어 앞 DISTANCE 만큼)
            Vec3d center = cameraPos.add(lookVec.multiply(activeDistance+1));

            // 6) 절반 길이만큼 양쪽으로 STEPS 개수 파티클 뿌리기
            for (int i = -50; i <= 50; i++) {
                double t = (double)i / 50  * 4.0;
                // perp1 축을 따라
                Vec3d p1 = center.add(rotated.multiply(t));
                // perp2 축을 따라
                Vec3d p2 = center.add(rotated2.multiply(t));

                // 원하는 파티클 타입으로 변경 가능 (예: DUST, FLAME, END_ROD 등)
                world.spawnParticles(new DustParticleEffect(
                                new Vector3f(0.0f, 0.0f, 0.0f),0.5f),
                        p1.x, p1.y, p1.z,
                        25, 0, 0, 0, 0);
                world.spawnParticles(new DustParticleEffect(
                                new Vector3f(0.0f, 0.0f, 0.0f),0.5f),
                        p2.x, p2.y, p2.z,
                        25, 0, 0, 0, 0);
                if(hit ){
                    double k = (double)i / 50  * 2.5;
                    // perp1 축을 따라
                    Vec3d p3 = center.add(rotated.multiply(k));
                    // perp2 축을 따라
                    Vec3d p4 = center.add(rotated2.multiply(k));
                    world.spawnParticles(new DustParticleEffect(
                                    new Vector3f(1.0f, 0.0f, 0.0f),0.5f),
                            p3.x, p3.y, p3.z,
                            25, 0, 0, 0, 0);
                    world.spawnParticles(new DustParticleEffect(
                                    new Vector3f(1.0f, 0.0f, 0.0f),0.5f),
                            p4.x, p4.y, p4.z,
                            25, 0, 0, 0, 0);
                }
            }
        }
        }

}
