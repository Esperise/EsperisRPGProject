package com.altale.esperis.mixin;
import com.altale.esperis.accessor.CustomAbsorptionAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
// 추적 가능한 보호막 mixin 생성
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements CustomAbsorptionAccessor {
    @Unique
    private static final TrackedData<Float> CUSTOM_ABSORPTION =
        DataTracker.registerData(LivingEntityMixin.class, TrackedDataHandlerRegistry.FLOAT);

    protected LivingEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void onInitDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(CUSTOM_ABSORPTION, 0.0f);
    }

    @Unique
    public void setCustomAbsorption(float value) {
        this.dataTracker.set(CUSTOM_ABSORPTION, value);
    }

    @Unique
    public float getCustomAbsorption() {
        return this.dataTracker.get(CUSTOM_ABSORPTION);
    }

}
