package com.altale.esperis.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class InvalidateModifyApplyDamage {
    @Inject(
            method="modifyAppliedDamage(Lnet/minecraft/entity/damage/DamageSource;F)F",
            at=@At("HEAD"),
            cancellable = true
    )
    private void invalidateShield(DamageSource source,float amount, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(amount);
    }
}
