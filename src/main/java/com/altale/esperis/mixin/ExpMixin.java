package com.altale.esperis.mixin;

import com.altale.esperis.CallBack.ExpChangeCallBack;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class ExpMixin {
    @Inject(method = "addExperience", at =@At("HEAD"), cancellable = true)
    private void onAddExperience(int amount, CallbackInfo ci) {
        ExpChangeCallBack.EVENT.invoker().onExpChange((PlayerEntity) (Object) this, amount);
        ci.cancel();
    }
}
