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

            while(levelComponent.canLevelUp()){
                PlayerPointStatComponent pointStatComponent = PlayerPointStatComponent.KEY.get(player);
                pointStatComponent.addSP(StatPointType.UnusedSP, 5);
                pointStatComponent.addSP(StatPointType.TotalSP, 5);
                levelComponent.levelUp();
                StatManager.statUpdate((ServerPlayerEntity) player);

                //레벨업 소리 이펙트
                Vec3d pos= player.getPos();
                player.getWorld().playSound(
                        null,pos.x,pos.y,pos.z,
                        SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE, SoundCategory.PLAYERS,5.0f,1.0f
                );
            }
        });
    }
}
