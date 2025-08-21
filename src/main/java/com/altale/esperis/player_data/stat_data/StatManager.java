package com.altale.esperis.player_data.stat_data;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.skill_data.passive.PassiveSkillManager;
import com.altale.esperis.player_data.stat_data.StatComponents.BaseAbilityComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerEquipmentStatComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerPointStatComponent;
import com.altale.esperis.skills.buff.AbilityBuff;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StatManager {
    public static void statUpdate(ServerPlayerEntity player){
        //들고있는 장비 변경, 손에 든 거 변경, statpoint투자 등 마지막에 호출하면 됨
        PlayerPointStatComponent pointStatComponent = PlayerPointStatComponent.KEY.get(player);
        PlayerLevelComponent playerLevelComponent= PlayerLevelComponent.KEY.get(player);
        PlayerEquipmentStatComponent equipmentStatComponent = PlayerEquipmentStatComponent.KEY.get(player);
        PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        BaseAbilityComponent baseAbilityComponent = BaseAbilityComponent.KEY.get(player);
        baseAbilityComponent.saveBaseAbility();
        //
 //TODO 버프map으로 변동해야할 baseAbility 변경후 map에 있는 모든 요소들을 finalStat에 저장
        Map<StatType, Double> baseAbilityMap= baseAbilityComponent.getAbilityMap();
        Map<StatType, List<Double>> buffMap = AbilityBuff.getBuffs(player);
        for(Map.Entry<StatType, Double> entry : baseAbilityMap.entrySet()){
            StatType statType = entry.getKey();
            double value = entry.getValue();
            if(buffMap.isEmpty()){
                finalStatComponent.setFinalStat(statType, value);
            }else if(buffMap.containsKey(statType)){
                double buffConstantValue= buffMap.get(statType).get(0);
                double buffPercentValue= buffMap.get(statType).get(1);
                value= (value + buffConstantValue) * (1+ buffPercentValue/100);
                if(Arrays.asList(StatType.getCapsStats()).contains(statType)){
                    value= Math.max(0, value);
                    value= Math.min(1, value);
                }
                finalStatComponent.setFinalStat(statType, value);
            }else{
                finalStatComponent.setFinalStat(statType, value);
            }
        }
        //방어구등으로 얻는 스탯, 공격력등 가져오기
//        double pointStr= pointStatComponent.getPointStat(StatType.STR);
//        double pointDex= pointStatComponent.getPointStat(StatType.DEX);
//        double pointLuk= pointStatComponent.getPointStat(StatType.LUK);
//        double pointDur= pointStatComponent.getPointStat(StatType.DUR);
//        int level= playerLevelComponent.getLevel();
//        double eqStr= equipmentStatComponent.getEquipmentStat(StatType.STR);
//        double eqDex= equipmentStatComponent.getEquipmentStat(StatType.DEX);
//        double eqLuk= equipmentStatComponent.getEquipmentStat(StatType.LUK);
//        double eqDur= equipmentStatComponent.getEquipmentStat(StatType.DUR);
//        double eqAtk= equipmentStatComponent.getEquipmentStat(StatType.ATK);
//        double eqDef= equipmentStatComponent.getEquipmentStat(StatType.DEF);
//        double eqMaxHealth= equipmentStatComponent.getEquipmentStat(StatType.MAX_HEALTH);
//        double eqSpd= equipmentStatComponent.getEquipmentStat(StatType.SPD);
//        double eqCrit= equipmentStatComponent.getEquipmentStat(StatType.CRIT);
//        double eqCritDamage= equipmentStatComponent.getEquipmentStat(StatType.CRIT_DAMAGE);
//        double eqAs= equipmentStatComponent.getEquipmentStat(StatType.ATTACK_SPEED);
//        double eqFinalDamage= equipmentStatComponent.getEquipmentStat(StatType.FinalDamagePercent);
//        double eqDefPen = equipmentStatComponent.getEquipmentStat(StatType.DefPenetrate);
//        double totalStr= pointStr + eqStr;
//        double totalDex= pointDex + eqDex;
//        double totalLuk= pointLuk + eqLuk;
//        double totalDur= pointDur + eqDur;
//        double totalFinalDamage= eqFinalDamage;
//        double totalDefPen= eqDefPen;
//        double atk= (
//                0.1*(totalStr) + 0.025*(totalDex) + 0.05*(totalLuk)
//                + ( 0.2 * level)+ eqAtk
//                //무기로 얻는 스탯 넣기
//                );
//        double def= (
//                level + (0.6 * totalDur)+eqDef
//                //+무기 방어력 추가
//                );
//        double maxHp= (//20+레벨당5+str당 1+ dur당5+ 장비체력
//                20+(4*level)+(0.5*totalStr)+(1.5*(totalDur))+eqMaxHealth
//                //+무기 체력 추가
//                );
//        double spd= (
//                1+(totalDex * 0.0015)+(totalLuk*0.00077)+eqSpd
//                );//1.025의 이동속도 계수를 가짐
//        double as=(
//                1+(totalDex * 0.003) + (totalLuk*0.00177)+eqAs
//                );
//        double crit=Math.min(1.0,
//                0.05+ totalLuk * 0.002 + eqCrit
//                );//기본 크확 5% 나중에 100곱하기, luk당 0.25%
//        double critDamage =(
//                1.75+(totalLuk * 0.002) +eqCritDamage
//                );// 기본 크뎀 배율 175% luk당 0.25%
//        double acc= Math.round(
//                ((totalDex+(totalLuk/4.0)) / ( totalDex+(totalLuk/4.0) + 1000)) *1000
//                )/1000.0;//명중률 계수 회피율 계수와 정량적인 뺄셈 진행-> randint로 공격 데미지 여부 결정, 소수점 2자리 반올림
//        double avd= Math.round(
//                (totalLuk+(totalDex/4)) /((totalLuk+500)+(totalDex/4)) *1000
//                )/1000.0;//소수점 3자리 반올림
//        //최종 스펙 저장하는 component만들어서 저장하기!!!
//
//            finalStatComponent.setFinalStat(StatType.STR, totalStr);
//            finalStatComponent.setFinalStat(StatType.DEX, totalDex);
//            finalStatComponent.setFinalStat(StatType.LUK, totalLuk);
//            finalStatComponent.setFinalStat(StatType.DUR, totalDur);
//            finalStatComponent.setFinalStat(StatType.ATK, atk);
//            finalStatComponent.setFinalStat(StatType.DEF, def);
//            finalStatComponent.setFinalStat(StatType.MAX_HEALTH, maxHp);
//            finalStatComponent.setFinalStat(StatType.SPD, spd);
//            finalStatComponent.setFinalStat(StatType.CRIT, crit);
//            finalStatComponent.setFinalStat(StatType.CRIT_DAMAGE, critDamage);
//            finalStatComponent.setFinalStat(StatType.ACC, acc);
//            finalStatComponent.setFinalStat(StatType.AVD, avd);
//            finalStatComponent.setFinalStat(StatType.ATTACK_SPEED, as);
//            finalStatComponent.setFinalStat(StatType.FinalDamagePercent, totalFinalDamage);
//            finalStatComponent.setFinalStat(StatType.DefPenetrate, totalDefPen);
            //실제 적용 부분
//            ApplyMaxHealth.applyMaxHealthByFinalStat(player);
//            ApplyMovementSpd.applyBaseSpeed(player);
            ApplyStat2Ability.applyPlayerBaseAbility(player);

        }
    public static void register(){
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            Iterable<ServerWorld> worlds = server.getWorlds();
            for(ServerWorld world : worlds){
                List<ServerPlayerEntity> players= world.getPlayers();
                for(ServerPlayerEntity player : players){
                    if(world.getTime() % 12000 == 0){
                        //5분마다 모든 플레이어 스탯 계산해서 저장/적용
                        statUpdate(player);
                        player.sendMessage(Text.literal("동기화 완료"));
                    }
                    if(world.getTime() % 80 ==0){
                        player.heal(player.getMaxHealth()/100); // 4초마다 (1% 최대체력 + 1) -> 1초당 0.25%+0.25 회복-> 8분에 풀피
                        PassiveSkillManager.hpRegenFlag(player);
                    }
                }

            }
        });
    }
}

