package com.altale.esperis.CallBack;


import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public interface CalculateDamageCallBack {
    Event<CalculateDamageCallBack> EVENT = EventFactory.createArrayBacked(
            CalculateDamageCallBack.class,
            listeners->(attacker, target, damageAmount )->{
                for(CalculateDamageCallBack callback : listeners) {
                    damageAmount = callback.calculateDamage(attacker, target, damageAmount);
                }
                return damageAmount;
            }
    );
    float calculateDamage(DamageSource attacker, LivingEntity target, float damageAmount);
}
