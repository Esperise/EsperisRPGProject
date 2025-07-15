package com.altale.esperis.player_data.stat_data;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class PlayerPointStatComponentImp implements PlayerPointStatComponent,AutoSyncedComponent {
    private final PlayerEntity player;
    private final Map<StatType,Double> statMap = new EnumMap<>(StatType.class);
    public PlayerPointStatComponentImp(PlayerEntity player) {
        this.player = player;
        for (StatType statType: StatType.values()){
            statMap.put(statType,0.0);
        }
    }


    @Override
    public void setPointStat(StatType statType, double amount) {
        statMap.put(statType, amount);
        PlayerPointStatComponent.KEY.sync(this.player);
    }

    @Override
    public double getPointStat(StatType statType) {
        return statMap.getOrDefault(statType, 0.0);
    }

    @Override
    public void addStat(StatType statType, double statValue) {
        double beforePointStat= getPointStat(statType);
        setPointStat(statType, beforePointStat + statValue);
    }

    @Override
    public void subtractStat(StatType statType, double statValue) {
        double beforePointStat= getPointStat(statType);
        setPointStat(statType, beforePointStat-statValue);
    }


    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        if(nbtCompound.contains("PointStats")) {
            NbtCompound statTag = nbtCompound.getCompound("PointStats");
            for(StatType statType: StatType.values()){
                if(statTag.contains(statType.name())){
                    statMap.put(statType,statTag.getDouble(statType.name()));
                }
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        NbtCompound statTag = new NbtCompound();
        for(Map.Entry<StatType, Double> entry: statMap.entrySet()){
            statTag.putDouble(entry.getKey().name(), entry.getValue());
        }
        nbtCompound.put("PointStats", statTag);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
