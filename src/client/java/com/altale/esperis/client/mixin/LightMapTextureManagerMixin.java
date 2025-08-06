package com.altale.esperis.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapTextureManager.class)
public class LightMapTextureManagerMixin {
    @ModifyExpressionValue(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target= "Lnet/minecraft/client/network/ClientPlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z",
                    ordinal = 0
            )
    )
    private boolean modifyHasNightVision(boolean original) {
        return true;
    }
}
