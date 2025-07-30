package com.altale.esperis.player_data.equipmentStat;

import com.altale.esperis.player_data.stat_data.StatComponents.PlayerEquipmentStatComponent;
import com.altale.esperis.player_data.stat_data.StatManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class DetectPlayerEquipmentChange {
    public static void register(){
        ServerEntityEvents.EQUIPMENT_CHANGE.register(
                (livingEntity, slot, previous, current) -> {
                    if(livingEntity instanceof PlayerEntity player){
                        if(slot.isArmorSlot() || slot.getName().equals("mainhand")){
                            if(EquipmentInfoManager.hasEquipmentInfo(previous) || EquipmentInfoManager.hasEquipmentInfo(current)){
                                PlayerEquipmentStatComponent equipmentStatComponent = PlayerEquipmentStatComponent.KEY.get(player);
                                equipmentStatComponent.changeEquipment(player, slot, previous , current);
                                StatManager.statUpdate((ServerPlayerEntity) player);
                            }
                        }//주손이고 방어구 슬롯에서만 변경 적용

                    }
                }
        );
    }
}
