package com.altale.esperis.skills;

import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import com.altale.esperis.skills.debuff.DotTypeVer2;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
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
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class test1 {
    private static final Map<ServerWorld, Map<Long, Runnable>> delayedTasks = new HashMap<>();

    public static void register() {
        // 1) UseItem 이벤트에서 즉시 효과 + 10틱 뒤 스케줄 등록
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient && player.getStackInHand(hand).getItem() == Items.WOODEN_SWORD && !CoolTimeManager.isOnCoolTime((ServerPlayerEntity) player, "test1")) {


                ServerWorld serverWorld = (ServerWorld) world;
                long now = world.getTime();
                String a= String.format("test%d",now);
                CoolTimeManager.setCoolTime((ServerPlayerEntity) player, a, (int) (100000* Math.random()));
                // 즉시 효과 실행
                doStepEffect((ServerPlayerEntity) player, serverWorld);

                // 10틱(0.5초) 뒤에 다시 실행되도록 스케줄
                long trigger = now ;
                delayedTasks
                        .computeIfAbsent(serverWorld, w -> new HashMap<>())
                        .put(trigger, () -> doStepEffect((ServerPlayerEntity) player, serverWorld));

                return TypedActionResult.success(player.getStackInHand(hand));
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });

        // 2) 틱마다 스케줄된 작업 체크해서 실행
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                Map<Long, Runnable> worldTasks = delayedTasks.get(world);
                if (worldTasks == null) continue;
                long t = world.getTime();
                Runnable task = worldTasks.remove(t);
                if (task != null) task.run();
                if (worldTasks.isEmpty()) delayedTasks.remove(world);
            }
        });
    }

    // 이펙트 + 데미지 + 출혈 DOT 주는 로직을 메서드로 분리
    private static void doStepEffect(ServerPlayerEntity player, ServerWorld world) {

        Vec3d eye = player.getCameraPosVec(1.0F);
        Vec3d dir = player.getRotationVec(1.0F).normalize();

// 횡방향: dir × up
        Vec3d lateral = dir.crossProduct(new Vec3d(0, 1, 0)).normalize();
        double randint= Math.random()*2 +1;
        double sideOffset = randint;
        if(new Random().nextBoolean()) {
            sideOffset= sideOffset *(-1);
        }
// 예: 오른쪽으로 1.5 블록 만큼 오프셋

        Vec3d offsetEye = eye.add(lateral.multiply(sideOffset));

        for (double d = 0.0; d <= 20.0; d += 0.05) {
                Vec3d pos = offsetEye.add(dir.multiply(d));
                world.spawnParticles(new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f),0.3f), pos.x, pos.y, pos.z, 250, 2.0, 2.25, 2.0, 0);

        }
        player.getWorld().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.BLOCK_ANVIL_PLACE,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f

        );



        // 사거리 7짜리 박스로 주변 엔티티 처리
        Box box = player.getBoundingBox().stretch(dir.multiply(20.0F)).expand(randint+2, 3, randint+2);
        for (Entity e : world.getOtherEntities(player, box)) {
            if (!(e instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity) e;
            // 즉시 데미지
            DamageSource src = world.getDamageSources().playerAttack(player);
            living.damage(src, living.getMaxHealth()/7);
            KnockedAirborneVer2.giveKnockedAirborneVer2(living, 60,5);
            // 출혈 DOT

        }
    }
}
