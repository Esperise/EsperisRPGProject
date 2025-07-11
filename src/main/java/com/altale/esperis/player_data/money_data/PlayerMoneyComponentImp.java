package com.altale.esperis.player_data.money_data;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.lang.reflect.Array;

public class PlayerMoneyComponentImp implements PlayerMoneyComponent, AutoSyncedComponent {

    private final PlayerEntity player;
    private int balance=0;

    public PlayerMoneyComponentImp(PlayerEntity player) {
        this.player = player;
    }


    @Override
    public void setBalance(int balance) {
        this.balance = balance;
        PlayerMoneyComponent.KEY.sync(this.player);
    }

    @Override
    public int getBalance() {
        PlayerMoneyComponent.KEY.sync(this.player);
        return balance;
    }

    @Override
    public int[] withdraw(int amount) {
        int[] arr= {balance, amount};
        if(balance < amount){
            arr[1]= 0;
        } else{
            balance -= amount;
            setBalance(balance);
            arr[0]= balance;
        }

        return arr;
    }


    @Override
    public int[] deposit(int amount) {
        this.balance += amount;
        setBalance(balance);
        System.out.println("deposit: " + amount);
        return new int[]{balance, amount};
    }

    @Override
    public boolean canWithdraw(int amount) {
        if(balance < amount){
            return false;
        }
        return true;
    }

    public int deposit(String wrongInput) {
        return 0;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        balance = nbtCompound.getInt("balance");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putInt("balance", balance);
    }
}
