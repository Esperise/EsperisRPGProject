package com.altale.esperis.client.mixin;

import com.altale.esperis.client.cache.UseScopeCache;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public abstract class InGameHudSpyglassMixin {
    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/client/gui/DrawContext;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingSpyglass()Z"
            )
    )
    private boolean forceSpyglassOverlay(boolean original) {
        // 서버에서 온 신호(UseScopeCache)가 true면 바닐라 오버레이가 그려지도록 강제
        return original || (UseScopeCache.isUseScope() && UseScopeCache.getScopeTime() > 0);
    }
}
