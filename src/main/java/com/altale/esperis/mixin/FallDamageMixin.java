package com.altale.esperis.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class FallDamageMixin {
    @ModifyArgs(
            method ="handleFallDamage",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;computeFallDamage(FF)I")
    )
    private void fallDistance(Args args){
        float fallDistance = args.get(0);
        float newFallDistance = fallDistance - 7;
        args.set(0, newFallDistance);
    }
}
