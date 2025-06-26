package com.altale.esperis.skills;

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
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.joml.Vector3f;

import java.util.*;

import static java.lang.Math.max;
enum DotType{
    Burn, Bleed, Poison,ItemAbility
}
public class DotDamage {// dotDamage class를 통해 남은 시간과 데미지를 개별적으로 저장
    private static class DotData{
        int remainingTicks;
        int duration;
        float damagePerTick;
        float expectedTotalDamage;
        int tickDelta;
        DotType dotType;
        LivingEntity target;
        LivingEntity source;

        DotData(int remainingTicks,int tickDelta,float damagePerTicks, float expectTotalDamage, DotType dotType){//객체에 저장용
            this.remainingTicks = remainingTicks;
            this.damagePerTick = damagePerTicks;
            this.expectedTotalDamage =expectTotalDamage;// 총 지속 시간 * (4)틱당데미지 * 5(4틱*5=20틱=1초)
            this.tickDelta = tickDelta;
            this.dotType= dotType;
        }
        DotData(int remainingTicks,int tickDelta,float damagePerTicks, float expectTotalDamage, DotType dotType,LivingEntity target, LivingEntity source){
            this.remainingTicks = remainingTicks;
            this.damagePerTick = damagePerTicks;
            this.expectedTotalDamage =expectTotalDamage;// 총 지속 시간 * (4)틱당데미지 * 5(4틱*5=20틱=1초)
            this.tickDelta = tickDelta;
            this.dotType= dotType;
            this.target = target;
            this.source = source;
        }
    }

    //<dotDamage 대상 entity의 Uuid ,<데미지를 주는 entity의 Uuid, DotData(지속시간, 데미지)>>
    private static final Map<UUID,  Map<UUID, Map<DotType, List<DotData>>>> dotDamageMap= new HashMap<>();
//    private static final Map<UUID,  Map<UUID, DotData>> dotDamageMap= new HashMap<>();

    public static void register(){//main 에 등록해야함
        //서버 1틱 마다 아래 코드를 실행
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            Iterator<Map.Entry<UUID, Map<UUID, Map<DotType, List<DotData> >>>> dotTargetEntityIter= dotDamageMap.entrySet().iterator();
            while (dotTargetEntityIter.hasNext()) {
                //outer 대상uuid-map<데미지주는entityUuid,DotData> 형태
                Map.Entry<UUID, Map<UUID, Map<DotType, List<DotData> >>> outEntry= dotTargetEntityIter.next();
                UUID dotDamageTargetEntity_Uuid = outEntry.getKey();
                //server에서 uuid args를 LivingEntity로 변환
                LivingEntity dotDamageTargetEntity= getLivingEntityByUuid(server, dotDamageTargetEntity_Uuid);

                //데미지를 주는 대상이 없거나 죽었거나 사라졌으면 해당 entity에 대한 map을 비우고 continue
                if(dotDamageTargetEntity == null || !dotDamageTargetEntity.isAlive() || dotDamageTargetEntity.isRemoved()){
                    dotTargetEntityIter.remove();
                    continue;
                }
                Map<UUID, Map<DotType, List<DotData> >> damageSourceAndDotDataMap= outEntry.getValue();
                System.out.println("damageSourceAndDotDataMap.size(): "+damageSourceAndDotDataMap.size());
                Iterator<Map.Entry<UUID,Map<DotType, List<DotData> >>> damageSourceAndDotDataIter= damageSourceAndDotDataMap.entrySet().iterator();

                while(damageSourceAndDotDataIter.hasNext()){
                    //innerEntry: <대상 entity의 UUID, DotData>
                    Map.Entry<UUID,Map<DotType, List<DotData> >> innerEntry= damageSourceAndDotDataIter.next();
                    UUID damageSourceEntity_Uuid = innerEntry.getKey();

                    LivingEntity dotDamageSourceEntity = getLivingEntityByUuid(server, damageSourceEntity_Uuid);

                    // damageSource 결정 : dotDamageSourceEntity가 플레이어/그 외 일 때로 설정
                    DamageSource dotDamageSource = (dotDamageSourceEntity instanceof PlayerEntity)
                            ? dotDamageTargetEntity.getWorld().getDamageSources().playerAttack((PlayerEntity) dotDamageSourceEntity)
                            : dotDamageSourceEntity.getWorld().getDamageSources().mobAttack((dotDamageSourceEntity));

                    Map<DotType, List<DotData> > dotDataMap = innerEntry.getValue();// int remainingTicks와 float damagePerTick 를 가지는 객체를 가져옴
                    if(dotDataMap.isEmpty()){
                        damageSourceAndDotDataIter.remove();
                    }
                    Iterator<Map.Entry<DotType, List<DotData>>> dotTypeIter= dotDataMap.entrySet().iterator();
                    while(dotTypeIter.hasNext()){
                        Map.Entry<DotType, List<DotData>> dotTypeEntry= dotTypeIter.next();
                        DotType dotType= dotTypeEntry.getKey();
                        List<DotData> dotDataList= dotTypeEntry.getValue();
                        if(dotDataList.isEmpty() ){
                            dotTypeIter.remove();
                        }
                        Iterator<DotData> dataList= dotDataList.iterator();
                        while(dataList.hasNext()){
                            DotData data = dataList.next();
                            data.remainingTicks--;//남은 시간(틱) 1감소=(0.05초)
                            if(data.tickDelta == 0){
                                System.out.println("경고: tickDelta가 0으로 설정됨");
                                data.tickDelta= 5;
                            }
                            if((data.remainingTicks> 0) && (data.remainingTicks % data.tickDelta == 0)){ //남아있는 시간이 0이상이고, 0.1초마다

                                dotDamageTargetEntity.timeUntilRegen= 0;
                                dotDamageTargetEntity.hurtTime= 0;
                                dotDamageTargetEntity.damage(dotDamageSource,data.damagePerTick);
                                dotDamageTargetEntity.setVelocity(Vec3d.ZERO);       // 강제로 멈추기
                                dotDamageTargetEntity.velocityModified = true;       // 넉백 무효화
                                System.out.println("dotDamageTargetEntity: "+dotDamageTargetEntity);
                                System.out.println("dotDamageSource: "+dotDamageSource);
                                System.out.println("remainingTicks: "+data.remainingTicks);
                                System.out.println("damagePerTick: "+data.damagePerTick);
                                System.out.println("expectedTotalDamage: "+data.expectedTotalDamage);
                                if (dotDamageTargetEntity.getWorld() instanceof ServerWorld serverWorld) {
                                    Vec3d pos = dotDamageTargetEntity.getPos();
                                    serverWorld.spawnParticles(ParticleTypes.CRIT, pos.x, pos.y, pos.z, 2, 0.8, 1.0, 0.8, 0.1);
                                    serverWorld.spawnParticles(ParticleTypes.FALLING_DRIPSTONE_LAVA, pos.x, pos.y, pos.z, 5, 0.5, 0.8, 0.5, 0.3);
                                    serverWorld.spawnParticles(new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f),0.5f), pos.x, pos.y, pos.z, 20, 0.6, 0.8, 0.6, 0.1);
                                    dotDamageTargetEntity.getWorld().playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.PLAYERS, 1.4f, (float)1/data.tickDelta);
                                }
                            }

                            if(data.remainingTicks <=0){
                                dotDamageTargetEntity.timeUntilRegen= data.tickDelta;
                                dotDamageTargetEntity.hurtTime= data.tickDelta;
                                dotDamageTargetEntity.damage(dotDamageSource,data.damagePerTick);
                                float entityMaxHealth = dotDamageTargetEntity.getMaxHealth();
                                float EntityLostHealth = entityMaxHealth - dotDamageTargetEntity.getHealth();
                                float entityLossHealthCoefficient = (float) ((EntityLostHealth / entityMaxHealth) +1);
//                        dotDamageTargetEntity.damage(dotDamageSource, (float) (data.expectedTotalDamage*entityLossHealthCoefficient*0.2));//총 도트데미지*(1+잃은체력%)*0.2로 추가타
                                if (dotDamageTargetEntity.getWorld() instanceof ServerWorld serverWorld) {
                                    Vec3d pos = dotDamageTargetEntity.getPos();
                                    int bloodEffectAmount= (int)(12*((EntityLostHealth / entityMaxHealth)*30 +1));
                                    int dustEffectAmount= (int)(150*((EntityLostHealth / entityMaxHealth)*30 +1));
                                    serverWorld.spawnParticles(ParticleTypes.FALLING_DRIPSTONE_LAVA, pos.x, pos.y, pos.z, bloodEffectAmount, 0.7, 0.75, 0.7, 0.1);
                                    serverWorld.spawnParticles(new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f),0.5f), pos.x, pos.y, pos.z, dustEffectAmount, 1.0, 0.7, 1.0, 0.01);

                                    dotDamageTargetEntity.getWorld().playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 1.4f, 1.0f);
                                }
//                                damageSourceAndDotDataIter.remove();
                            }
                        }
                    }


