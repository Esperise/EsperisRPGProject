package com.altale.esperis.player_data.stat_data.StatComponents;

import com.altale.esperis.player_data.stat_data.StatPointType;
import com.altale.esperis.player_data.stat_data.StatType;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stat;

import java.util.EnumMap;
import java.util.Map;

public class PlayerFinalStatComponentImp implements PlayerFinalStatComponent, AutoSyncedComponent {
    private final PlayerEntity player;
    private final Map<StatType, Double> FinalStatMap = new EnumMap<>(StatType.class);
    public PlayerFinalStatComponentImp(PlayerEntity player) {
        this.player = player;
        for(StatType statType: StatType.values()){
            FinalStatMap.put(statType, 0.0);
        }
    }


    @Override
    public void setFinalStat(StatType statType, double value) {
        FinalStatMap.put(statType, value);
        PlayerFinalStatComponent.KEY.sync(this.player);
    }

    @Override
    public void setAllFinalStat() {
        PlayerFinalStatComponent.KEY.sync(this.player);
    }


    @Override
    public double getFinalStat(StatType statType) {
        return FinalStatMap.get(statType);
    }
    @Override
    public Map<StatType, Double> getAllFinalStat() {
        Map<StatType, Double> allStatsMap = new EnumMap<>(StatType.class);
        for(StatType statType: StatType.values()){
            allStatsMap.put(statType, FinalStatMap.getOrDefault(statType,0.0));
        }
        return allStatsMap;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        if(nbtCompound.contains("FinalStats")) {
            NbtCompound finalStatsTag = nbtCompound.getCompound("FinalStats");
            for(StatType statType: StatType.values()){
                FinalStatMap.put(statType, finalStatsTag.getDouble(statType.name()));
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        NbtCompound finalStatsTag = new NbtCompound();
        for(Map.Entry<StatType, Double> entry: FinalStatMap.entrySet()){
            finalStatsTag.putDouble(entry.getKey().toString(), entry.getValue());
        }
        nbtCompound.put("FinalStats", finalStatsTag);
    }
}
