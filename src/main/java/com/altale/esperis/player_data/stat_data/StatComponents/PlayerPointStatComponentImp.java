package com.altale.esperis.player_data.stat_data.StatComponents;

import com.altale.esperis.player_data.stat_data.StatPointType;
import com.altale.esperis.player_data.stat_data.StatType;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stat;

import javax.swing.text.SimpleAttributeSet;
import java.util.EnumMap;
import java.util.Map;

public class PlayerPointStatComponentImp implements PlayerPointStatComponent,AutoSyncedComponent {
    private final PlayerEntity player;
    private final Map<StatType,Double> statMap = new EnumMap<>(StatType.class);
    private final Map<StatPointType, Integer> StatPointMap = new EnumMap<>(StatPointType.class);
    public PlayerPointStatComponentImp(PlayerEntity player) {
        this.player = player;
        for (StatType statType: StatType.values()){
            statMap.put(statType,0.0);
        }
        for(StatPointType statPointType: StatPointType.values()){
            if(statPointType == StatPointType.UnusedSP){
                StatPointMap.put(statPointType,5);
            }
            else{
                StatPointMap.put(statPointType,0);
            }
        }
    }

    @Override
    public void setPointStat(StatType statType, double amount) {
        statMap.put(statType, amount);
        PlayerPointStatComponent.KEY.sync(this.player);
    }

    @Override
    public double getPointStat(StatType statType) {
        return statMap.getOrDefault(statType, 0.0);
    }

    @Override
    public Map<StatType, Double> getAllPointStat(){
        Map<StatType,Double> map = new EnumMap<>(StatType.class);
        for(StatType statType: StatType.getNormalStatType()){
            map.put(statType,statMap.getOrDefault(statType, 0.0));
        }
        return map;
    }


    //statPoint(스탯 포인트 관련 method)
    @Override
    public void setSP(StatPointType spType, int amount) {
        StatPointMap.put(spType, amount);
        PlayerPointStatComponent.KEY.sync(this.player);
    }
    @Override
    public void useSP(StatType statType, int amount){
        subtractSP(StatPointType.UnusedSP, amount);//미사용 sp amount만큼 감소
        addSP(StatPointType.UsedSP , amount);//사용한 sp 값 증가 시킴
        addStat(statType, amount);//addStat안에 setStat이 있음-> 동기화 됨, amount만큼 스탯 증가 시킴
    }

    @Override
    public int getSP(StatPointType statPointType){
        return StatPointMap.getOrDefault(statPointType, 0);
    }
    @Override
    public void addSP(StatPointType spType, int amount){
        int beforeSP = getSP(spType);
        setSP(spType, beforeSP + amount);//levelup 안에서 addSP(StatPointType.UnusedSP, 5) 넣어서 미사용 sp 5추가
    }
    @Override
    public void subtractSP(StatPointType spType, int amount){
        int beforeSP = getSP(spType);
        setSP(spType, beforeSP - amount);
    }

    public void giveLevelUpSP(){
        addSP(StatPointType.UnusedSP, 5);
    }




    @Override
    public void addStat(StatType statType, double statValue) {
        double beforePointStat= getPointStat(statType);
        setPointStat(statType, beforePointStat + statValue);
    }

    @Override
    public void subtractStat(StatType statType, double statValue) {
        double beforePointStat= getPointStat(statType);
        setPointStat(statType, beforePointStat-statValue);
    }


    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        if(nbtCompound.contains("PointStats")) {
            NbtCompound statTag = nbtCompound.getCompound("PointStats");
            for(StatType statType: StatType.values()){
                if(statTag.contains(statType.name())){
                    statMap.put(statType,statTag.getDouble(statType.name()));
                }
            }
            for(StatPointType statPointType: StatPointType.values()){
                if(statTag.contains(statPointType.name())){
                    StatPointMap.put(statPointType,statTag.getInt(statPointType.name()));
                }
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        NbtCompound statTag = new NbtCompound();
        for(Map.Entry<StatType, Double> entry: statMap.entrySet()){
            statTag.putDouble(entry.getKey().name(), entry.getValue());
        }
        for(Map.Entry<StatPointType, Integer> entry: StatPointMap.entrySet()){
            statTag.putInt(entry.getKey().name(), entry.getValue());
        }
        nbtCompound.put("PointStats", statTag);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
