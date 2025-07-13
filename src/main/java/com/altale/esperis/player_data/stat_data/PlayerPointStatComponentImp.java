package com.altale.esperis.player_data.stat_data;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class PlayerPointStatComponentImp implements PlayerPointStatComponent,AutoSyncedComponent {
    private final PlayerEntity player;
    public PlayerPointStatComponentImp(PlayerEntity player) {this.player = player;}
    int atk=1;
    int maxHealth=20;
    int def=0;
    int str=0;
    int dex=0;
    int luk=0;
    int dur=0;
    int spd = 100; //기본 이동속도 계수 : 100%


    @Override
    public void setAtk(int atkValue) {
        this.atk = atkValue;
        PlayerPointStatComponent.KEY.sync(this.player);
    }

    @Override
    public void setMaxHealth(int maxHealthValue) {
        this.maxHealth = maxHealthValue;
        PlayerPointStatComponent.KEY.sync(this.player);
    }

    @Override
    public void setDef(int defValue) {
        this.def = defValue;
        PlayerPointStatComponent.KEY.sync(this.player);
    }

    @Override
    public void setStr(int strValue) {
        this.str = strValue;
        PlayerPointStatComponent.KEY.sync(this.player);

    }

    @Override
    public void setDex(int dexValue) {
        this.dex = dexValue;
        PlayerPointStatComponent.KEY.sync(this.player);
    }

    @Override
    public void setLuk(int lukValue) {
        this.luk = lukValue;
        PlayerPointStatComponent.KEY.sync(this.player);
    }

    @Override
    public void setDur(int durValue) {
        this.dur = durValue;
        PlayerPointStatComponent.KEY.sync(this.player);
    }


    @Override
    public void setSpd(int spdValue) {
        this.spd = spdValue;
        PlayerPointStatComponent.KEY.sync(this.player);

    }

    @Override
    public int getAtk() {
        return this.atk;
    }

    @Override
    public int getMaxHealth() {
        return this.maxHealth;
    }

    @Override
    public int getDef() {
        return this.def;
    }

    @Override
    public int getStr() {
        return this.str;
    }

    @Override
    public int getDex() {
        return 0;
    }

    @Override
    public int getLuk() {
        return 0;
    }

    @Override
    public int getDur() {
        return 0;
    }

    @Override
    public int getSpd() {
        return 0;
    }

    //add stat
    @Override
    public void addStat(String strType, int statValue) {
        switch (strType) {
            case "atk":
                setAtk(getAtk()+statValue);
                break;
            case "def":
                setAtk(getDef()+statValue);
                break;
            case "dex":
                setDex(getDex()+statValue);
                break;
            case "luk":
                setLuk(getLuk()+statValue);
                break;
            case "dur":
                setDur(getDur()+statValue);
                break;
            case "spd":
                setSpd(getSpd()+statValue);
                break;
            default:
                break;
        }
    }

    @Override
    public void subtractStat(String strType, int statValue) {
        switch (strType) {
            case "atk":
                setAtk(getAtk()-statValue);
                break;
            case "def":
                setAtk(getDef()-statValue);
                break;
            case "dex":
                setDex(getDex()-statValue);
                break;
            case "luk":
                setLuk(getLuk()-statValue);
                break;
            case "dur":
                setDur(getDur()-statValue);
                break;
            case "spd":
                setSpd(getSpd()-statValue);
                break;
            default:
                break;
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        atk = nbtCompound.getInt("atk");
        def = nbtCompound.getInt("def");
        maxHealth = nbtCompound.getInt("maxHealth");
        dex = nbtCompound.getInt("dex");
        luk = nbtCompound.getInt("luk");
        dur = nbtCompound.getInt("dur");
        spd = nbtCompound.getInt("spd");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putInt("atk", atk);
        nbtCompound.putInt("def", def);
        nbtCompound.putInt("maxHealth", maxHealth);
        nbtCompound.putInt("dex", dex);
        nbtCompound.putInt("luk", luk);
        nbtCompound.putInt("dur", dur);
        nbtCompound.putInt("spd", spd);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
