package com.altale.esperis.mixin.entity;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    @Inject(method = "initialize", at = @At("TAIL"))
    private void multiplyMaxHealthOnSpawn(
            ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir
    ) {
        MobEntity self = (MobEntity)(Object)this;
        Random random = new Random();
        Difficulty currentDifficulty = world.getDifficulty();
        int hpBoost = 0;
        int atkBoost= 0;
        switch(currentDifficulty){
            case EASY ->{
                hpBoost = self.getRandom().nextInt(8)+8;
                atkBoost = self.getRandom().nextInt(5);
            }
            case NORMAL ->{
                hpBoost = self.getRandom().nextInt(10)+10;
                atkBoost = self.getRandom().nextInt(6)+2;
            }
            case HARD ->{
                hpBoost = self.getRandom().nextInt(15)+20;
                atkBoost = self.getRandom().nextInt(8)+4;
            }
        }

        // 체력 속성 가져오기
        EntityAttributeInstance healthAttr = self.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        EntityAttributeInstance attackAttr = self.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        EntityAttributeInstance speedAttr = self.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        EntityAttributeInstance attackSpeedAttr = self.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);
        if (healthAttr != null) {
            double baseHealth = healthAttr.getBaseValue();
            double multiplier = 1.0 + self.getRandom().nextDouble();
            if(self instanceof HostileEntity){
                baseHealth+= hpBoost;
                if(attackAttr != null){
                    double baseAttack= attackAttr.getBaseValue();
                    baseAttack += atkBoost;
                    attackAttr.setBaseValue(baseAttack);
                }
                if(speedAttr != null && !self.isBaby()){
                    double baseSpeed= speedAttr.getBaseValue();
                    baseSpeed *= 1+( random.nextDouble()*3/4);
                    speedAttr.setBaseValue(baseSpeed);
                }
                if(attackSpeedAttr != null && !self.isBaby()){
                    double baseAttackSpeed= attackSpeedAttr.getBaseValue();
                    baseAttackSpeed += random.nextFloat(1f);
                    attackSpeedAttr.setBaseValue(baseAttackSpeed);
                }

            }
            int newHealth = (int) Math.floor(baseHealth * multiplier);
            healthAttr.setBaseValue(newHealth);
            self.setHealth((float) newHealth);
        }
    }
}
