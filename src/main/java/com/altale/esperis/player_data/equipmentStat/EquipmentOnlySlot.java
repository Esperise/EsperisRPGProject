package com.altale.esperis.player_data.equipmentStat;

import com.altale.esperis.items.itemFunction.SpecialBowItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;

public class EquipmentOnlySlot extends Slot {
    public EquipmentOnlySlot(Inventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }
    @Override
    public boolean canInsert(ItemStack stack) {
        Item item = stack.getItem();
        if(item instanceof ArmorItem) return true;
        if(item instanceof ToolItem) return true;
        if(item instanceof SwordItem) return true;
        if(item instanceof SpecialBowItem) return true;
        return false;
    }
    @Override
    public boolean canTakeItems(PlayerEntity player){
        return true;
    }

}
