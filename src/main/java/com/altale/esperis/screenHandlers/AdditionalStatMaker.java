package com.altale.esperis.screenHandlers;

import com.altale.esperis.player_data.equipmentStat.EquipmentOnlySlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;


public class AdditionalStatMaker extends ScreenHandler {
    private final Inventory statMakerInventory;
    public AdditionalStatMaker(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.ADDITIONAL_STAT_MAKER, syncId);
        this.statMakerInventory= new SimpleInventory(1);
        this.statMakerInventory.onOpen(playerInventory.player);
        this.addSlot(new EquipmentOnlySlot(statMakerInventory, 0, 138, 91));

        for (int row=0; row<3; row++) {
            for (int col=0; col<9; col++) {
                int index = col+row*9 +9;
                int x= 30+col*18;
                int y= 123+row*18;
                this.addSlot(new Slot(playerInventory, index, x, y));
            }
        }
        for(int col=0; col<9; col++){
            this.addSlot(new Slot(playerInventory, col, 30+col*18, 181));
        }

    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (index == 0) {
                // 커스텀 슬롯 -> 플레이어 인벤토리로 이동
                if (!this.insertItem(originalStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 플레이어 인벤토리 -> 커스텀 슬롯 (0번 슬롯)
                if (!this.insertItem(originalStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (!player.getWorld().isClient()) {
            this.dropInventory(player, this.statMakerInventory);
        }
    }
}
