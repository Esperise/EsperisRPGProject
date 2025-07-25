package com.altale.esperis.player_data.equipmentStat;

import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ChangeEquipmentStat {

    public static boolean canChangeStat(ItemStack stack) {
        // 1) 스택이 없거나 NBT가 아예 없으면 false
        if (stack == null || !stack.hasNbt()) {
            return false;
        }

        // 2) equipment_info 자체가 세팅되어 있지 않으면 false
        if (!EquipmentInfoManager.hasEquipmentInfo(stack)) {
            return false;
        }

        // 3) 남은 변경 가능 횟수가 1 이상이면 true, 아니면 false
        return EquipmentInfoManager
                .getEquipmentCanChangeAdditionalNum(stack) > 0;
    }









    public static boolean changeStat(ItemStack stack, PlayerEntity player){ // 요청 패킷 받는 class에서 사용, 성공시 true, 실패시 false
        Map<StatType, Double> eqStatMap = new EnumMap<>(StatType.class);
        if(!canChangeStat(stack)){
            if(EquipmentInfoManager.getEquipmentCanChangeAdditionalNum(stack) == 0){
                PlayerMoneyComponent playerMoneyComponent = PlayerMoneyComponent.KEY.get(player);
                if(playerMoneyComponent.getBalance()<10000){
                    return false;
                }
                playerMoneyComponent.withdraw(25000);
                NbtCompound root = stack.getNbt();
                NbtCompound equipmentInfo = root.getCompound("equipment_info");
                NbtCompound equipmentBasicStat = equipmentInfo.getCompound("equipment_basic_stat_cmp");
                NbtCompound equipAdditionalStat = equipmentInfo.getCompound("equipment_additional_stat_cmp");
                NbtCompound equipAdditionalTotalStatsCompound=equipAdditionalStat.getCompound("equipment_additional_total_stats_cmp");//여기에 저장
                int eqRarity = equipmentBasicStat.getInt("equipment_basic_rarity");//(기본: 0) 노말 0 레어 1 유니크 2 레전더리 3 에픽 4 태초 5
                int eqLevel = equipmentBasicStat.getInt("equipment_basic_level");
                Map<StatType, Double> newStatsMap = computeRandomStatMap(eqLevel, eqRarity, player);
                for (Map.Entry<StatType, Double> e : newStatsMap.entrySet()) {
                    double value = e.getValue();
                    if (value <= 0) continue;
                    double multiplier = getStatMultiplier(e.getKey());
                    equipAdditionalTotalStatsCompound.putDouble(e.getKey().toString(), value * multiplier);
                }
                return true;
            }


            return false;
        } else{
            NbtCompound root = stack.getNbt();
            NbtCompound equipmentInfo = root.getCompound("equipment_info");
            NbtCompound equipmentBasicStat = equipmentInfo.getCompound("equipment_basic_stat_cmp");
            NbtCompound equipAdditionalStat = equipmentInfo.getCompound("equipment_additional_stat_cmp");
            NbtCompound equipAdditionalTotalStatsCompound=equipAdditionalStat.getCompound("equipment_additional_total_stats_cmp");//여기에 저장
            int eqRarity = equipmentBasicStat.getInt("equipment_basic_rarity");//(기본: 0) 노말 0 레어 1 유니크 2 레전더리 3 에픽 4 태초 5
            int eqLevel = equipmentBasicStat.getInt("equipment_basic_level");


            int availableNumber = equipAdditionalStat.getInt("equipment_change_additional_stat_number");
            equipAdditionalStat.putInt("equipment_change_additional_stat_number", --availableNumber);//변경가능 횟수 감소
            Map<StatType, Double> newStatsMap= computeRandomStatMap(eqLevel, eqRarity, player);
            //map을 통해 장비 스탯을 nbt에 저장
            for (Map.Entry<StatType, Double> e : newStatsMap.entrySet()) {
                double value = e.getValue();
                if (value <= 0) continue;
                double multiplier = getStatMultiplier(e.getKey());
                equipAdditionalTotalStatsCompound.putDouble(e.getKey().toString(), value * multiplier);
            }
            return true;
        }
    }
    private static int calculateBase(int level, int rarity, PlayerEntity player){
        PlayerFinalStatComponent playerStatComponent = PlayerFinalStatComponent.KEY.get(player);
        int luk =(int) playerStatComponent.getFinalStat(StatType.LUK);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int baseStat= 4 + rarity + (int) Math.round(level/5.0);
        int randInt=random.nextInt(100+luk);
        if(randInt > 450) {
            baseStat+= 8;
        } else if (randInt > 350) {
            baseStat += 7;
        }else if (randInt > 300 ) {
            baseStat += 6;
        }else if (randInt > 250) {
            baseStat += 5;
        }else if (randInt > 200) {
            baseStat += 4;
        }else if (randInt > 115) {
            baseStat += 3;
        }else if (randInt > 95) {
            baseStat += 2;
        }else if (randInt > 80) {
            baseStat += 1;
        } else if (randInt > 65) {

        }else if(randInt > 35){
            baseStat -= 1;
        }else if(randInt > 10){
            baseStat -= 2;
        } else{
            baseStat -= 3;
        }
        return baseStat;
    }
    private static Map<StatType, Double> computeRandomStatMap(int level, int rarity, PlayerEntity player){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int baseStat = calculateBase(level, rarity, player);
        Map<StatType, Double> eqStatsMap = new EnumMap<>(StatType.class);
        while(baseStat>=6 && random.nextInt(20)<baseStat){
            baseStat -=3;
            StatType[] specialStats= StatType.getSpecialStatType();
            StatType pick = specialStats[random.nextInt(specialStats.length)];
            eqStatsMap.merge(pick, Math.max(0.5, random.nextDouble(1.5)), Double::sum);//computIf, Absent 없이 한줄로 가능함
        }
        StatType[] normal = StatType.getArmorStatType();
        for (int i = 0; i < baseStat; i++) {
            StatType pick = normal[random.nextInt(normal.length)];
            eqStatsMap.merge(pick, 1.0, Double::sum);
        }

        return eqStatsMap;

    }
    private static double getStatMultiplier(StatType stat) {
        return switch(stat) {
            case STR, DEX, LUK, DUR        -> 1.0;
            case MAX_HEALTH                 -> 3.0;
            case DEF                        -> 1.5;
            case ATK                        -> 0.5;
            case SPD, CRIT, FinalDamagePercent -> 0.02;
            case ACC, AVD                   -> 0.01;
            case CRIT_DAMAGE                -> 0.04;
            case DefPenetrate               -> 0.03;
        };
    }
}
