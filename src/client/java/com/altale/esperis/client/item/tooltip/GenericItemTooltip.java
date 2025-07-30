package com.altale.esperis.client.item.tooltip;

import com.altale.esperis.client.item.toolTipManager.TooltipManager;
import com.altale.esperis.player_data.equipmentStat.EquipmentInfoManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;



@Environment(EnvType.CLIENT)
public class GenericItemTooltip {
    public  static void register(){
        ItemTooltipCallback.EVENT.register((stack, context, tooltip) -> {

            if(TooltipManager.canMakeTooltip(stack)){
                if(EquipmentInfoManager.hasEquipmentInfo(stack)){
                    tooltip.addAll(TooltipManager.makeStatText(stack));
                }

            }
        });
    }
}
