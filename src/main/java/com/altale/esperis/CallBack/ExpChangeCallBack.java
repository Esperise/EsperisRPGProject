package com.altale.esperis.CallBack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface ExpChangeCallBack {
    Event<ExpChangeCallBack> EVENT = EventFactory.createArrayBacked(
            ExpChangeCallBack.class,
            listeners -> (player, amount) -> {
                for(ExpChangeCallBack callBack : listeners){
                    callBack.onExpChange(player, amount);
                }
            });
    void onExpChange(PlayerEntity player, int amount);
}
