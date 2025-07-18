package com.altale.esperis.player_data.stat_data;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerEquipmentStatComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerPointStatComponent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.List;

public class StatManager {
    public static void statUpdate(ServerPlayerEntity player){
        //들고있는 장비 변경, 손에 든 거 변경, statpoint투자 등 마지막에 호출하면 됨
        PlayerPointStatComponent pointStatComponent = PlayerPointStatComponent.KEY.get(player);
        PlayerLevelComponent playerLevelComponent= PlayerLevelComponent.KEY.get(player);
        PlayerEquipmentStatComponent equipmentStatComponent = PlayerEquipmentStatComponent.KEY.get(player);
        PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        //

        //방어구등으로 얻는 스탯, 공격력등 가져오기
        double pointStr= pointStatComponent.getPointStat(StatType.STR);
        double pointDex= pointStatComponent.getPointStat(StatType.DEX);
        double pointLuk= pointStatComponent.getPointStat(StatType.LUK);
        double pointDur= pointStatComponent.getPointStat(StatType.DUR);
        int level= playerLevelComponent.getLevel();
        double eqStr= equipmentStatComponent.getEquipmentStat(StatType.STR);
        double eqDex= equipmentStatComponent.getEquipmentStat(StatType.DEX);
        double eqLuk= equipmentStatComponent.getEquipmentStat(StatType.LUK);
        double eqDur= equipmentStatComponent.getEquipmentStat(StatType.DUR);
        double eqAtk= equipmentStatComponent.getEquipmentStat(StatType.ATK);
        double eqDef= equipmentStatComponent.getEquipmentStat(StatType.DEF);
        double eqMaxHealth= equipmentStatComponent.getEquipmentStat(StatType.MAX_HEALTH);
        double eqSpd= equipmentStatComponent.getEquipmentStat(StatType.SPD);
        double eqCrit= equipmentStatComponent.getEquipmentStat(StatType.CRIT);
        double eqCritDamage= equipmentStatComponent.getEquipmentStat(StatType.CRIT_DAMAGE);
        double totalStr= pointStr + eqStr;
        double totalDex= pointDex + eqDex;
        double totalLuk= pointLuk + eqLuk;
        double totalDur= pointDur + eqDur;
        double atk= (
                0.5*(totalStr) + 0.1*(totalDex) + 0.25*(totalLuk)
                + ( level)+ eqAtk
                //무기로 얻는 스탯 넣기
                );
        double def= (
                level+totalDur+eqDef
                //+무기 방어력 추가
                );
        double maxHp= (//20+레벨당5+str당 1+ dur당5+ 장비체력
                20+(6*level)+totalStr+(5*(totalDur))+eqMaxHealth
                //+무기 체력 추가
                );
        double spd= (
                1+(totalDex * 0.015)+eqSpd
                );//1.015의 이동속도 계수를 가짐
        double crit=(
                0.05+ totalLuk * 0.01 + eqCrit
                );//기본 크확 5% 나중에 100곱하기, 레벨당 1퍼 증가
        double critDamage =(
                1.75+(totalLuk * 0.005) +eqCritDamage
                );// 기본 크뎀 배율 175% luk당 0.5%
        double acc= Math.round(
                (totalDex/(totalDex+totalLuk + 1000))*100
                )/100.0;//명중률 계수 회피율 계수와 정량적인 뺄셈 진행-> randint로 공격 데미지 여부 결정, 소수점 2자리 반올림
        double avd= Math.round(
                (totalLuk/(totalLuk+500) )*100
                )/100.0;//소수점 2자리 반올림rh
        //최종 스펙 저장하는 component만들어서 저장하기!!!

            finalStatComponent.setFinalStat(StatType.STR, totalStr);
            finalStatComponent.setFinalStat(StatType.DEX, totalDex);
            finalStatComponent.setFinalStat(StatType.LUK, totalLuk);
            finalStatComponent.setFinalStat(StatType.DUR, totalDur);
            finalStatComponent.setFinalStat(StatType.ATK, atk);
            finalStatComponent.setFinalStat(StatType.DEF, def);
            finalStatComponent.setFinalStat(StatType.MAX_HEALTH, maxHp);
            //TODO 아래 디버깅용 지우기
            String test=String.format("%f", finalStatComponent.getFinalStat(StatType.MAX_HEALTH));
            player.sendMessage(Text.literal(test));
            finalStatComponent.setFinalStat(StatType.SPD, spd);
            finalStatComponent.setFinalStat(StatType.CRIT, crit);
            finalStatComponent.setFinalStat(StatType.CRIT_DAMAGE, critDamage);
            finalStatComponent.setFinalStat(StatType.ACC, acc);
            finalStatComponent.setFinalStat(StatType.AVD, avd);
            //실제 적용 부분
            ApplyMaxHealth.applyMaxHealthByFinalStat(player);

        }
    public static void register(){
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            Iterable<ServerWorld> worlds = server.getWorlds();
            for(ServerWorld world : worlds){
                List<ServerPlayerEntity> players= world.getPlayers();
                for(ServerPlayerEntity player : players){
                    if(world.getTime() % 2400 == 0){
                        //2분마다 모든 플레이어 스탯 계산해서 저장/적용
                        statUpdate(player);

                        player.sendMessage(Text.literal("동기화 완료"));
                    }
                }

            }
        });
    }
}

