package com.altale.esperis.player_data.equipmentStat;

import com.altale.esperis.items.ModItems;
import com.altale.esperis.items.itemFunction.Artifact.Tomori;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerEquipmentStatComponent;
import com.altale.esperis.player_data.stat_data.StatManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class DetectPlayerEquipmentChange {
    public static void register(){
        ServerEntityEvents.EQUIPMENT_CHANGE.register(
                (livingEntity, slot, previous, current) -> {
                    if(livingEntity instanceof PlayerEntity player){
                        boolean isMainOrArmor = slot == EquipmentSlot.MAINHAND || slot.isArmorSlot();

                        // 2) 오프핸드는 tomori만 허용
                        boolean isOffhandTomori =
                                slot == EquipmentSlot.OFFHAND &&
                                        (previous.getItem() == ModItems.TOMORI || current.getItem() == ModItems.TOMORI);

                        if (!isMainOrArmor && !isOffhandTomori) {
                            // 메인핸드·방어구도 아니고, offhand의 tomori도 아니면 무시
                            return;
                        }

                        // 통계 갱신
                        PlayerEquipmentStatComponent equipmentStatComponent =
                                PlayerEquipmentStatComponent.KEY.get(player);

                        // 장착 정보가 EquipmentInfoManager에 있으면 일반 로직
                        if (EquipmentInfoManager.hasEquipmentInfo(previous) || EquipmentInfoManager.hasEquipmentInfo(current)) {
                            equipmentStatComponent.changeEquipment(player, slot, previous, current);
                        } else {
                            // tomori offhand 전환 같은 특수 처리
                            if (previous.getItem() == ModItems.TOMORI) {
                                equipmentStatComponent.changeEquipment(player, slot, previous, ItemStack.EMPTY);
                            } else if (current.getItem() == ModItems.TOMORI) {
                                equipmentStatComponent.changeEquipment(player, slot, ItemStack.EMPTY, current);
                            }
                        }

                        // 플레이어 스탯 업데이트
                        StatManager.statUpdate((ServerPlayerEntity) player);

                    }
                }
        );
    }
}
