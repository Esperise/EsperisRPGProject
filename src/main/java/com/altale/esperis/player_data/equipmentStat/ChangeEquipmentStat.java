package com.altale.esperis.player_data.equipmentStat;

import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ChangeEquipmentStat {

    public final static int REROLL_COST= 25000;


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
        if(!canChangeStat(stack)){
            int flag = EquipmentInfoManager.getEquipmentCanChangeAdditionalNum(stack);
            if(flag == 0){//정상적으로 nbt있는데 횟수는 다썼을 때-> 돈으로 돌림
                PlayerMoneyComponent playerMoneyComponent = PlayerMoneyComponent.KEY.get(player);
                if(playerMoneyComponent.getBalance()<REROLL_COST){
                    return false;
                }
                int rarity = EquipmentInfoManager.getRarityLevel(stack);
                playerMoneyComponent.withdraw(REROLL_COST*(rarity+1));
                changeStatFunction(stack, player, false);
                return true;
            } else if (flag == -100 || flag == -99) { //nbt에 eqinfo관련 없을때
                Map<StatType, Double> map = new HashMap<>();
                map.putIfAbsent(StatType.DEF, 1.0);
                map.putIfAbsent(StatType.MAX_HEALTH, 2.0);
                EquipmentInfoManager.setEquipmentInfo(stack, 1 , 0,5,5,map);
                changeStatFunction(stack, player, true);
                return true;
            } else{
                return false;
            }
        } else{//일반
            changeStatFunction(stack, player, true);
            return true;
        }
    }

    private static void changeStatFunction(ItemStack stack, PlayerEntity player , Boolean consumeAvailable){
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound equipmentInfo = root.getCompound("equipment_info");
        NbtCompound equipmentBasicStat = equipmentInfo.getCompound("equipment_basic_stat_cmp");
        NbtCompound equipAdditionalStat = equipmentInfo.getCompound("equipment_additional_stat_cmp");
//        NbtCompound equipAdditionalTotalStatsCompound=equipAdditionalStat.getCompound("equipment_additional_total_stats_cmp");//여기에 저장
        equipAdditionalStat.put("equipment_additional_total_stats_cmp", new NbtCompound());
        NbtCompound equipAdditionalTotalStatsCompound=equipAdditionalStat.getCompound("equipment_additional_total_stats_cmp");//여기에 저장
        int eqRarity = equipmentBasicStat.getInt("equipment_basic_rarity");//(기본: 0) 노말 0 레어 1 유니크 2 레전더리 3 에픽 4 태초 5
        int eqLevel = equipmentBasicStat.getInt("equipment_basic_level");

        if(consumeAvailable){
            int left = equipAdditionalStat.getInt("equipment_change_additional_stat_number") -1 ;
            equipAdditionalStat.putInt("equipment_change_additional_stat_number", left);//변경가능 횟수 감소
        }

        Map<StatType, Double> newStatsMap= computeRandomStatMap(eqLevel, eqRarity, player);
        //map을 통해 장비 스탯을 nbt에 저장
        for (Map.Entry<StatType, Double> e : newStatsMap.entrySet()) {
            double value = e.getValue();
            if (value <= 0) continue;
            double multiplier = getStatMultiplier(e.getKey());
            equipAdditionalTotalStatsCompound.putDouble(e.getKey().toString(), value * multiplier);
        }
    }

    private static int calculateBase(int level, int rarity, PlayerEntity player){
        PlayerFinalStatComponent playerStatComponent = PlayerFinalStatComponent.KEY.get(player);
        int luk =(int) playerStatComponent.getFinalStat(StatType.LUK);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int baseStat= 4 + rarity + (int) Math.round(level/5.0);
        int randInt=random.nextInt(100000+(luk*10));
        if(randInt > 100500) {//0%(luk=0)
            baseStat+= 10;
        } else if (randInt > 99900) {//0.1%
            baseStat += 8;
        }else if (randInt > 99700) {//0.3%
            baseStat += 7;
        }else if (randInt > 99000) {//1
            baseStat += 5;
        }else if (randInt > 95000) {//5
            baseStat += 4;
        }else if (randInt > 85000) {//15
            baseStat += 3;
        }else if (randInt > 70000) {//30
            baseStat += 2;
        }else if (randInt > 50000) {//20
            baseStat += 1;
        } else if (randInt > 40000) {

        }else if(randInt > 30000 ){
            baseStat -= 1;
        }else if(randInt > 10000){
            baseStat -= 2;
        } else{
            baseStat -= 3;
        }
        System.out.println(baseStat);
        return baseStat;
    }
    private static Map<StatType, Double> computeRandomStatMap(int level, int rarity, PlayerEntity player){

        Random random = new Random();
        int baseStat = calculateBase(level, rarity, player);
        Map<StatType, Double> eqStatsMap = new EnumMap<>(StatType.class);

        while(baseStat>=6 && random.nextInt(20)<=baseStat){
            baseStat -=3;
            StatType[] specialStats= StatType.getSpecialStatType();//spd, crit, critDmg, finalDmg, acc,avd,defPen
            StatType pick = specialStats[random.nextInt(specialStats.length)];
            eqStatsMap.merge(pick, Math.max(0.5, random.nextDouble(1.5)), Double::sum);//computIf, Absent 없이 한줄로 가능함
        }
        int firstIndex= random.nextInt(StatType.getNoneSpecialStats().length-1);
        System.out.println("firstIndex: "+firstIndex);
        int lastIndex= random.nextInt(StatType.getNoneSpecialStats().length - firstIndex) + firstIndex;
        while(lastIndex == firstIndex){
            lastIndex= random.nextInt(StatType.getNoneSpecialStats().length - firstIndex+1) + firstIndex;
        }
        System.out.println("lastIndex: "+lastIndex);

        StatType[] normal = StatType.getNoneSpecialStats();//atk, def, maxHealth, str, dex, luk, dur
        for (int i = 0; i < baseStat; i++) {
            StatType pick = normal[Math.min(6,random.nextInt(lastIndex-firstIndex+1)+firstIndex)];
            System.out.println("pick: "+pick);
            eqStatsMap.merge(pick, 1.0, Double::sum);
        }

        return eqStatsMap;

    }
    private static double getStatMultiplier(StatType stat) {
        return switch(stat) {
            case STR, DEX, LUK, DUR ,ATK      -> 1.0;
            case MAX_HEALTH                 -> 2.0;
            case DEF                        -> 1.5;

            case ATTACK_SPEED -> 0.08;
            case SPD, CRIT  -> 0.03;
            case ACC, AVD                   -> 0.012;
            case CRIT_DAMAGE                -> 0.05;
            case DefPenetrate               -> 0.04;
            case FinalDamagePercent         -> 0.035;
        };
    }
}
