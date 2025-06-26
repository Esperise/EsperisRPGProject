package com.altale.esperis.skills.coolTime;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static java.lang.Math.max;

public class CoolTimeManager {
    //                 <unique id, <skill id,coolTime(Tick)>>
    private static final Map<UUID, Map<String, Integer>> coolTimeMap = new HashMap<>();

    //쿨타임 설정
    public static void setCoolTime(ServerPlayerEntity player, String skillId, int coolTimeTick) {
        coolTimeMap.computeIfAbsent(player.getUuid(), u -> new HashMap<>())
                .put(skillId, coolTimeTick);
    }
    //player의 스킬(skillId)쿨타임 여부: 쿨이면 true , 아니면 false
    public static boolean isOnCoolTime(ServerPlayerEntity player, String skillId) {
        return coolTimeMap.getOrDefault(player.getUuid(), Collections.emptyMap()).getOrDefault(skillId, 0) > 0;
    }
    //player의 스킬(skillId) 의 현재 쿨타임(tick) 반환
    public static int getRemainCoolTime(ServerPlayerEntity player, String skillId) {
        return coolTimeMap.getOrDefault(player.getUuid(), Collections.emptyMap()).getOrDefault(skillId, 0);
    }
    //player의 스킬(skillId) 의 쿨타임을 띄움
    public static void showRemainCoolTime(ServerPlayerEntity player, String skillId) {
        int remainCoolTime= coolTimeMap.getOrDefault(player.getUuid(), Collections.emptyMap()).getOrDefault(skillId, 0);
        String text = String.format("쿨타임:%s- %.1f 초", skillId, remainCoolTime/20.0);
        player.sendMessage(net.minecraft.text.Text.literal(text), false);//true: 액션바 false:채팅
    }
    // n% 만큼 player의 모든 스킬 쿨타임을 감소
    public static void allCoolTimePercentReduction(ServerPlayerEntity player, double coolTimeReductionPercent) {
        Map<String, Integer> playerCoolTimeMap = coolTimeMap.getOrDefault(player.getUuid(), Collections.emptyMap());
        playerCoolTimeMap.replaceAll((skillId, coolTimeTick)-> (int) ((1-(coolTimeReductionPercent/100))*coolTimeTick));
        String text = String.format("모든 스킬 쿨타임 %.1f 감소", coolTimeReductionPercent);
        player.sendMessage(net.minecraft.text.Text.literal(text), true);
    }
    // n tick만큼 player의 모든 스킬 쿨타임을 감소
    public static void allCoolTimeReduction(ServerPlayerEntity player, int coolTimeReductionTick ) {
        Map<String, Integer> playerCoolTimeMap = coolTimeMap.getOrDefault(player.getUuid(), Collections.emptyMap());
        playerCoolTimeMap.replaceAll((skillId, coolTimeTick)-> coolTimeTick-coolTimeReductionTick);
        String text = String.format("모든 스킬 쿨타임 %.1f 감소",coolTimeReductionTick/20.0);
        player.sendMessage(net.minecraft.text.Text.literal(text), true);
    }
    //player의 스킬(skillId)을 n% 만큼 감소
    public static void specificCoolTimePercentReduction(ServerPlayerEntity player,String skillId ,double coolTimeReductionPercent) {
        Map<String, Integer> playerCoolTimeMap = coolTimeMap.getOrDefault(player.getUuid(), Collections.emptyMap());
        Integer targetSkillCoolTime = playerCoolTimeMap.getOrDefault(skillId, 0);

        if(!(targetSkillCoolTime==0)) {
            playerCoolTimeMap.replace(skillId, (int) ((1-(coolTimeReductionPercent/100))*targetSkillCoolTime));
            String text = String.format("%s: %.1f-(%.1f)",skillId , targetSkillCoolTime/20.0, (coolTimeReductionPercent/100)*targetSkillCoolTime/20);
            player.sendMessage(net.minecraft.text.Text.literal(text), false);
        }
    }
    //player의 스킬(skillId)을 n tick 만큼 감소
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
    // CoolTimeTickManager에서 사용되면 호출시 모든 player의 모든 skill 쿨타임을 1tick(0.05초) 감소시킴, 매 틱마다 호출시켜서 쿨타임 시스템 구현
    public static void tick(){
        for(Map<String, Integer> playerCoolTime : coolTimeMap.values()) {
            playerCoolTime.replaceAll((skillId, coolTimeTick)-> max(coolTimeTick-1,0));
        }
    }
}
