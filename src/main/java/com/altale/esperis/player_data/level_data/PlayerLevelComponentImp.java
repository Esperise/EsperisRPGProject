package com.altale.esperis.player_data.level_data;

import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class PlayerLevelComponentImp implements PlayerLevelComponent, AutoSyncedComponent {
    private final PlayerEntity player;
    int level=1;
    int currentExp=0;
    int maxExp=50;

    public PlayerLevelComponentImp(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level= level;
        PlayerLevelComponent.KEY.sync(this.player);
    }

    @Override
    public int getCurrentExp() {
        return currentExp;
    }

    @Override
    public void setCurrentExp(int exp) {
        this.currentExp= exp;
        PlayerLevelComponent.KEY.sync(this.player);
    }

    @Override
    public int getMaxExp() {
        return maxExp;
    }

    @Override
    public void setMaxExp(int maxExp) {
        this.maxExp= maxExp;
        PlayerLevelComponent.KEY.sync(this.player);
    }

    @Override
    public boolean canLevelUp() {
        if(this.currentExp >= maxExp){
            return true;
        }
        return false;
    }
    public void levelUp(){
            setLevel(this.level+1);
            setCurrentExp(this.currentExp-this.maxExp);
            setMaxExp((int) (this.maxExp * 1.1));
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        level=nbtCompound.getInt("level");
        currentExp=nbtCompound.getInt("currentExp");
        maxExp=nbtCompound.getInt("maxExp");

    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putInt("level", level);
        nbtCompound.putInt("currentExp", currentExp);
        nbtCompound.putInt("maxExp", maxExp);
    }
}
