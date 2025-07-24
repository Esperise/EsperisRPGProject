package com.altale.esperis.unuse;

import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAt;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAtDistance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class SpecialBowSkill {
    public static void useSpecialBow(ServerPlayerEntity player, ServerWorld world) {
        PlayerFinalStatComponent playerFinalStatComponent= PlayerFinalStatComponent.KEY.get(player);
        double atk= playerFinalStatComponent.getFinalStat(StatType.ATK);
        double dex= playerFinalStatComponent.getFinalStat(StatType.DEX);
        double shotDamage= 2+ (atk * 0.25) + (dex * 0.15);
        PlayerInventory inventory = player.getInventory();
        boolean hasArrow=inventory.contains(Items.ARROW.getDefaultStack());
        if(hasArrow){
            shotDamage += 4;
        }
        Entity target = GetEntityLookingAt.getEntityLookingAt(player, 40.0f, 0);
        player.getWorld().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.PLAYERS,
                1.0f,
                0.4f
        );//
        if( target == null ){ //타겟팅 대상 없음
            //FIXME x, z 좌표 대각 벡터일때 판정 이상한거 고치기
            System.out.println("None targeted");
            Vec3d playerLookVec= player.getRotationVec(1.0f);
            Vec3d playerCameraPos= player.getCameraPosVec(1.0f);
            Vec3d dir = player.getRotationVec(1F).normalize();
//            Box box = player.getBoundingBox().contract(0.49,0.49,0.49).stretch(dir.multiply(30.0F));
            Vec3d start = player.getCameraPosVec(1.0f);
            Vec3d end = start.add(dir.multiply(30.0));

// 선처럼 얇은 박스를 만들기 위해 두 위치의 min/max 좌표를 사용
            double thickness = 0.1; // 좌우 위아래 폭

            Box box = new Box(
                    Math.min(start.x, end.x) - thickness,
                    Math.min(start.y, end.y) - thickness,
                    Math.min(start.z, end.z) - thickness,
                    Math.max(start.x, end.x) + thickness,
                    Math.max(start.y, end.y) + thickness,
                    Math.max(start.z, end.z) + thickness
            );

            int entityCount =0;
            if(world.getOtherEntities(player, box).isEmpty()){
                for(double i=0; i<=30.0f;i+=0.01){
                    Vec3d pos = playerCameraPos.add(playerLookVec.multiply(i));
                    world.spawnParticles(new DustParticleEffect(new Vector3f(0.0f, 0.0f, 0.0f),0.1f), pos.x, pos.y, pos.z, 5, 0, 0, 0, 0);
                }
            }
//            else{
//                System.out.println(world.getOtherEntities(player, box).isEmpty());
//
//                DamageSource src = world.getDamageSources().playerAttack(player);
//                for(Entity e : world.getOtherEntities(player, box)) {
//                    if(!(e instanceof LivingEntity)) continue;
//                    if(player.getBoundingBox().intersects(e.getBoundingBox())){
//                        e.damage(src, (float) atk);
//                        player.getWorld().playSound(
//                                null,
//                                player.getX(),
//                                player.getY(),
//                                player.getZ(),
//                                SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
//                                SoundCategory.PLAYERS,
//                                1.0f,
//                                1.0f
//                        );
//                    }
//                    entityCount++;
//                    if(entityCount == 1){
//                        LivingEntity targetEntity = (LivingEntity) e;
//                        double distance = GetEntityLookingAtDistance.getEntityLookingAtDistance(player, targetEntity);
//                        for(double i=0; i<=(float) distance ;i+=0.01){
//                            Vec3d pos = playerCameraPos.add(playerLookVec.multiply(i));
//                            world.spawnParticles(new DustParticleEffect(new Vector3f(0.0f, 0.0f, 0.0f),0.1f), pos.x, pos.y, pos.z, 5, 0, 0, 0, 0);
//                        }
//                        player.getWorld().playSound(
//                                null,
//                                player.getX(),
//                                player.getY(),
//                                player.getZ(),
//                                SoundEvents.ITEM_TRIDENT_HIT,
//                                SoundCategory.PLAYERS,
//                                1.0f,
//                                0.4f
//                        );
//                        targetEntity.timeUntilRegen = 0;
//                        targetEntity.hurtTime = 0;
//                        if(distance > 17.0f){
//                            double overDistanceCoefficient = (double)  Math.round(100* 0.04 *(distance - 17.0f))/100.0;
//                            shotDamage *= overDistanceCoefficient;
//                        }
//                        System.out.println(targetEntity);
//                        targetEntity.damage(src, (float) shotDamage);
//
//                        break;
//                    }
//                }
//            }


        } else {
            // 타겟팅 대상에게
            System.out.println("targeted");
            if(target instanceof LivingEntity targetEntity){
                Vec3d playerLookVec= player.getRotationVec(1.0f).normalize();
                Vec3d playerCameraPos= player.getCameraPosVec(1.0f);
                double distance = GetEntityLookingAtDistance.getEntityLookingAtDistance(player, targetEntity);
                for(double i=0; i<=(float) distance ;i+=0.01){
                    Vec3d pos = playerCameraPos.add(playerLookVec.multiply(i));
                    world.spawnParticles(new DustParticleEffect(new Vector3f(0.0f, 0.0f, 0.0f),0.1f), pos.x, pos.y, pos.z, 5, 0, 0, 0, 0);
                }
                player.getWorld().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ITEM_TRIDENT_HIT,
                        SoundCategory.PLAYERS,
                        1.0f,
                        0.4f
                );
                DamageSource src = world.getDamageSources().playerAttack(player);
                targetEntity.timeUntilRegen = 0;
                targetEntity.hurtTime = 0;
                if(distance > 20.0f){
                    double overDistanceCoefficient = (double)  Math.round(100* 0.03 *(distance - 20.0f))/100.0;
                    shotDamage *= overDistanceCoefficient;
                }
                targetEntity.damage(src, (float) shotDamage);

            }
        }

    }
}
