package com.altale.esperis.player_data.level_data;

import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;



public class KillOtherEntityEXP {
    public static void register() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, killer, killedEntity)->{
            if(killer instanceof PlayerEntity player){
                if(killedEntity instanceof PlayerEntity killedPlayer){
                    // 아무 이벤트 x
                }
                // 어떤엔티티가 플레이어가 아니고 이 엔티티를 죽인 엔티티가 플레이어 일때:
                PlayerLevelComponent playerLevelComponent = PlayerLevelComponent.KEY.get(player);
                PlayerMoneyComponent playerMoneyComponent = PlayerMoneyComponent.KEY.get(player);

                float killedEntityMaxHealth = killedEntity.getMaxHealth();
                if(killedEntity instanceof Monster monster){
                    EntityType<?> type = killedEntity.getType();
                    // 2) Identifier ("minecraft:creeper" 등) 추출
                    Identifier id = Registries.ENTITY_TYPE.getId(type);
                    // 또는 id.getPath()만 비교할 수도 있습니다.
                    double multiplyCoeffi= 0.5 + player.getRandom().nextDouble();
                    playerMoneyComponent.deposit((int) (killedEntityMaxHealth * multiplyCoeffi * 50));
                    switch (id.toString()) {
                        case "minecraft:creeper"
                                -> playerLevelComponent.addExp(22+ (int) killedEntityMaxHealth/4) ;
                        case "minecraft:pillager", "minecraft:skeleton"
                                -> playerLevelComponent.addExp(25+ (int) killedEntityMaxHealth/4);
                        case "minecraft:blaze", "minecraft:drowned", "minecraft:stray", "minecraft:phantom"
                                -> playerLevelComponent.addExp(35+ (int) killedEntityMaxHealth/4);
                        case "minecraft:piglin_brute"
                                -> playerLevelComponent.addExp(30+ (int) killedEntityMaxHealth/4);
                        case "minecraft:ghast"
                                -> playerLevelComponent.addExp(50+ (int) killedEntityMaxHealth/4);
                        case "minecraft:wither_skeleton"
                                -> playerLevelComponent.addExp(70+ (int) killedEntityMaxHealth/4);
                        case "minecraft:enderman", "minecraft:vindicator", "minecraft:guardian"
                                -> playerLevelComponent.addExp(100+ (int) killedEntityMaxHealth/4);
                        case "minecraft:vex"
                                -> playerLevelComponent.addExp(200+ (int) killedEntityMaxHealth/4);
                        case "minecraft:ravager"
                                -> playerLevelComponent.addExp(500+ (int) killedEntityMaxHealth/4);
                        case "minecraft:endermite"
                                -> playerLevelComponent.addExp(1777);
                        case "minecraft:wither", "minecraft:elder_guardian"
                                -> playerLevelComponent.addExp(2000+ (int) killedEntityMaxHealth);
                        case "minecraft:warden"
                                -> playerLevelComponent.addExp(12000);

                        default -> playerLevelComponent.addExp((int) killedEntityMaxHealth/4);
                    }
                }

            }else{

            }


        });
    }
}
