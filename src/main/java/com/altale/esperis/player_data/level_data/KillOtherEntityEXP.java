package com.altale.esperis.player_data.level_data;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;


public class KillOtherEntityEXP {
    public static void register() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, killer, killedEntity)->{
            if(killer instanceof PlayerEntity player){
                if(killedEntity instanceof PlayerEntity killedPlayer){
                    // 아무 이벤트 x
                }
                // 어떤엔티티가 플레이어가 아니고 이 엔티티를 죽인 엔티티가 플레이어 일때:
                PlayerLevelComponent playerLevelComponent = PlayerLevelComponent.KEY.get(player);
                float killedEntityMaxHealth = killedEntity.getMaxHealth();
                if(killedEntity instanceof Monster monster){
                    String id = killedEntity.getUuid().toString();
                    switch (id){
                        case "minecraft:creeper"
                                -> playerLevelComponent.addExp(10);
                        case "minecraft:pillager", "minecraft:skeleton"
                                -> playerLevelComponent.addExp(12);
                        case "minecraft:blaze", "minecraft:drowned", "minecraft:stray", "minecraft:phantom"
                                -> playerLevelComponent.addExp(27);
                        case "minecraft:piglin_brute"
                                -> playerLevelComponent.addExp(30);
                        case "minecraft:ghast"
                                -> playerLevelComponent.addExp(50);
                        case "minecraft:enderman", "minecraft:vindicator", "minecraft:guardian"
                                -> playerLevelComponent.addExp(100);
                        case "minecraft:wither_skeleton"
                                -> playerLevelComponent.addExp(120);
                        case "minecraft:vex"
                                -> playerLevelComponent.addExp(200);
                        case "minecraft:ravager"
                                -> playerLevelComponent.addExp(500);
                        case "minecraft:endermite"
                                -> playerLevelComponent.addExp(1777);
                        case "minecraft:wither", "minecraft:elder_guardian"
                                -> playerLevelComponent.addExp(2000);
                        case "minecraft:warden"
                                -> playerLevelComponent.addExp(10000);

                        default -> playerLevelComponent.addExp((int) killedEntityMaxHealth/4);
                    }
                }

            }else{

            }


        });
    }
}
