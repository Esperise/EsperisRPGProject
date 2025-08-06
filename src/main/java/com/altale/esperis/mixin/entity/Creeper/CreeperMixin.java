package com.altale.esperis.mixin.entity.Creeper;

import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public class CreeperMixin  {
    @Shadow
    @Mutable
    private int currentFuseTime;
    @Shadow
    @Mutable
    private int fuseTime;
    @Inject(
            method = "explode",
            at=@At("HEAD"),
            cancellable = true
    )
    private void explode(CallbackInfo ci) {
        CreeperEntity creeper = (CreeperEntity)(Object)this;
        if (!creeper.getWorld().isClient) {
            creeper.getWorld().createExplosion(creeper, creeper.getX(), creeper.getY(), creeper.getZ(), 3 , World.ExplosionSourceType.MOB);
            this.currentFuseTime=0;
            this.fuseTime=15;
            ci.cancel();
        }
    }
}
