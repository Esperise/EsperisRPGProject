package com.altale.esperis.mixin;

import net.minecraft.item.BowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BowItem.class)
public abstract class NormalBowAttackSpeedMixin {
    @ModifyConstant(
            method="getPullProgress",
            constant = @Constant(floatValue = 20.0F)
    )
    private static float nerfPullProgress(float original) {
        return original*5;
    }
}

