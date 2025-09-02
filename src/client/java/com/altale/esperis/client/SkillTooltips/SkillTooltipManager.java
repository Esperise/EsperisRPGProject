package com.altale.esperis.client.SkillTooltips;

import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class SkillTooltipManager {
    public static boolean hasSkillTooltip(ItemStack stack){
        if(stack.hasNbt()){
            NbtCompound nbt = stack.getOrCreateNbt();
            return nbt.contains("SkillTooltip");
        }
        return false;
    }
    public static boolean canMakeTooltip(ItemStack stack){
//        if(stack.getItem() instanceof SkillTooltipItem) return true;
        //SkillTooltipItem 추가해야함
        return false;
    }

}
