package com.altale.esperis.player_data.equipmentStat;

import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.*;

public class EquipmentInfoManager {
    /*
    equipment_info

        equipment_additional_stat_cmp

            변경 가능 횟수 → "equipment_change_additional_stat_number" (Int)

            전체 스탯 → "equipment_additional_total_stats_cmp" (Compound)
                stats (Double)

        equipment_basic_stat_cmp

            전체 스탯 → "equipment_basic_total_stats_cmp" (Compound)
                stats (Double)

            레벨 → "equipment_basic_level" (Int)

            희귀도 → "equipment_basic_rarity"(Int)

        equipment_scroll_stat_cmp

            적용된 스크롤 스탯 → "equipment_scroll_applied_stats_cmp" (Compound)
                stats (Double)

            가용 스크롤 횟수 → "equipment_scroll_available"(Int)

            */

    public static void makeEquipmentInfo( ItemStack stack,NbtCompound root
            , int level, int rarity,int additionalShuffleNum, int canUseScrollNum, Map<StatType, Double> statsMap){
        root.put("equipment_info", new NbtCompound());

        NbtCompound equipmentInfo = root.getCompound("equipment_info");

        makeEqAdditionalStatComponent(equipmentInfo,additionalShuffleNum);
        makeBasicStatComponent(equipmentInfo,level,rarity,statsMap);
        makeEquipmentScrollStat(equipmentInfo,canUseScrollNum);

    }
    public static void makeEqAdditionalStatComponent(NbtCompound equipmentInfo, int additionalShuffleNum){
        equipmentInfo.put("equipment_additional_stat_cmp", new NbtCompound());
        NbtCompound equipmentAdditionalStatCmp = equipmentInfo.getCompound("equipment_additional_stat_cmp");
        equipmentAdditionalStatCmp.putInt("equipment_change_additional_stat_number", additionalShuffleNum);
        equipmentAdditionalStatCmp.put("equipment_additional_total_stats_cmp", new NbtCompound());
    }
    public static void makeBasicStatComponent(NbtCompound equipmentInfo, int level, int rarity, Map<StatType, Double> statsMap){
        equipmentInfo.put("equipment_basic_stat_cmp", new NbtCompound());
        NbtCompound equipmentBasicStatComponent = equipmentInfo.getCompound("equipment_basic_stat_cmp");
        equipmentBasicStatComponent.put("equipment_basic_total_stats_cmp", new NbtCompound());
        NbtCompound basicStats = equipmentBasicStatComponent.getCompound("equipment_basic_total_stats_cmp");

        // basicStats를 Map으로 반복하면서 초기화 시키는 거
        Iterator<Map.Entry<StatType, Double>> entryIterator = statsMap.entrySet().iterator();
        while(entryIterator.hasNext()){
            Map.Entry<StatType, Double> entry = entryIterator.next();
            if(entry.getValue()==0.0){
                continue;
            }else{
                basicStats.putDouble(entry.getKey().toString(),entry.getValue());
            }
        }
        equipmentBasicStatComponent.putInt("equipment_basic_level", level);
        equipmentBasicStatComponent.putInt("equipment_basic_rarity", rarity);
    }
    public static void makeEquipmentScrollStat(NbtCompound equipmentInfo, int canUseScrollNum){
        equipmentInfo.put("equipment_scroll_stat_cmp", new NbtCompound());
        NbtCompound equipmentScrollStat = equipmentInfo.getCompound("equipment_scroll_stat_cmp");
        equipmentScrollStat.put("equipment_scroll_applied_stats_cmp",new NbtCompound());
        equipmentInfo.putInt("equipment_scroll_available", canUseScrollNum);
    }

    public static void setEquipmentInfo(ItemStack stack, int level, int rarity, int additionalShuffleNum, int canUseScrollNum
                                        ,Map<StatType, Double> basicStatsMap){
        if(stack.hasNbt()){
            if(hasEquipmentInfo(stack)){
                return;
            }else{
                //root는 있지만 equipment_info는 없음
                NbtCompound root = stack.getOrCreateNbt();
                makeEquipmentInfo(stack,root, level, rarity, additionalShuffleNum, canUseScrollNum, basicStatsMap);
            }
        } else {
            //root부터 만들음
            NbtCompound root = stack.getOrCreateNbt();
            makeEquipmentInfo(stack,root, level, rarity, additionalShuffleNum, canUseScrollNum, basicStatsMap);
        }
    }

    public static boolean hasEquipmentInfo(ItemStack stack){
        if(stack.hasNbt()){
            NbtCompound nbt = stack.getOrCreateNbt();
            if(Objects.requireNonNull(nbt).contains("equipment_info")){
                return true;
            } else{
                return false;
            }
        }return false;
    }

