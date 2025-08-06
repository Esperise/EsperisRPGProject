package com.altale.esperis.player_data.stat_data.StatComponents;

import com.altale.esperis.player_data.equipmentStat.EquipmentInfoManager;
import com.altale.esperis.player_data.stat_data.StatType;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.util.EnumMap;
import java.util.Map;

public class PlayerEquipmentStatComponentImp implements PlayerEquipmentStatComponent , AutoSyncedComponent {
    static final Map<StatType, Double> EquipmentStatsMap= new EnumMap<>(StatType.class);
    private final PlayerEntity player;
    public PlayerEquipmentStatComponentImp(PlayerEntity player) {
        this.player = player;
        for(StatType statType : StatType.values()) {
            EquipmentStatsMap.put(statType, 0.0);
        }
    }
    @Override
    public void setEquipmentStat(StatType statType, double amount) {
        EquipmentStatsMap.put(statType, amount);
        PlayerEquipmentStatComponent.KEY.sync(this.player);
    }

    @Override
    public double getEquipmentStat(StatType statType) {
        return EquipmentStatsMap.getOrDefault(statType,0.0);
    }

    @Override
    public void addEquipmentStat(StatType statType, double amount) {
        double equipmentStat = getEquipmentStat(statType);
        equipmentStat += amount;
        setEquipmentStat(statType, equipmentStat);
    }
    @Override
    public void changeEquipment(PlayerEntity player, EquipmentSlot slot , ItemStack previous , ItemStack current){
        if(!previous.isEmpty()){
            Map<StatType, Double> previousStatsMap = EquipmentInfoManager.sumEquipmentStats(previous);
            for (Map.Entry<StatType, Double> entry : previousStatsMap.entrySet()) {
                addEquipmentStat(entry.getKey(), -entry.getValue()); // 빼기
            }
        }
        if(!current.isEmpty()){
            // 2. 새로운 장비 스탯 추가
            Map<StatType, Double> currentStatsMap = EquipmentInfoManager.sumEquipmentStats(current);
            for (Map.Entry<StatType, Double> entry : currentStatsMap.entrySet()) {
                addEquipmentStat(entry.getKey(), entry.getValue()); // 더하기
            }
        }

    }
    @Override
    public  void initializeEquipmentStat(PlayerEntity player){
        EquipmentStatsMap.clear();
        for(ItemStack stack : player.getItemsEquipped()) {
            Map<StatType, Double> map = EquipmentInfoManager.sumEquipmentStats(stack);
            for(Map.Entry<StatType, Double> entry : map.entrySet()) {
                addEquipmentStat(entry.getKey(), entry.getValue());
                System.out.println(stack);
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        if(nbtCompound.contains("EquipmentStats")) {
            NbtCompound equipmentStatsTag = nbtCompound.getCompound("EquipmentStats");
            for(StatType statType: StatType.values()){
                if(equipmentStatsTag.contains(statType.name())){
                    EquipmentStatsMap.put(statType,equipmentStatsTag.getDouble(statType.name()));
                }
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        NbtCompound equipmentStatsTag = new NbtCompound();
        for(Map.Entry<StatType, Double> entry: EquipmentStatsMap.entrySet()){
            equipmentStatsTag.putDouble(entry.getKey().name(), entry.getValue());
            equipmentStatsTag.putDouble(entry.getKey().name(), entry.getValue());
        }
        nbtCompound.put("EquipmentStats", equipmentStatsTag);
    }
}
