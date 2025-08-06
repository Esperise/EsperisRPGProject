package com.altale.esperis.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Random;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    @Inject(method = "initialize", at = @At("TAIL"))
    private void multiplyMaxHealthOnSpawn(
            ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir
    ) {
        MobEntity self = (MobEntity)(Object)this;
        Random random = new Random();

        // 체력 속성 가져오기
        EntityAttributeInstance healthAttr = self.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        EntityAttributeInstance attackAttr = self.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        EntityAttributeInstance speedAttr = self.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (healthAttr != null) {
            double baseHealth = healthAttr.getBaseValue();
            double multiplier = 1.0 + self.getRandom().nextDouble();
            if(self instanceof HostileEntity){
                baseHealth+= self.getRandom().nextInt(15)+10;
                if(attackAttr != null){
                    double baseAttack= attackAttr.getBaseValue();
                    baseAttack += random.nextInt(7)+3;
                    attackAttr.setBaseValue(baseAttack);
                }
                if(speedAttr != null){
                    double baseSpeed= speedAttr.getBaseValue();
                    baseSpeed *= 1+( random.nextDouble()*3/4);
                    speedAttr.setBaseValue(baseSpeed);
                }

            }
            int newHealth = (int) Math.floor(baseHealth * multiplier);
            healthAttr.setBaseValue(newHealth);
            self.setHealth((float) newHealth);
        }
    }
}
