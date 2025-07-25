package com.altale.esperis.player_data.equipmentStat;

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
        super(ModScreenHandlers.ADDITIONAL_STAT_MAKER_SCREEN_HANDLER, syncId);
        this.statMakerInventory= new SimpleInventory(1);
        this.statMakerInventory.onOpen(playerInventory.player);
        this.addSlot(new EquipmentOnlySlot(statMakerInventory, 0, 80, 35));

        for (int row=0; row<3; row++) {
            for (int col=0; col<3; col++) {
                int index = col+row*9 +9;
                int x= 8+col*18;
                int y= 84+row*18;
                this.addSlot(new Slot(playerInventory, index, x, y));
            }
        }
        for(int col=0; col<9; col++){
            this.addSlot(new Slot(playerInventory, col, 8+col*18, 142));
        }

    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return false;
    }
}
