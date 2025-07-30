package com.altale.esperis.player_data.stat_data.StatComponents;

import com.altale.esperis.player_data.stat_data.StatType;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public interface PlayerEquipmentStatComponent extends Component {
    ComponentKey<PlayerEquipmentStatComponent> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            new Identifier("esperis", "player_equipment_stat_component"), PlayerEquipmentStatComponent.class
    );
    void setEquipmentStat(StatType statType, double amount);
    double getEquipmentStat(StatType statType);
    void addEquipmentStat(StatType statType, double amount);
    void changeEquipment(PlayerEntity player, EquipmentSlot changedSlot, ItemStack previous, ItemStack current);
    void initializeEquipmentStat(PlayerEntity player);

}
