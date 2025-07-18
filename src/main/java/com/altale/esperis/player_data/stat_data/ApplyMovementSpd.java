package com.altale.esperis.player_data.stat_data;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class ApplyMovementSpd {
    public static void register(){
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            server.execute(() -> {
                PlayerLevelComponent lvComponent = PlayerLevelComponent.KEY.get(player);
                if(lvComponent.getLevel()==1 && lvComponent.getCurrentExp()==0) {
                    StatManager.statUpdate(player);
                    applyBaseSpeed(player);
                }
                applyBaseSpeed(player);
            });
            if(player.getMovementSpeed()<= 0){
                StatManager.statUpdate(player);
                applyBaseSpeed(player);
            }
            else{
                StatManager.statUpdate(player);
                applyBaseSpeed(player);
            }


        });
        ServerPlayerEvents.COPY_FROM.register((oldP, newP, alive) -> {
            if(newP.getMovementSpeed() <=0 ) {

                applyBaseSpeed(newP);

            }else {
                    applyBaseSpeed(newP);

                }

        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldP, newP, alive) -> {
            if(newP.getMovementSpeed() <=0 ) {
                applyBaseSpeed(newP);


            }else {
                    applyBaseSpeed(newP);

                }
        });
    }

    private static void applyBaseSpeed(ServerPlayerEntity player) {
        PlayerFinalStatComponent statComponent = PlayerFinalStatComponent.KEY.get(player);
            EntityAttributeInstance movementSpdAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if( movementSpdAttr != null ){
                float spd= (float) statComponent.getFinalStat(StatType.SPD);
                movementSpdAttr.setBaseValue(0.1*(spd));

            }
    }

}
