package com.altale.esperis.client.mixin;

import com.altale.esperis.items.ModItems;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class PlayerSightMixin {
    @Inject(
            method="getNightVisionStrength",
            at=@At("HEAD"),
            cancellable = true
    )
    private static void getNightVisionStrength(LivingEntity entity, float tickDelta, CallbackInfoReturnable<Float> cir) {
        if(entity.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            StatusEffectInstance statusEffectInstance = entity.getStatusEffect(StatusEffects.NIGHT_VISION);
            float hasNightVision =  !statusEffectInstance.isDurationBelow(200) ? 1.0F : 0.7F + MathHelper.sin(((float)statusEffectInstance.getDuration() - tickDelta) * (float)Math.PI * 0.2F) * 0.3F;
            cir.setReturnValue(hasNightVision);
            return;
        }
        boolean bl = false;
        float visionBrightness = 0.0F;
        int count =0;
        for(ItemStack stack : entity.getHandItems()){
            if(count != 1){
                if(stack.isOf(Items.TORCH)){
                    visionBrightness += 0.025F;
                    count ++;
                }else if(stack.isOf(ModItems.TOMORI)){
                    visionBrightness += 0.07F;
                    count ++;
                }
            }
        }
        cir.setReturnValue(visionBrightness);
    }
}
