package com.altale.esperis.skills.debuff;
import com.altale.esperis.serverSide.Utilities.HealthHanlder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.lang.ref.WeakReference;
import java.util.*;

import static java.lang.Math.max;

public class DotDamageVer2 {
    // dotDamage class를 통해 남은 시간과 데미지를 개별적으로 저장
    private static class DotData {
        int remainingTicks;
        int duration;
        float damagePerNTick;
        float expectedTotalDamage;
        int tickDelta;
        boolean overlap;
        String Id;
        DotTypeVer2 dotTypeVer2;
        WeakReference<LivingEntity> targetRef;
        WeakReference<LivingEntity> sourceRef;

        DotData(int remainingTicks, int tickDelta, float damagePerNTicks, float expectTotalDamage, DotTypeVer2 dotTypeVer2,
                LivingEntity target, LivingEntity source, String Id) {
            this(remainingTicks, tickDelta,  damagePerNTicks,  expectTotalDamage,  dotTypeVer2,
                    target, source,  false , Id);
        }
        DotData(int remainingTicks, int tickDelta, float damagePerNTicks, float expectTotalDamage, DotTypeVer2 dotTypeVer2,
                LivingEntity target, LivingEntity source, boolean overlap, String Id) {
            this.remainingTicks = remainingTicks;
            this.duration = remainingTicks;
            this.damagePerNTick = damagePerNTicks;
            this.expectedTotalDamage = expectTotalDamage;
            this.tickDelta = tickDelta;
            this.dotTypeVer2 = dotTypeVer2;
            this.overlap= overlap;
            this.Id = Id;
            this.targetRef = new WeakReference<>(target);
            this.sourceRef = new WeakReference<>(source);
        }
    }

    // <dotDamage 대상 entity의 Uuid ,<데미지를 주는 entity의 Uuid, DotType → List<DotData>>>
    private static final Map<UUID, Map<UUID, Map<DotTypeVer2, List<DotData>>>> dotDamageMap = new HashMap<>();

