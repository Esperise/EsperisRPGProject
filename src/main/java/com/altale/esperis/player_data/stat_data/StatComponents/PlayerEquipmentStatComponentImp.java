package com.altale.esperis.player_data.stat_data.StatComponents;

import com.altale.esperis.player_data.stat_data.StatType;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.EnumMap;
import java.util.Map;

public class PlayerEquipmentStatComponentImp implements PlayerEquipmentStatComponent , AutoSyncedComponent {
    static final Map<StatType, Double> EquipmentStatsMap= new EnumMap<>(StatType.class);
    private final PlayerEntity player;
    public PlayerEquipmentStatComponentImp(PlayerEntity player) {
        this.player = player;
        for(StatType statType : StatType.values()) {
            EquipmentStatsMap.put(statType, 0.0);
        }
    }
    @Override
    public void setEquipmentStat(StatType statType, double amount) {
        EquipmentStatsMap.put(statType, amount);
        PlayerEquipmentStatComponent.KEY.sync(this.player);
    }

    @Override
    public double getEquipmentStat(StatType statType) {
        return EquipmentStatsMap.getOrDefault(statType,0.0);
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        if(nbtCompound.contains("EquipmentStats")) {
            NbtCompound equipmentStatsTag = nbtCompound.getCompound("EquipmentStats");
            for(StatType statType: StatType.values()){
                if(equipmentStatsTag.contains(statType.name())){
                    EquipmentStatsMap.put(statType,equipmentStatsTag.getDouble(statType.name()));
                }
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        NbtCompound equipmentStatsTag = new NbtCompound();
        for(Map.Entry<StatType, Double> entry: EquipmentStatsMap.entrySet()){
            equipmentStatsTag.putDouble(entry.getKey().name(), entry.getValue());
            equipmentStatsTag.putDouble(entry.getKey().name(), entry.getValue());
        }
        nbtCompound.put("EquipmentStats", equipmentStatsTag);
    }
}
