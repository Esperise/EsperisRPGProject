package com.altale.esperis.player_data.level_data;

import com.altale.esperis.CallBack.ExpChangeCallBack;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerPointStatComponent;
import com.altale.esperis.player_data.stat_data.StatManager;
import com.altale.esperis.player_data.stat_data.StatPointType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class PlayerCustomExpSystem {
    public static void register(){
        ExpChangeCallBack.EVENT.register((player, amount)->{
            PlayerLevelComponent levelComponent = PlayerLevelComponent.KEY.get(player);

            levelComponent.addExp(amount);

        });
    }
}
