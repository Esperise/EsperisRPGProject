package com.altale.esperis.items.itemFunction;

import com.altale.esperis.player_data.equipmentStat.EquipmentInfoManager;
import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ExpCoupon extends Item {
    public final int expAmount;
    public ExpCoupon(int expAmount) {
        super(new FabricItemSettings().maxCount(64));
        this.expAmount = expAmount;
    }
    public ExpCoupon(){
        this(150);
    }
    public  int getExpAmount(Item item){
        return expAmount;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        int expAmount = 0;
        if(!world.isClient) {
            NbtCompound nbt = stack.getOrCreateNbt();
            if(nbt.contains("expAmount", 3)){
                PlayerLevelComponent playerLevelComponent = PlayerLevelComponent.KEY.get(user);
                expAmount = nbt.getInt("expAmount");
                playerLevelComponent.addExp(expAmount);
                stack.decrement(1);
            }
        }
        return super.use(world, user, hand);
    }
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        return stack;
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        // 서버에서, 플레이어 인벤토리에 들어간 첫 틱에만 동작
        if (!world.isClient
                && entity instanceof PlayerEntity
                && !stack.hasNbt()) {
            stack.getOrCreateNbt().putInt("expAmount",expAmount);

        }
    }
}
