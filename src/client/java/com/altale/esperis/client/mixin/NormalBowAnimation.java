package com.altale.esperis.client.mixin;

import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(HeldItemRenderer.class)
public class NormalBowAnimation {
    @ModifyConstant(
            method="renderFirstPersonItem",
            constant = @Constant(floatValue= 20.0f)
    )
    float modifyFirstPersonItem(float value) {
        return value*3;
    }
}