//                    else{
//                        dotDamageMap.get(dotDamageTargetEntity_Uuid).put(damageSourceEntity_Uuid, DotData);
//                    }
                }
                if(damageSourceAndDotDataMap.isEmpty()) dotTargetEntityIter.remove();

            }

        });
    }
    public static void giveDotDamage(LivingEntity target , LivingEntity source,int duration,int tickDelta, float expectTotalDamage,DotType dotType){

        float damagePerTicks = expectTotalDamage * tickDelta / duration;// 총 데미지와 지속 시간, 틱 간격으로 틱 당 데미지 계산

        DotData newData= new DotData(duration, tickDelta, damagePerTicks, expectTotalDamage,dotType);
        dotDamageMap.computeIfAbsent(target.getUuid() , uuid -> new HashMap<>())
                .computeIfAbsent(source.getUuid(), uuid-> new HashMap<>())
                .computeIfAbsent(dotType, dT -> new ArrayList<>())
                .add(newData);
    }
    public static void giveDotDamage(World world, UUID targetUuid , UUID sourceUuid,int duration,int tickDelta, float expectTotalDamage,DotType dotType){

        float damagePerTicks = expectTotalDamage * tickDelta / duration;// 총 데미지와 지속 시간, 틱 간격으로 틱 당 데미지 계산
        LivingEntity targetEntity = getLivingEntityByUuid((ServerWorld) world, targetUuid);
        LivingEntity sourceEntity = getLivingEntityByUuid((ServerWorld) world, sourceUuid);
        DotData newData= new DotData(duration, tickDelta, damagePerTicks, expectTotalDamage,dotType,targetEntity,sourceEntity);
        dotDamageMap.computeIfAbsent(targetUuid , uuid -> new HashMap<>())
                .computeIfAbsent(sourceUuid , uuid-> new HashMap<>())
                .computeIfAbsent(dotType, dT -> new ArrayList<>())
                .add(newData);
    }
    public static LivingEntity getLivingEntityByUuid(net.minecraft.server.MinecraftServer server, UUID uuid){
        for(ServerWorld world : server.getWorlds()){
            LivingEntity entity = (LivingEntity) world.getEntity(uuid);
            if(entity != null) return entity;
        }
        return null;
    }
    public static LivingEntity getLivingEntityByUuid(ServerWorld world, UUID uuid){

            LivingEntity entity = (LivingEntity) world.getEntity(uuid);
            if(entity != null) return entity;

        return null;
    }
}
