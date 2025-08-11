package com.altale.esperis.player_data.stat_data.StatComponents;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;


import java.util.EnumMap;
import java.util.Map;

public class BaseAbilityComponentImp implements BaseAbilityComponent, AutoSyncedComponent {
    private final PlayerEntity player;
    private final Map<StatType, Double> BaseAbilityMap = new EnumMap<StatType, Double>(StatType.class);
    private StatType Str;

    public BaseAbilityComponentImp(PlayerEntity player) {
        this.player = player;
        for (StatType statType : StatType.values()) {
            if(statType == StatType.FinalDamagePercent){
                BaseAbilityMap.put(statType, 1.0);
            } else{
                BaseAbilityMap.put(statType, 0.0);
            }
        }
    }
    @Override
    public void setBaseAbility(StatType statType, Double value) {
        BaseAbilityMap.put(statType, value);
        BaseAbilityComponent.KEY.sync(player);

    }
    @Override
    public void saveBaseAbility(){
        PlayerEquipmentStatComponent eqStat= PlayerEquipmentStatComponent.KEY.get(player);
        PlayerPointStatComponent pointStat= PlayerPointStatComponent.KEY.get(player);
        PlayerLevelComponent levelComponent= PlayerLevelComponent.KEY.get(player);
        int level = levelComponent.getLevel();
        double totalStr= eqStat.getEquipmentStat(StatType.STR)+ pointStat.getPointStat(StatType.STR);
        double totalDex= eqStat.getEquipmentStat(StatType.DEX)+ pointStat.getPointStat(StatType.DEX);
        double totalLuk= eqStat.getEquipmentStat(StatType.LUK)+ pointStat.getPointStat(StatType.LUK);
        double totalDur= eqStat.getEquipmentStat(StatType.DUR)+ pointStat.getPointStat(StatType.DUR);
        double eqAtk= eqStat.getEquipmentStat(StatType.ATK);
        double eqDef= eqStat.getEquipmentStat(StatType.DEF);
        double eqMaxHealth= eqStat.getEquipmentStat(StatType.MAX_HEALTH);
        double eqSpd= eqStat.getEquipmentStat(StatType.SPD);
        double eqCrit= eqStat.getEquipmentStat(StatType.CRIT);
        double eqCritDamage= eqStat.getEquipmentStat(StatType.CRIT_DAMAGE);
        double eqAs= eqStat.getEquipmentStat(StatType.ATTACK_SPEED);
        double eqFinalDamage= eqStat.getEquipmentStat(StatType.FinalDamagePercent);
        double eqDefPen = eqStat.getEquipmentStat(StatType.DefPenetrate);
        double totalFinalDamage= eqFinalDamage;
        double totalDefPen= eqDefPen;
        double atk= (
                0.1*(totalStr) + 0.025*(totalDex) + 0.05*(totalLuk)
                        + ( 0.2 * level)+ eqAtk
                //무기로 얻는 스탯 넣기
        );
        double def= (
                level + (0.6 * totalDur)+eqDef
                //+무기 방어력 추가
        );
        double maxHp= (//20+레벨당5+str당 1+ dur당5+ 장비체력
                20+(4*level)+(0.5*totalStr)+(1.5*(totalDur))+eqMaxHealth
                //+무기 체력 추가
        );
        double spd= (
                1+(totalDex * 0.0015)+(totalLuk*0.00077)+eqSpd
        );//1.025의 이동속도 계수를 가짐
        double as=(
                1+(totalDex * 0.003) + (totalLuk*0.00177)+eqAs
        );
        double crit=Math.min(1.0,
                0.05+ totalLuk * 0.002 + eqCrit
        );//기본 크확 5% 나중에 100곱하기, luk당 0.25%
        double critDamage =(
                1.75+(totalLuk * 0.002) +eqCritDamage
        );// 기본 크뎀 배율 175% luk당 0.25%
        double acc= Math.round(
                ((totalDex+(totalLuk/4.0)) / ( totalDex+(totalLuk/4.0) + 1000)) *1000
        )/1000.0;//명중률 계수 회피율 계수와 정량적인 뺄셈 진행-> randint로 공격 데미지 여부 결정, 소수점 2자리 반올림
        double avd= Math.round(
                (totalLuk+(totalDex/4)) /((totalLuk+500)+(totalDex/4)) *1000
        )/1000.0;//소수점 3자리 반올림
        setBaseAbility(StatType.STR, totalStr);
        setBaseAbility(StatType.DEX, totalDex);
        setBaseAbility(StatType.LUK, totalLuk);
        setBaseAbility(StatType.DUR, totalDur);
        setBaseAbility(StatType.ATK, atk);
        setBaseAbility(StatType.DEF, def);
        setBaseAbility(StatType.MAX_HEALTH, maxHp);
        setBaseAbility(StatType.SPD, spd);
        setBaseAbility(StatType.CRIT, crit);
        setBaseAbility(StatType.CRIT_DAMAGE, critDamage);
        setBaseAbility(StatType.ACC, acc);
        setBaseAbility(StatType.AVD, avd);
        setBaseAbility(StatType.ATTACK_SPEED, as);
        setBaseAbility(StatType.FinalDamagePercent, totalFinalDamage);
        setBaseAbility(StatType.DefPenetrate, totalDefPen);
    }
    @Override
    public Map<StatType, Double> getAbilityMap() {
        return BaseAbilityMap;

    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        if(nbtCompound.contains("BaseAbility")){
            NbtCompound baseAbilityTag= nbtCompound.getCompound("BaseAbility");
            for(StatType statType: StatType.values()){
                BaseAbilityMap.put(statType, baseAbilityTag.getDouble(statType.toString()));
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        NbtCompound baseAbilityTag= new NbtCompound();
        for(Map.Entry<StatType, Double> entry: BaseAbilityMap.entrySet()){
            baseAbilityTag.putDouble(entry.getKey().toString(), entry.getValue());
        }
        nbtCompound.put("BaseAbility", baseAbilityTag);
    }
}
