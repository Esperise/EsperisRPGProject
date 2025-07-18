package com.altale.esperis.player_data.stat_data;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ApplyMaxHealth {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            server.execute(() -> {
                PlayerLevelComponent lvComponent = PlayerLevelComponent.KEY.get(player);
                if(lvComponent.getLevel()==1 && lvComponent.getCurrentExp()==0) {
                    StatManager.statUpdate(player);
                    applyMaxHealthByFinalStat(player);
                }
                applyMaxHealthByFinalStat(player);
            });
            if(player.getMaxHealth()<= 1){
                StatManager.statUpdate(player);
                applyMaxHealthByFinalStat(player);
            }
            else{
                StatManager.statUpdate(player);
                applyMaxHealthByFinalStat(player);
            }


        });
        ServerPlayerEvents.COPY_FROM.register((oldP, newP,alive) -> {
            if(newP.getMaxHealth() <=0 ) {

                applyMaxHealthByFinalStat(newP);
                newP.setHealth(newP.getMaxHealth());

            }else {
                    applyMaxHealthByFinalStat(newP);
                    newP.setHealth(newP.getMaxHealth());
                }

        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldP, newP, alive) -> {
            if(newP.getMaxHealth() <=0 ) {
                applyMaxHealthByFinalStat(newP);
                newP.setHealth(newP.getMaxHealth());

            }else {
                    applyMaxHealthByFinalStat(newP);
                    newP.setHealth(newP.getMaxHealth());
                }
        });
    }
    public static void applyMaxHealthByFinalStat(ServerPlayerEntity player) {
        PlayerFinalStatComponent statComponent = PlayerFinalStatComponent.KEY.get(player);
            EntityAttributeInstance maxHealthAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if( maxHealthAttr != null ){
                float maxHealth= (float) statComponent.getFinalStat(StatType.MAX_HEALTH);
                maxHealthAttr.setBaseValue(maxHealth);
                player.setHealth(Math.min(player.getHealth(), maxHealth));
            }
    }
}
