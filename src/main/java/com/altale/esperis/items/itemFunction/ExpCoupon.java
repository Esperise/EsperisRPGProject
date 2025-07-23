package com.altale.esperis.items.itemFunction;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ExpCoupon extends Item {
    public ExpCoupon() {
        super(new FabricItemSettings().maxCount(64));
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        int expAmount = 0;
        if(!world.isClient) {
            NbtCompound nbt = stack.getNbt();
            if(nbt.contains("expAmount", 3)){
                PlayerLevelComponent playerLevelComponent = PlayerLevelComponent.KEY.get(user);
                expAmount = nbt.getInt("expAmount");
                playerLevelComponent.addExp(expAmount);
                stack.decrement(1);

            }
        }
        return super.use(world, user, hand);
    }
}
