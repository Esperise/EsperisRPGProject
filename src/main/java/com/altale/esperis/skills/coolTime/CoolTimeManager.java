package com.altale.esperis.skills.coolTime;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.Math.max;

public class CoolTimeManager {
    private static final Map<UUID, Map<String, Integer>> coolTimeMap = new HashMap<>();

    public static void setCoolTime(ServerPlayerEntity player, String skillId, int coolTimeTick) {
        coolTimeMap.computeIfAbsent(player.getUuid(), u -> new HashMap<>())
                .put(skillId, coolTimeTick);
    }
    public static boolean isOnCoolTime(ServerPlayerEntity player, String skillId) {
        return coolTimeMap.getOrDefault(player.getUuid(), Collections.emptyMap()).getOrDefault(skillId, 0) > 0;
    }
    public static int getRemainCoolTime(ServerPlayerEntity player, String skillId) {
        return coolTimeMap.getOrDefault(player.getUuid(), Collections.emptyMap()).getOrDefault(skillId, 0);
    }
    public static void showRemainCoolTime(ServerPlayerEntity player, String skillId) {
        int remainCoolTime= coolTimeMap.getOrDefault(player.getUuid(), Collections.emptyMap()).getOrDefault(skillId, 0);
        String text = String.format("쿨타임:%s- %.1f 초", skillId, remainCoolTime/20.0);
        player.sendMessage(net.minecraft.text.Text.literal(text), false);
    }
    public static void coolTimeReduction(ServerPlayerEntity player, double coolTimeReductionPercent) {
        Map<String, Integer> playerCoolTimeMap = coolTimeMap.getOrDefault(player.getUuid(), Collections.emptyMap());
        playerCoolTimeMap.replaceAll((skillId, coolTimeTick)-> (int) ((1-(coolTimeReductionPercent/100))*coolTimeTick));
        String text = String.format("모든 스킬 쿨타임 %.1f 감소", coolTimeReductionPercent);
        player.sendMessage(net.minecraft.text.Text.literal(text), true);
    }
    public static void specificCoolTimePercentReduction(ServerPlayerEntity player,String skillId ,double coolTimeReductionPercent) {
        Map<String, Integer> playerCoolTimeMap = coolTimeMap.getOrDefault(player.getUuid(), Collections.emptyMap());
        Integer targetSkillCoolTime = playerCoolTimeMap.getOrDefault(skillId, 0);
        System.out.println(playerCoolTimeMap);
        System.out.println(targetSkillCoolTime);
        if(!(targetSkillCoolTime==0)) {
            playerCoolTimeMap.replace(skillId, (int) ((1-(coolTimeReductionPercent/100))*targetSkillCoolTime));
            String text = String.format("%s: %.1f-(%.1f)",skillId , targetSkillCoolTime/20.0, (coolTimeReductionPercent/100)*targetSkillCoolTime/20);
            player.sendMessage(net.minecraft.text.Text.literal(text), false);
        }
        else{
            System.out.println(playerCoolTimeMap);
            System.out.println(targetSkillCoolTime);
        }
    }
    public static void specificCoolTimeReduction(ServerPlayerEntity player,String skillId ,int coolTimeReductionTick) {
        double coolTimeReductionSeconds = (double) coolTimeReductionTick /20;
        Map<String, Integer> playerCoolTimeMap = coolTimeMap.getOrDefault(player.getUuid(), Collections.emptyMap());
        Integer targetSkillCoolTime = playerCoolTimeMap.getOrDefault(skillId, 0);
        if(!(targetSkillCoolTime==0)) {
            playerCoolTimeMap.replace(skillId, targetSkillCoolTime-coolTimeReductionTick);
            String text = String.format("%s: %.1f-(%.1f)", skillId, targetSkillCoolTime/20.0 ,coolTimeReductionSeconds );
            player.sendMessage(net.minecraft.text.Text.literal(text), false);
        }
    }
    public static void tick(){
        for(Map<String, Integer> playerCoolTime : coolTimeMap.values()) {
            playerCoolTime.replaceAll((skillId, coolTimeTick)-> max(coolTimeTick-1,0));
        }
    }
}
