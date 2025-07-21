package com.altale.esperis.mixin;

import com.altale.esperis.CallBack.CalculateDamageCallBack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class CalculateDamageMixin {
    @Inject(method="applyArmorToDamage", at=@At("HEAD"), cancellable = true)
    public void applyDefDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir){
        float damage= CalculateDamageCallBack.EVENT.invoker().calculateDamage(source,(LivingEntity)(Object)this,amount );
        cir.setReturnValue(damage);

    }
}