    public static Map<StatType, Double> getAllEquipmentAdditionalStatsMap(ItemStack stack){
        Map<StatType, Double> map = new HashMap<>();
        if(hasEquipmentInfo(stack)){
            NbtCompound statsCmp = stack.getOrCreateNbt()
                    .getCompound("equipment_info")
                    .getCompound("equipment_additional_stat_cmp")
                    .getCompound("equipment_additional_total_stats_cmp");//전부 double임
            if(statsCmp.isEmpty()){
                return new HashMap<>();
            }else{
                for(String Key :statsCmp.getKeys()){
                    map.put(StatType.valueOf(Key), statsCmp.getDouble(Key));
                }
                return map;
            }

        }
        return  new HashMap<>();// map.empty 로 걸러야함
    }
    public static int getEquipmentCanChangeAdditionalNum(ItemStack stack) {
        // 1) NBT 자체가 없으면 -1
        if (!stack.hasNbt()) {
            return -100;
        }

        NbtCompound root = stack.getOrCreateNbt();

        // 2) equipment_info Compound 체크
        if (!root.contains("equipment_info", NbtElement.COMPOUND_TYPE)) {
            return -99;
        }
        NbtCompound info = root.getCompound("equipment_info");

        // 3) equipment_additional_stat_cmp Compound 체크
        if (!info.contains("equipment_additional_stat_cmp", NbtElement.COMPOUND_TYPE)) {
            return -98;
        }
        NbtCompound additional = info.getCompound("equipment_additional_stat_cmp");

        // 4) equipment_change_additional_stat_number Int 체크
        if (!additional.contains("equipment_change_additional_stat_number", NbtElement.INT_TYPE)) {
            return -97;
        }

        // 5) 모두 있으면 실제 값 반환
        return additional.getInt("equipment_change_additional_stat_number");
    }




    public static Map<StatType, Double> getAllEquipmentBasicStatsMap(ItemStack stack){
        Map<StatType, Double> map = new HashMap<>();
        if(hasEquipmentInfo(stack)){
            NbtCompound statsCmp= Objects.requireNonNull(stack.getOrCreateNbt()).getCompound("equipment_info").getCompound("equipment_basic_stat_cmp").getCompound("equipment_basic_total_stats_cmp");
            if(statsCmp.isEmpty()){
                return new HashMap<>();
            }else{
                for(String Key :statsCmp.getKeys()){
                    map.put(StatType.valueOf(Key), statsCmp.getDouble(Key));
                }
                return map;
            }

        } else{
            return new HashMap<>();
        }
    }
    public static int getEquipmentLevel(ItemStack stack){
        if(hasEquipmentInfo(stack)){
            return stack.getOrCreateNbt()
                    .getCompound("equipment_info")
                    .getCompound("equipment_basic_stat_cmp")
                    .getInt("equipment_basic_level");
        }else{
            return 1;
        }
    }public static int getRarityLevel(ItemStack stack){
        if(hasEquipmentInfo(stack)){
            return stack.getOrCreateNbt()
                    .getCompound("equipment_info")
                    .getCompound("equipment_basic_stat_cmp")
                    .getInt("equipment_basic_rarity");
        }else{
            return 1;
        }
    }

    public static Map<StatType, Double> getAllEquipmentScrollStatsMap(ItemStack stack){
        Map<StatType, Double> map = new HashMap<>();
        if(hasEquipmentInfo(stack)){
            NbtCompound statsCmp= Objects.requireNonNull(stack.getOrCreateNbt()).getCompound("equipment_info").getCompound("equipment_scroll_stat_cmp").getCompound("equipment_scroll_applied_stats_cmp");
            if(statsCmp.isEmpty()) {
                return new HashMap<>();
            }else{
                for(String Key :statsCmp.getKeys()){
                    map.put(StatType.valueOf(Key), statsCmp.getDouble(Key));
                }
                return map;
            }
        }else{
            return new HashMap<>();
        }

    }

    public int getEquipmentCanUseScrollNum(ItemStack stack){
        return stack.getOrCreateNbt()
                .getCompound("equipment_info")
                .getInt("equipment_scroll_available");

    }
    public static Map<StatType,Double> sumEquipmentStats(ItemStack stack){
        if(hasEquipmentInfo(stack)){
            Map<StatType, Double> baseStatsMap = EquipmentInfoManager.getAllEquipmentBasicStatsMap(stack);
            Map<StatType, Double> additionalStatsMap = EquipmentInfoManager.getAllEquipmentAdditionalStatsMap(stack);
            Map<StatType, Double> scrollStatsMap = EquipmentInfoManager.getAllEquipmentScrollStatsMap(stack);
            for (Map.Entry<StatType, Double> entry : additionalStatsMap.entrySet()) {
                baseStatsMap.merge(entry.getKey(), entry.getValue(), Double::sum);
            }
            for (Map.Entry<StatType, Double> entry : scrollStatsMap.entrySet()) {
                baseStatsMap.merge(entry.getKey(), entry.getValue(), Double::sum);
            }
            return baseStatsMap;
        }
        return  new HashMap<>();
    }
}
