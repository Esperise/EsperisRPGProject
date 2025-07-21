package com.altale.esperis.mixin;

import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerEntity.class)
public abstract class JumpCritInvalidationMixin {
    @ModifyArg(
            method = "attack(Lnet/minecraft/entity/Entity;)V",
            at= @At(
                    value = "INVOKE",
                    target ="Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
        ),
            index=1
    )

    private float invalidateCritDamage(  float originalDamage) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        PlayerFinalStatComponent damagerPFSC= PlayerFinalStatComponent.KEY.get(player);
        double atk= damagerPFSC.getFinalStat(StatType.ATK);
        float amount= (float) atk;
        return amount;

    }
}
