package com.altale.esperis.items.itemFunction.Artifact;

import com.altale.esperis.player_data.equipmentStat.EquipmentInfoManager;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class Tomori extends Item {
    public Tomori() {
        super(new FabricItemSettings().maxCount(1).fireproof());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        ItemCooldownManager cooldownManager= user.getItemCooldownManager();
        if(cooldownManager.isCoolingDown(this)){
            return TypedActionResult.fail(stack);
        }
        if(!world.isClient){
            AbsorptionBuff.giveAbsorptionBuff((ServerWorld) world, user, "Tomori", 10+user.getMaxHealth()*3/20,100);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 2));
            cooldownManager.set(this, 500 );
        }
        return super.use(world, user, hand);
    }
    @Override
    public Text getName(ItemStack stack){
        if(EquipmentInfoManager.hasEquipmentInfo(stack)){

            return Text.literal("tomori");
        }else{
            String custom = "tomori";

            return Text.literal(custom);
        }
    }
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        Map<StatType , Double> map = new HashMap<>();
        map.put(StatType.DEF, 15.0);
        map.put(StatType.MAX_HEALTH, 20.0);
        map.put(StatType.ATTACK_SPEED, 0.25);
        EquipmentInfoManager.setEquipmentInfo(stack, 10, 4,20,7,map);
        return stack;
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        // 서버에서, 플레이어 인벤토리에 들어간 첫 틱에만 동작
        if (!world.isClient
                && entity instanceof PlayerEntity
                && (!stack.hasNbt() || !EquipmentInfoManager.hasEquipmentInfo(stack))) {
            // 초기 Stat 세팅
            Map<StatType, Double> map = new HashMap<>();
            map.put(StatType.DEF, 15.0);
            map.put(StatType.MAX_HEALTH, 20.0);
            map.put(StatType.ATTACK_SPEED, 0.25);
            EquipmentInfoManager.setEquipmentInfo(stack, 10, 4, 20, 7, map);
        }
    }

}

