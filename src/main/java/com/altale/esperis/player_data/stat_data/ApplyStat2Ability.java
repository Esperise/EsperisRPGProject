package com.altale.esperis.player_data.stat_data;

import com.altale.esperis.items.ModItems;
import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerEquipmentStatComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ApplyStat2Ability {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            server.execute(() -> {
                if (!player.getCommandTags().contains("First_Join_Done")) {
                    player.giveItemStack(new ItemStack(ModItems.RED_POTION, 10));
                    player.giveItemStack(ModItems.SPECIAL_BOW_1.getDefaultStack());
                    player.giveItemStack(new ItemStack(ModItems.PURPLE_POTION, 128));
                    player.addCommandTag("First_Join_Done");
                }

                PlayerLevelComponent lvComponent = PlayerLevelComponent.KEY.get(player);
                if(lvComponent.getLevel()==1 && lvComponent.getCurrentExp()==0) {

//                    PlayerEquipmentStatComponent playerEquipmentStatComponent = PlayerEquipmentStatComponent.KEY.get(player);
//                    playerEquipmentStatComponent.initializeEquipmentStat(player);
                    StatManager.statUpdate(player);
                    ApplyStat2Ability.applyPlayerBaseAbility(player);
                }
//                ApplyStat2Ability.applyPlayerBaseAbility(player);
            });
            if(player.getMaxHealth()<= 1){

//                PlayerEquipmentStatComponent playerEquipmentStatComponent = PlayerEquipmentStatComponent.KEY.get(player);
//                playerEquipmentStatComponent.initializeEquipmentStat(player);
                StatManager.statUpdate(player);
                ApplyStat2Ability.applyPlayerBaseAbility(player);
            }
            else{
//                PlayerEquipmentStatComponent playerEquipmentStatComponent = PlayerEquipmentStatComponent.KEY.get(player);
//                playerEquipmentStatComponent.initializeEquipmentStat(player);
                StatManager.statUpdate(player);
                ApplyStat2Ability.applyPlayerBaseAbility(player);
            }
            if(player.hasNoGravity()){
                player.setNoGravity(false);
            }

        });
        ServerPlayerEvents.COPY_FROM.register((oldP, newP,alive) -> {
//            PlayerEquipmentStatComponent playerEquipmentStatComponent = PlayerEquipmentStatComponent.KEY.get(newP);
//            playerEquipmentStatComponent.initializeEquipmentStat(newP);
            StatManager.statUpdate(newP);
            ApplyStat2Ability.applyPlayerBaseAbility(newP);
//            PlayerEquipmentStatComponent oldPplayerEquipmentStatComponent = PlayerEquipmentStatComponent.KEY.get(oldP);
//            oldPplayerEquipmentStatComponent.initializeEquipmentStat(oldP);
//            StatManager.statUpdate(oldP);
//            ApplyStat2Ability.applyPlayerBaseAbility(oldP);

        });
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            PlayerEquipmentStatComponent playerEquipmentStatComponent = PlayerEquipmentStatComponent.KEY.get(player);
            playerEquipmentStatComponent.initializeEquipmentStat(player);
            ApplyStat2Ability.applyPlayerBaseAbility(player);
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldP, newP, alive) -> {
            PlayerLevelComponent lvComponent = PlayerLevelComponent.KEY.get(newP);
            PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(newP);
            double luk=playerFinalStatComponent.getFinalStat(StatType.LUK);
            int currExp = lvComponent.getCurrentExp();
            int maxExp = lvComponent.getMaxExp();
            ThreadLocalRandom random = ThreadLocalRandom.current();

            if(random.nextDouble()<0.03+(luk/(luk+1000))){
                newP.sendMessage(Text.literal("운이 좋아 경험치를 잃지 않았습니다"), true);
            }else{
                double lostExpCoeffi = Math.round( (Math.max(0.0, random.nextDouble()/4 ) *100) )/100.0;
                currExp = Math.max(0, currExp-(int)(maxExp*lostExpCoeffi));
                lvComponent.setCurrentExp(currExp);
            }

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
            float as= (float) statComponent.getFinalStat(StatType.ATTACK_SPEED);

            Objects.requireNonNull(movementSpdAttr).setBaseValue(0.1*(spd));
            Objects.requireNonNull(attackSpdAttr).setBaseValue(((4.0-2.4)*as+2.4));
            Objects.requireNonNull(attackDamageAttr).setBaseValue(atk+1);
            Objects.requireNonNull(maxHealthAttr).setBaseValue(maxHealth);
            player.setHealth(Math.min(player.getHealth(), maxHealth));

        }
    }

}
