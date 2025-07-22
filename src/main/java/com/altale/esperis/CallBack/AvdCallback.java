package com.altale.esperis.CallBack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;


//공격자가 있는 피해에 대한 회피-> AvdCallback->이 method로 구현-> 0.0F returncallback이 0.0F 리턴
// -> 최종적으로 AvdMixin에 변수에 대입-> 이 변수로 조건 분기-> damage method자체를 취소
public  interface AvdCallback {
    Event<AvdCallback> EVENT = EventFactory.createArrayBacked(AvdCallback.class, listeners ->
            (attacker,target, amount) -> {
        float bl = amount;
        for(AvdCallback callback : listeners) {
            bl = callback.damageAvd(attacker, target,amount);//callback.damageAvd가 true-> bl-> return true
        }
        return bl;
    });
    float damageAvd(DamageSource attacker, LivingEntity target, float amount);
}
