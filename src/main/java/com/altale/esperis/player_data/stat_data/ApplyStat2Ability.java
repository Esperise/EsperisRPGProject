package com.altale.esperis.player_data.stat_data;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public class ApplyStat2Ability {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            server.execute(() -> {
                PlayerLevelComponent lvComponent = PlayerLevelComponent.KEY.get(player);
                if(lvComponent.getLevel()==1 && lvComponent.getCurrentExp()==0) {
                    StatManager.statUpdate(player);
                    ApplyStat2Ability.applyPlayerBaseAbility(player);
                }
                ApplyStat2Ability.applyPlayerBaseAbility(player);
            });
            if(player.getMaxHealth()<= 1){
                StatManager.statUpdate(player);
                ApplyStat2Ability.applyPlayerBaseAbility(player);
            }
            else{
                StatManager.statUpdate(player);
                ApplyStat2Ability.applyPlayerBaseAbility(player);
            }


        });
        ServerPlayerEvents.COPY_FROM.register((oldP, newP,alive) -> {
            if(newP.getMaxHealth() <=0 ) {

                ApplyStat2Ability.applyPlayerBaseAbility(newP);
                newP.setHealth(newP.getMaxHealth());

            }else {
                ApplyStat2Ability.applyPlayerBaseAbility(newP);
                newP.setHealth(newP.getMaxHealth());
            }

        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldP, newP, alive) -> {
            if(newP.getMaxHealth() <=0 ) {
//                applyMaxHealthByFinalStat(newP);
                ApplyStat2Ability.applyPlayerBaseAbility(newP);
                newP.setHealth(newP.getMaxHealth());

            }else {
//                    applyMaxHealthByFinalStat(newP);
                ApplyStat2Ability.applyPlayerBaseAbility(newP);
                newP.setHealth(newP.getMaxHealth());
            }
        });
    }
    public static void applyPlayerBaseAbility(ServerPlayerEntity player) {
        PlayerFinalStatComponent statComponent = PlayerFinalStatComponent.KEY.get(player);
        EntityAttributeInstance movementSpdAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        EntityAttributeInstance attackDamageAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        EntityAttributeInstance attackSpdAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);
        EntityAttributeInstance maxHealthAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if( movementSpdAttr != null || attackDamageAttr != null || maxHealthAttr != null ) {

            float spd= (float) statComponent.getFinalStat(StatType.SPD);
            float atk= (float) statComponent.getFinalStat(StatType.ATK);
            float maxHealth= (float) statComponent.getFinalStat(StatType.MAX_HEALTH);

            Objects.requireNonNull(movementSpdAttr).setBaseValue(0.1*(spd));
            Objects.requireNonNull(attackSpdAttr).setBaseValue(((4.0-2.4)*spd+2.4));
            Objects.requireNonNull(attackDamageAttr).setBaseValue(atk+1);
            Objects.requireNonNull(maxHealthAttr).setBaseValue(maxHealth);
            player.setHealth(Math.min(player.getHealth(), maxHealth));

        }
    }

}
