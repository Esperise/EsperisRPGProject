package com.altale.esperis.player_data.stat_data;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

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
    public void readFromNbt(NbtCompound nbtCompound) {

    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {

    }
}
