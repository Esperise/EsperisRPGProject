package com.altale.esperis.mixin;

import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlayerEntity.class)
public abstract class JumpCritInvalidationMixin {
    @ModifyConstant(
            method = "attack(Lnet/minecraft/entity/Entity;)V",
            constant= @Constant(floatValue = 1.5F)
    )

    private float invalidateCritDamage(float originalDamage) {
        System.out.println(originalDamage);
        return 1.05F;

    }
}
