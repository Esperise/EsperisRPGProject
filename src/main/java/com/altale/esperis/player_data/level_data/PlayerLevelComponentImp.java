package com.altale.esperis.player_data.level_data;

import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerPointStatComponent;
import com.altale.esperis.player_data.stat_data.StatManager;
import com.altale.esperis.player_data.stat_data.StatPointType;
import com.altale.esperis.skills.buff.HealBuff;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

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
    public void addExp(int exp) {
        this.currentExp+= exp;
        while(canLevelUp()){
            PlayerPointStatComponent pointStatComponent = PlayerPointStatComponent.KEY.get(player);
            pointStatComponent.addSP(StatPointType.UnusedSP, 5);
            pointStatComponent.addSP(StatPointType.TotalSP, 5);
            levelUp();
            StatManager.statUpdate((ServerPlayerEntity) player);
            HealBuff.giveHealBuff(player, 100, 5, player.getMaxHealth(),"레벨업 체력 회복");

            //레벨업 소리 이펙트
            Vec3d pos= player.getPos();
            player.getWorld().playSound(
                    null,pos.x,pos.y,pos.z,
                    SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE, SoundCategory.PLAYERS,5.0f,1.0f
            );
        }
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
        setMaxExp((int) (this.maxExp * Math.max(1.12,(1.27-(getLevel()/100.0)))));

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
