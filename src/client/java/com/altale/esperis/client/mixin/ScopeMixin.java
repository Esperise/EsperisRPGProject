package com.altale.esperis.client.mixin;

import com.altale.esperis.client.HUD.Scope;
import com.altale.esperis.client.cache.UseScopeCache;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


    @Mixin(GameRenderer.class)
    public abstract class ScopeMixin {
        @ModifyReturnValue(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", at = @At("RETURN"))
        private double applyScopeZoom(double original) {
            if (UseScopeCache.isUseScope() && UseScopeCache.getScopeTime()>0) {
//                double mul = MathHelper.lerp(Scope.zoomProgress, 1.0, 0.10); // 대략 10배 줌
                double mul = Math.max(0.25 , 1- (UseScopeCache.getScopeTime()* 0.1));
                return original * mul;
            }
            return original;
        }
    }
