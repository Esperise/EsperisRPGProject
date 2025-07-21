package com.altale.esperis.mixin;

import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @ModifyConstant(
            method="update(Lnet/minecraft/entity/player/PlayerEntity;)V",
            constant=@Constant(intValue = 0, ordinal = 0)
            )
    private int replaceValue(int original){
        return 20;
    }
}
