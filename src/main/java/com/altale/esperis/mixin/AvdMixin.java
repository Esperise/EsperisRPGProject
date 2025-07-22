package com.altale.esperis.mixin;

import com.altale.esperis.CallBack.AvdCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class AvdMixin {
    @Inject(method="damage", at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"), cancellable = true)
    public void avdDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        float bl= AvdCallback.EVENT.invoker().damageAvd(source, (LivingEntity) (Object) this, amount);//return-> TRUE이면 bl: true
        if(bl<=0.0F){
            cir.setReturnValue(false);
        }
    }
}
