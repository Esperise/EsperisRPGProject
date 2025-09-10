package com.altale.esperis.skills.statSkills.lukStatSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAt;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import com.altale.esperis.skills.debuff.DotTypeVer2;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.*;

public class DoubleStep {
    // World별, 실행할 시간 → 실행 로직(Runnable) 맵
    private static final Map<ServerWorld, Map<UUID, Map<Long, Runnable>>> delayedTasks = new HashMap<>();
    public static final String skillName= SkillsId.LUK_25.getSkillName();
    public static final int cooltime= 60;
    public static final float baseDamage= 1;
    public static final float atkCoeffi = 0.37f;
    public static final float baseDotDamage = 2.5f;
    public static final float dotAtkCoeffi= 0.77f;

    public static void doubleStep(ServerPlayerEntity player, ServerWorld world1) {
        // 효과는 기존 doStepEffect() 내용
        ServerWorld serverWorld = (ServerWorld) world1;
        long now = world1.getTime();

        if(CoolTimeManager.isOnCoolTime((ServerPlayerEntity) player,skillName)){
            CoolTimeManager.showRemainCoolTime((ServerPlayerEntity) player, skillName);
        }
        else{
            CoolTimeManager.setCoolTime((ServerPlayerEntity) player, skillName, cooltime);

            // 즉시 효과 실행
            doStepEffect((ServerPlayerEntity) player, serverWorld);
            int skillLevel=1;

            // 10틱(0.5초) 뒤에 다시 실행되도록 스케줄
            for(long trig=now; trig<=now+(14*skillLevel); trig+=7){
                delayedTasks
                        .computeIfAbsent(serverWorld, w -> new HashMap<>())
                        .computeIfAbsent(player.getUuid(), u -> new HashMap<>())
                        .put(trig, () -> doStepEffect((ServerPlayerEntity) player, serverWorld));
            }
        }
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                Map<UUID, Map<Long, Runnable>> UuidTasks = delayedTasks.get(world);
                if (UuidTasks == null) continue;
                else{
                    for (Map.Entry<UUID, Map<Long, Runnable>> uuidEntry : UuidTasks.entrySet()) {
                        Map<Long, Runnable> taskMap = uuidEntry.getValue();
                        long t = world.getTime();
                        Runnable task = taskMap.remove(t);
                        if (task != null) task.run();
                        if (UuidTasks.isEmpty()) delayedTasks.remove(world);
                    }

                }

            }
        });
    }
    public static void register() {
        // 1) UseItem 이벤트에서 즉시 효과 + 10틱 뒤 스케줄 등록
//        UseItemCallback.EVENT.register((player, world, hand) -> {
//            if (!world.isClient && player.getStackInHand(hand).getItem() == Items.GOLDEN_SWORD) {
//                ServerWorld serverWorld = (ServerWorld) world;
//                long now = world.getTime();
//
//                if(CoolTimeManager.isOnCoolTime((ServerPlayerEntity) player,"double_step_rc")){
//                    CoolTimeManager.showRemainCoolTime((ServerPlayerEntity) player, "double_step_rc");
//                }
//                else{
//                    CoolTimeManager.setCoolTime((ServerPlayerEntity) player, "double_step_rc", 100);
//
//                    // 즉시 효과 실행
//                    doStepEffect((ServerPlayerEntity) player, serverWorld);
//
//                    // 10틱(0.5초) 뒤에 다시 실행되도록 스케줄
//                    for(long trig=now; trig<=now+16; trig+=4){
//                        delayedTasks
//                                .computeIfAbsent(serverWorld, w -> new HashMap<>())
//                                .computeIfAbsent(player.getUuid(), u -> new HashMap<>())
//                                .put(trig, () -> doStepEffect((ServerPlayerEntity) player, serverWorld));
//                    }
//                }
//
//
//
//
//
//                return TypedActionResult.success(player.getStackInHand(hand));
//            }
//            return TypedActionResult.pass(player.getStackInHand(hand));
//        });

        // 2) 틱마다 스케줄된 작업 체크해서 실행
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                Map<UUID, Map<Long, Runnable>> UuidTasks = delayedTasks.get(world);
                if (UuidTasks == null) continue;
                else{
                    Iterator<Map.Entry<UUID, Map<Long, Runnable>>> uuidIter = UuidTasks.entrySet().iterator();
                    while(uuidIter.hasNext()){
                        Map.Entry<UUID, Map<Long, Runnable>> uuidEntry = uuidIter.next();
                        Map<Long, Runnable> taskMap = uuidEntry.getValue();
                        long t = world.getTime();
                        Runnable task = taskMap.remove(t);
                        if (task != null) task.run();
                        if (UuidTasks.isEmpty()) delayedTasks.remove(world);
                    }

                }

            }
        });
    }

    // 이펙트 + 데미지 + 출혈 DOT 주는 로직을 메서드로 분리
    private static void doStepEffect(ServerPlayerEntity player, ServerWorld world) {
        PlayerFinalStatComponent playerStatComponent = PlayerFinalStatComponent.KEY.get(player);
        double atk= playerStatComponent.getFinalStat(StatType.ATK);
        float stepDamage= (float) (baseDamage+  (atk* atkCoeffi));
        float dotDamage= (float) (baseDotDamage +  atk* dotAtkCoeffi);

        Vec3d eye = player.getCameraPosVec(1.0F);
        Vec3d dir = player.getRotationVec(1.0F).normalize();

// 횡방향: dir × up
        Vec3d lateral = dir.crossProduct(new Vec3d(0, 1, 0)).normalize();
        double randint= Math.random()*2 ;
        double sideOffset = randint;
        double randint2 = randint;
        if(new Random().nextBoolean()) {
            sideOffset= sideOffset *(-1);

        }
        if(new Random().nextBoolean()) {
            randint2= (randint-0.7) *(-1.5);

        }
// 예: 오른쪽으로 1.5 블록 만큼 오프셋

        Vec3d offsetEye = eye.add(lateral.multiply(sideOffset));

        for (double d = 0; d <= 5.0; d += 0.05) {
            if(d<1.25){
                Vec3d pos = offsetEye.add(dir.multiply(d));
                world.spawnParticles(new DustParticleEffect(new Vector3f(0.0f, 0.0f, 0.0f),0.5f), pos.x, pos.y+(randint2/2), pos.z, 25, 0, 0.175, 0, 0);
            }
            else if(d<1.75){
                Vec3d pos = offsetEye.add(dir.multiply(d));
                world.spawnParticles(new DustParticleEffect(new Vector3f(0.0f, 0.0f, 0.0f),0.5f), pos.x, pos.y+(randint2/2), pos.z, 25, 0, 0.4, 0, 0);
            }

            else {
                Vec3d pos = offsetEye.add(dir.multiply(d));
                world.spawnParticles(new DustParticleEffect(new Vector3f(1.0f, 1.0f, 1.0f),0.5f), pos.x, pos.y+(randint2/2), pos.z, 40, 0, 0.251-(d/20), 0, 0);
            }
        }
        player.getWorld().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ITEM_TRIDENT_HIT,
                SoundCategory.PLAYERS,
                5.0f,
                1.0f

        );



        // 사거리 7짜리 박스로 주변 엔티티 처리
        Box box = player.getBoundingBox().stretch(dir.multiply(2.0F+randint)).expand(randint, 0.5, randint);
        int entityCount =0;
        Entity entity =GetEntityLookingAt.getEntityLookingAt(player, 6.0F+randint, randint);
        if(entity != null){
            LivingEntity living = (LivingEntity) entity;
            // 즉시 데미지
            living.getWorld().playSound(
                    null,
                    living.getX(),
                    living.getY(),
                    living.getZ(),
                    SoundEvents.ENTITY_ITEM_BREAK,
                    SoundCategory.PLAYERS,
                    5.0f,
                    1.0f

            );
            DamageSource src = world.getDamageSources().playerAttack(player);
            living.timeUntilRegen = 0;
            living.hurtTime = 0;
            living.damage(src, stepDamage);
            living.setVelocity(Vec3d.ZERO);
            living.velocityModified = true;
            // 출혈 DOT
            DotDamageVer2.giveDotDamage(living, player, 50, 10, dotDamage, DotTypeVer2.Bleed, false,0f, skillName);
            CoolTimeManager.specificCoolTimeReduction(player, SkillsId.LUK_75.getSkillName(), 20);
        }
        else{
            Box box2 = player.getBoundingBox().stretch(dir.multiply(2.5F)).expand(randint, 0.5, randint);

            for (Entity e : world.getOtherEntities(player, box2)) {
                if (!(e instanceof LivingEntity)) continue;
                entityCount++;
                if(entityCount >=2) {
                    break;
                }

                LivingEntity living = (LivingEntity) e;
                // 즉시 데미지
                living.getWorld().playSound(
                        null,
                        living.getX(),
                        living.getY(),
                        living.getZ(),
                        SoundEvents.ENTITY_ITEM_BREAK,
                        SoundCategory.PLAYERS,
                        5.0f,
                        1.0f

                );
                DamageSource src = world.getDamageSources().playerAttack(player);
                living.timeUntilRegen = 0;
                living.hurtTime = 0;
                living.damage(src, 2.0f);
                living.setVelocity(Vec3d.ZERO);
                living.velocityModified = true;
                // 출혈 DOT
                DotDamageVer2.giveDotDamage(living, player, 50, 10, dotDamage, DotTypeVer2.Bleed, true,0.1f, skillName);
                if(world.getOtherEntities(player, box).isEmpty()){

                }
            }

        }

    }
}

