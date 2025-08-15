package com.altale.esperis.skills.buff;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.skills.visualEffect.DrawCircle;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.*;

public class AbsorptionBuff {
    private static class AbsorptionData{
        int remainingTicks;
        int duration;
        float amount;
        LivingEntity entity;
        String skillId;
        ServerWorld serverWorld;
        AbsorptionData( int duration, float amount, LivingEntity entity ,String skillId, ServerWorld serverWorld){
            this.duration = duration;
            this.amount = amount;
            this.entity= entity;
            this.skillId= skillId;
            this.serverWorld = serverWorld;
            this.remainingTicks = duration;
        }
    }
                        //  uuid      skillId      amount   duration
    private static final Map<UUID, Map<String ,AbsorptionData>> absorptionBuff = new HashMap<>();
    private static final Map<UUID, Float> beforeAbsorptionBuffAmount = new HashMap<>();
    private static final Map<UUID, LivingEntity> uuidLivingEntityMap = new HashMap<>();
    public static void register(){
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<UUID, Map<String ,AbsorptionData>>> outerIter = absorptionBuff.entrySet().iterator();
            while (outerIter.hasNext()) {
                Map.Entry<UUID, Map<String ,AbsorptionData>> outerEntry = outerIter.next();
                UUID uuid = outerEntry.getKey();
                float beforeAbsorptionAmount;
                float currentAbsorptionAmount;
                if(beforeAbsorptionBuffAmount.containsKey(uuid)){
                    beforeAbsorptionAmount = beforeAbsorptionBuffAmount.get(uuid);
                    currentAbsorptionAmount = Optional.ofNullable(uuidLivingEntityMap.get(uuid)).map(LivingEntity::getAbsorptionAmount).orElse(0f);
                }else{
                    currentAbsorptionAmount= 0;
                    beforeAbsorptionAmount = 0;
                }
                //저장된 전 값 가져옴
                float currentAbsorptionBuffAmount=0;
                Iterator<Map.Entry<String, AbsorptionData>> innerIter = outerEntry.getValue().entrySet().iterator();
                while(innerIter.hasNext()){
                    Map.Entry<String, AbsorptionData> innerEntry = innerIter.next();
                    String skillId = innerEntry.getKey();
                    AbsorptionData data = innerEntry.getValue();
                    if(data.entity == null || !data.entity.isAlive() || data.entity.isRemoved()){
                        innerIter.remove();
                        continue;
                    }
                    data.remainingTicks--;
                    if(data.remainingTicks <= 0){
                        data.amount=0;
                    } else{
                        currentAbsorptionBuffAmount += data.amount;
                    }
                    // 별도의 제거 루프
                    if (data.amount == 0 && data.remainingTicks <= 0) {
                        innerIter.remove();
                    }

                }//outerIter end
                float totalAmount=0;
                if(currentAbsorptionAmount-beforeAbsorptionAmount < 0){//현재 총 흡수량 - map에 저장된 전 흡수 버프량 -> 음수나오면
                    float reductionAmount =  beforeAbsorptionAmount - currentAbsorptionAmount;//감소 해야할 양(양수)

                    Iterator<Map.Entry<String, AbsorptionData>> reductionIter = outerEntry.getValue().entrySet().iterator();
                    while(reductionIter.hasNext()){ //uuid에 대한 iter skillId-data 구조 하나 하나에서 data의 값을 변경시키기 위함
                        AbsorptionData data = reductionIter.next().getValue();
                        if(data.amount > reductionAmount){//현재 iter에서의 data.amount(=흡수 버프량)이 감소해야할 버프량보다 크면
                            data.amount -= reductionAmount;//흡수 버프량 -= 감소해야할 버프량
                            totalAmount += data.amount; //totalAmount에 흡수 버프량 저장 -> totalAmount 는  setAbsorption 에 사용
                        }else{
                            // 특정 스킬의 흡수량이 감소해야할 값 보다 작을때 -> 해당 data 삭제후 남는 값 전달해야함
                            reductionAmount -= data.amount;
                            reductionIter.remove();//현재 iter삭제(현재 반복자의 skillId-data를 삭제 시킴)
                        }
                    }
                    beforeAbsorptionBuffAmount.put(uuid, totalAmount);
                    uuidLivingEntityMap.get(uuid).setAbsorptionAmount(totalAmount);
                    LivingEntity entity = uuidLivingEntityMap.get(uuid);
                    ServerWorld world= (ServerWorld) entity.getWorld();
                    //모든 버프 순회후 흡수량 총합 적용
                } else {
                    Iterator<Map.Entry<String, AbsorptionData>> reductionIter = outerEntry.getValue().entrySet().iterator();
                    float red = 1.0f;
                    float blue= 1.0f;
                    float green = 1.0f;
                    float dustSize=0.15f;
                    while (reductionIter.hasNext()) {
                        AbsorptionData data = reductionIter.next().getValue();
                        totalAmount += data.amount;
                        if(data.skillId.equals(SkillsId.STR_100.getSkillName())){
                            red = 1f;
                            blue= 0.2f;
                            green= 0.2f;
                            dustSize= 0.35f;
                        }
                        else if(data.skillId.equals(SkillsId.LUK_150.getSkillName())){
                            red=1f;
                            blue=0.2f;
                            green=1f;
                        }
                    }
                    beforeAbsorptionBuffAmount.put(uuid, totalAmount);
                    uuidLivingEntityMap.get(uuid).setAbsorptionAmount(totalAmount);
                    LivingEntity entity = uuidLivingEntityMap.get(uuid);
                    ServerWorld world= (ServerWorld) entity.getWorld();

                    DrawCircle.spawnSphereAroundBarrier(entity, world,24,red,green,blue,dustSize,1);//계속 나오는게 얘임
                }
                if(totalAmount==0){
                    LivingEntity entity = uuidLivingEntityMap.get(uuid);
                    ServerWorld world= (ServerWorld) entity.getWorld();
                    world.playSound(null,entity.getX(),entity.getY(),entity.getZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS,5.0f,1.0f);
                    outerIter.remove();//uuid 아래 모든 skillId-data삭제
                    uuidLivingEntityMap.remove(uuid);
                    beforeAbsorptionBuffAmount.remove(uuid);

                }
            }

            });
    }

    public static void giveAbsorptionBuff(ServerWorld serverWorld, LivingEntity entity, String skillId , float amount , int duration){
        AbsorptionData newData= new AbsorptionData(duration, amount, entity,skillId,serverWorld);
        absorptionBuff.computeIfAbsent(entity.getUuid(), k -> new HashMap<>())
                .put(skillId, newData);
        uuidLivingEntityMap.putIfAbsent(entity.getUuid(), entity);
    }
}
