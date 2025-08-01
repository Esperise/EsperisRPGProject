package com.altale.esperis.items.itemFunction;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;



public class PotionInstantHeal extends Item {
    public PotionInstantHeal() {
        super(new FabricItemSettings().maxCount(64));
        }
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
        ItemStack stack = user.getStackInHand(hand);
        if(user.getItemCooldownManager().isCoolingDown(this) || hand.equals(Hand.OFF_HAND)){
            return TypedActionResult.fail(stack);
        }
            if(!world.isClient()){
                PlayerLevelComponent levelComponent= PlayerLevelComponent.KEY.get(user);
                int level = levelComponent.getLevel();
                float healAmount= user.getMaxHealth()*0.07F + 12;
                user.heal(healAmount);
                world.playSound(
                        null,
                        user.getX(), user.getY(), user.getZ(),
                        SoundEvents.ENTITY_GENERIC_DRINK,
                        SoundCategory.PLAYERS, 1.0F, 1.0F

                );
                ((ServerWorld) user.getWorld()).spawnParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        user.getX(), user.getY(), user.getZ(), 35, 0.75,1.5,0.75,0
                );

                user.getItemCooldownManager().set(this, 160 );
                stack.decrement(1);
            }
        return super.use(world, user, hand);
    }

}
