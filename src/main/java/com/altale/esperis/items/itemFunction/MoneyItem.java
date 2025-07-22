package com.altale.esperis.items.itemFunction;

import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class MoneyItem extends Item {
    public MoneyItem() {
        super(new FabricItemSettings().maxCount(64));
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
        ItemStack stack= user.getStackInHand(hand);
        int amount =0;
        if(stack.hasNbt()){
            PlayerMoneyComponent playerMoneyComponent = PlayerMoneyComponent.KEY.get(user);
            NbtCompound nbt = stack.getNbt();
            if(!world.isClient()){
                stack.decrement(1);
                if(nbt.contains("amount",3)){
                    amount = nbt.getInt("amount");
                    playerMoneyComponent.deposit(amount);
                    user.sendMessage(Text.literal(String.format("%d esp 입금, 현재: %d",amount,playerMoneyComponent.getBalance())), false);
                }
            }

        }

        return super.use(world, user, hand);
    }
}
