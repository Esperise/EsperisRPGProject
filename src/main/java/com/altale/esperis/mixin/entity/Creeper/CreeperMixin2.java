package com.altale.esperis.mixin.entity.Creeper;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(CreeperIgniteGoal.class)
public class CreeperMixin2 {

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "CONSTANT",
                    args = "doubleValue=49.0"
            )
    )
    private double modifyFuseCancelDistance(double original) {
        return 25.0;
    }
}