    public static void register() {
        // 서버 1틱 마다 아래 코드를 실행
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<UUID, Map<UUID, Map<DotTypeVer2, List<DotData>>>>> dotTargetEntityIter = dotDamageMap.entrySet().iterator();
            while (dotTargetEntityIter.hasNext()) {
                Map.Entry<UUID, Map<UUID, Map<DotTypeVer2, List<DotData>>>> outEntry = dotTargetEntityIter.next();
                Map<UUID, Map<DotTypeVer2, List<DotData>>> damageSourceAndDotDataMap = outEntry.getValue();

                Iterator<Map.Entry<UUID, Map<DotTypeVer2, List<DotData>>>> damageSourceAndDotDataIter = damageSourceAndDotDataMap.entrySet().iterator();
                while (damageSourceAndDotDataIter.hasNext()) {
                    Map.Entry<UUID, Map<DotTypeVer2, List<DotData>>> innerEntry = damageSourceAndDotDataIter.next();
                    Map<DotTypeVer2, List<DotData>> dotDataMap = innerEntry.getValue();

                    Iterator<Map.Entry<DotTypeVer2, List<DotData>>> dotTypeIter = dotDataMap.entrySet().iterator();
                    while (dotTypeIter.hasNext()) {
                        Map.Entry<DotTypeVer2, List<DotData>> dotTypeEntry = dotTypeIter.next();
                        List<DotData> dotDataList = dotTypeEntry.getValue();
                        Iterator<DotData> dataList = dotDataList.iterator();
                        while (dataList.hasNext()) {
                            DotData data = dataList.next();
                            LivingEntity target = data.targetRef.get();
                            LivingEntity source = data.sourceRef.get();
                            // 참조 소멸 또는 사망한 경우 제거
                            if (target == null || !target.isAlive() || target.isRemoved() ||
                                    source==null ||!source.isAlive() || source.isRemoved()) {
                                dataList.remove();
                                continue;
                            }
                            // DOT 처리
                            data.remainingTicks--;
                            if (data.tickDelta <= 0) {
                                data.tickDelta = 5;
                            }
                            if (data.remainingTicks > 0 && data.remainingTicks % data.tickDelta == 0) {
                                // 데미지 및 넉백 무효화
                                target.timeUntilRegen = 0;
                                target.hurtTime = 0;
                                DamageSource ds = (source instanceof PlayerEntity)
                                        ? target.getWorld().getDamageSources().playerAttack((PlayerEntity) source)
                                        : target.getWorld().getDamageSources().mobAttack(source);
                                if(data.dotTypeVer2.equals(DotTypeVer2.DamageSource_Generic)){
                                    ds = target.getDamageSources().generic();
                                }
                                target.damage(ds, data.damagePerNTick);
                                if(data.dotTypeVer2 != DotTypeVer2.DamageSource_Generic) {
                                    target.setVelocity(Vec3d.ZERO);
                                    target.velocityModified = true;
                                }


                                // 파티클 & 사운드
                                if (target.getWorld() instanceof ServerWorld serverWorld) {
                                    Vec3d pos = target.getPos();
                                    serverWorld.spawnParticles(ParticleTypes.CRIT, pos.x, pos.y, pos.z,
                                            2, 0.8, 1.0, 0.8, 0.1);
                                    serverWorld.spawnParticles(ParticleTypes.FALLING_DRIPSTONE_LAVA,
                                            pos.x, pos.y, pos.z, 5, 0.5, 0.8, 0.5, 0.3);
                                    serverWorld.spawnParticles(new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f),0.5f),
                                            pos.x, pos.y, pos.z, 20, 0.6, 0.8, 0.6, 0.1);
//                                    target.getWorld().playSound(null, pos.x, pos.y, pos.z,
//                                            SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT,
//                                            SoundCategory.PLAYERS, 1.4f, 1f / data.tickDelta);
                                }
                            }

                            // 종료 시 마지막 데미지
                            if (data.remainingTicks <= 0) {
                                target.timeUntilRegen = 0;
                                target.hurtTime = 0;
                                DamageSource ds = (source instanceof PlayerEntity)
                                        ? target.getWorld().getDamageSources().playerAttack((PlayerEntity) source)
                                        : target.getWorld().getDamageSources().mobAttack(source);
                                target.damage(ds, data.damagePerNTick);
                                target.setVelocity(Vec3d.ZERO);
                                target.velocityModified = true;
                                float maxHp = target.getMaxHealth();
                                float lost = maxHp - target.getHealth();
                                float coef = (lost / maxHp) + 1;
                                dataList.remove();
                            }
                        }
                        // 해당 리스트가 비었으면 DotType 제거
                        if (dotDataList.isEmpty()) {
                            dotTypeIter.remove();
                        }
                    }
                    // 해당 공격자 맵이 비었으면 제거
                    if (dotDataMap.isEmpty()) {
                        damageSourceAndDotDataIter.remove();
                    }
                }
                // 대상 엔티티 전체 map이 비었으면 제거
                if (damageSourceAndDotDataMap.isEmpty()) {
                    dotTargetEntityIter.remove();
                }
            }
        });
    }

    public static void giveDotDamage(LivingEntity target, LivingEntity source,
                                        int duration, int tickDelta, float expectTotalDamage, DotTypeVer2 dotTypeVer2, boolean overlap,float overlapCoeff, String Id) {
        float damagePerNTicks = expectTotalDamage * tickDelta / duration;
        DotData newData = new DotData(duration, tickDelta, damagePerNTicks,
                expectTotalDamage, dotTypeVer2, target, source,overlap,Id);
        if(overlap){
            boolean found = false;
            List<DotData> tempList=dotDamageMap.computeIfAbsent(target.getUuid(), u -> new HashMap<>())
                    .computeIfAbsent(source.getUuid(), u -> new HashMap<>())
                    .computeIfAbsent(dotTypeVer2, d -> new ArrayList<>())
                    ;
            if(!tempList.isEmpty()){
                for (DotData data : tempList) {
                    if (data.Id.equals(Id)) {
                        float remainingDamage = (float) (data.remainingTicks * data.tickDelta) / duration;
                        data.remainingTicks = duration;
                        data.expectedTotalDamage = remainingDamage +  (expectTotalDamage * overlapCoeff) ;
                        data.damagePerNTick = data.expectedTotalDamage * data.tickDelta / data.duration;
//                        data.damagePerNTick += damagePerNTicks*overlapCoeff;
                        found= true;
                        break;
                    }
                }
            }
            if(!found){
                dotDamageMap.computeIfAbsent(target.getUuid(), u -> new HashMap<>())
                        .computeIfAbsent(source.getUuid(), u -> new HashMap<>())
                        .computeIfAbsent(dotTypeVer2, d -> new ArrayList<>())
                        .add(newData);
            }


        }
        else{
            if(!dotDamageMap.isEmpty()) {
                if(dotDamageMap.containsKey(target.getUuid())) {
                    List<DotData> dataList = dotDamageMap.get(target.getUuid()).get(source.getUuid()).get(dotTypeVer2);
                    if(dataList == null){
                        dotDamageMap.computeIfAbsent(target.getUuid(), u -> new HashMap<>())
                                .computeIfAbsent(source.getUuid(), u -> new HashMap<>())
                                .computeIfAbsent(dotTypeVer2, d -> new ArrayList<>())
                                .add(newData);
                    }
                    else{
                        Iterator<DotData> dataIter = dataList.iterator();
                        while(dataIter.hasNext()){
                            DotData data= dataIter.next();
                            if(data.Id.equals(Id)) {
                                if (data.remainingTicks < duration) {
                                    data.remainingTicks += tickDelta;
                                }
                            }
                }
            }


            }
        }
        else{
                dotDamageMap.computeIfAbsent(target.getUuid(), u -> new HashMap<>())
                        .computeIfAbsent(source.getUuid(), u -> new HashMap<>())
                        .computeIfAbsent(dotTypeVer2, d -> new ArrayList<>())
                        .add(newData);
            }

    }
}
    public static void giveDotDamage(LivingEntity target, LivingEntity source
            , int duration, int tickDelta, float expectTotalDamage, DotTypeVer2 dotTypeVer2, String Id) {
        float damagePerNTicks = expectTotalDamage * tickDelta / duration;
        DotData newData = new DotData(duration, tickDelta, damagePerNTicks,
                expectTotalDamage, dotTypeVer2, target, source,Id);

            if(!dotDamageMap.isEmpty()) {
                if(dotDamageMap.containsKey(target.getUuid())) {
                    List<DotData> dataList = dotDamageMap.get(target.getUuid()).get(source.getUuid()).get(dotTypeVer2);
                    if(dataList == null){
                        dotDamageMap.computeIfAbsent(target.getUuid(), u -> new HashMap<>())
                                .computeIfAbsent(source.getUuid(), u -> new HashMap<>())
                                .computeIfAbsent(dotTypeVer2, d -> new ArrayList<>())
                                .add(newData);
                    }
                    else{
                        Iterator<DotData> dataIter = dataList.iterator();
                        while(dataIter.hasNext()){
                            DotData data= dataIter.next();
                            if(data.Id.equals(Id)) {
                                if (data.remainingTicks < duration) {
                                    data.remainingTicks += tickDelta;
                                }
                            }
                        }
                    }



            }
            else{
                dotDamageMap.computeIfAbsent(target.getUuid(), u -> new HashMap<>())
                        .computeIfAbsent(source.getUuid(), u -> new HashMap<>())
                        .computeIfAbsent(dotTypeVer2, d -> new ArrayList<>())
                        .add(newData);
            }

        }
    }
    public static boolean isDotDamage(LivingEntity target){
        if(dotDamageMap.containsKey(target.getUuid())){
            return dotDamageMap.containsKey(target.getUuid());
        }
        else{
            return false;
        }
    }
    public static void instantDotDamage(LivingEntity target, LivingEntity damageSource, double additionalCoeff){
        if(isDotDamage(target)){
            UUID uuid = target.getUuid();
            float totalDotDamage=0;
            Map<UUID, Map<DotTypeVer2, List<DotData>>> dotMap= dotDamageMap.get(uuid);
            Iterator<Map.Entry<UUID,Map<DotTypeVer2,List<DotData>>>> iter = dotMap.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry<UUID,Map<DotTypeVer2,List<DotData>>> outerEntry = iter.next();
                Iterator<Map.Entry<DotTypeVer2,List<DotData>>> innerIter = outerEntry.getValue().entrySet().iterator();
                while(innerIter.hasNext()){
                    Map.Entry<DotTypeVer2,List<DotData>> innerEntry = innerIter.next();
                    List<DotData> dataList = innerEntry.getValue();
                    Iterator<DotData> dataIter = dataList.iterator();
                    while(dataIter.hasNext()){
                        DotData data= dataIter.next();
                        if(data.dotTypeVer2.equals(DotTypeVer2.DamageSource_Generic)) continue;
                        float remainDotDamage= data.remainingTicks*data.damagePerNTick /data.tickDelta;
                        totalDotDamage+=remainDotDamage;
                    }

                }

            }
            if(totalDotDamage>=0){
                dotDamageMap.remove(uuid);
                DamageSource ds = (damageSource instanceof PlayerEntity)
                        ? target.getWorld().getDamageSources().playerAttack((PlayerEntity) damageSource)
                        : target.getWorld().getDamageSources().mobAttack(damageSource);
                target.damage(ds, totalDotDamage* HealthHanlder.getLostHealthRatio(target
                        ,true,additionalCoeff));
                if (target.getWorld() instanceof ServerWorld serverWorld) {
                    Vec3d pos = target.getPos();
                    int blood = (int)(15*((HealthHanlder.getLostHealthRatio(target,false))*50+1));
                    int dust = (int)(150*((HealthHanlder.getLostHealthRatio(target,false))*30+1));
                    serverWorld.spawnParticles(ParticleTypes.FALLING_DRIPSTONE_LAVA,
                            pos.x, pos.y, pos.z, blood, 0.7, 0.75, 0.7, 0.0);
                    serverWorld.spawnParticles(new DustParticleEffect(new Vector3f(1.0f,0.0f,0.0f),0.5f),
                            pos.x, pos.y, pos.z, dust, 1.0, 0.7, 1.0, 0.0);
                    target.getWorld().playSound(null, pos.x, pos.y, pos.z,
                            SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST,
                            SoundCategory.PLAYERS, 3.4f, 1.0f);
                }

            }

        }
    }
}

