package com.altale.esperis.client.item.toolTipManager;

import com.altale.esperis.items.SkillsTooltip.SkillTooltipManager;
import com.altale.esperis.items.SkillsTooltip.SkillTooltipItem;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class SkillTooltip {
    public static void register(){
        ItemTooltipCallback.EVENT.register((stack, context, tooltip) -> {
            if(canMakeTooltip(stack)){
                tooltip.addAll(makeTooltip(stack));
            }
        });
    }
    public static String getSpecialCharByStatType(StatType statType){
        String specialChar;
        switch(statType){
            case ATK -> specialChar = "⚔";
            case DEF -> specialChar = "🛡";
            case STR -> specialChar = "힘";
            case DEX -> specialChar = "민첩";
            case LUK -> specialChar = "행운";
            case DUR -> specialChar = "내구";
            case MAX_HEALTH -> specialChar = "♥";
            case SPD -> specialChar = "이동 속도";
            case ATTACK_SPEED -> specialChar = "공격 속도";
            case AVD -> specialChar = "회피율";
            case ACC -> specialChar = "명중률";
            case CRIT -> specialChar = "크리티컬 확률";
            case CRIT_DAMAGE -> specialChar = "크리티컬 데미지";
            case FinalDamagePercent -> specialChar = "최종 데미지";
            case DefPenetrate -> specialChar = "방어력 관통";
            default -> specialChar = "";
        }
        return specialChar;
    }
    public static List<Text> makeTooltip(ItemStack stack){
        List<Text> tooltips = new ArrayList<>(List.of());
        MutableText tempLine= Text.literal("");
        if(!SkillTooltipManager.hasSkillTooltip(stack)) return tooltips;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return tooltips;
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        Map<StatType, Double> statsMap = playerFinalStatComponent.getAllFinalStat();

        if(SkillTooltipManager.getCooltime(stack) > 0){
            tempLine.append(String.format("쿨타임: %.2f초", SkillTooltipManager.getCooltime(stack)));
            tooltips.add(tempLine);
            tempLine= Text.literal("");
        }
        //info 예시: 검을 휘둘러 _damageFlag_ 의 피해를 입히고, 체력을 _healFlag_ 만큼 회복한다.
        //info안의 _*Flag_를 구분자로 하여 분리,
        String temp= SkillTooltipManager.getInfo(stack);
        List<MutableText> info = new ArrayList<>();
        Pattern pattern = Pattern.compile("(_damageFlag_)|(_barrierFlag_)|(_healFlag_)|(_lineBreak_)|([^_]+)");
        Matcher matcher = pattern.matcher(temp);

        while(matcher.find()){
            String split = matcher.group();
            if(split.equals("_damageFlag_")){
                if(SkillTooltipManager.hasDamage(stack)){
                    float baseDamage = SkillTooltipManager.getBaseDamage(stack);
                    Map<StatType, Double> damageCoefficientsMap = SkillTooltipManager.getDamageCoefficients(stack);

                    String damageCoefficientString = valueWithCoefficientText(stack, statsMap, baseDamage, damageCoefficientsMap);
                    info.add(Text.literal(damageCoefficientString).formatted(Formatting.LIGHT_PURPLE));
                }
            }else if(split.equals("_barrierFlag_")){
                if(SkillTooltipManager.hasBarrier(stack)){
                    float baseBarrier = SkillTooltipManager.getBaseBarrier(stack);
                    Map<StatType, Double> barrierCoefficientsMap = SkillTooltipManager.getBarrierCoefficients(stack);
                    String barrierCoefficientString = valueWithCoefficientText(stack, statsMap,baseBarrier, barrierCoefficientsMap);
                    info.add(Text.literal(barrierCoefficientString).formatted(Formatting.GRAY));
                }

            }else if(split.equals("_healFlag_")){
                if(SkillTooltipManager.hasHeal(stack)){
                    float baseHeal = SkillTooltipManager.getBaseHeal(stack);
                    Map<StatType, Double> healCoefficientsMap = SkillTooltipManager.getHealCoefficients(stack);
                    String healCoefficientString = valueWithCoefficientText(stack, statsMap, baseHeal, healCoefficientsMap);
                    info.add(Text.literal(healCoefficientString).formatted(Formatting.GREEN));
                }
            }else if(split.equals("_lineBreak_")){
                    MutableText infoLine = Text.literal("");
        for(MutableText infoText : info){
            infoLine.append(infoText);
        }
        info.clear();
        tempLine = insertIntoTooltips(tempLine, tooltips, infoLine);
                }
            else{
                info.add(Text.literal(split));
            }
        }
        MutableText infoLine = Text.literal("");
        for(MutableText infoText : info){
            infoLine.append(infoText);
        }
        tempLine = insertIntoTooltips(tempLine, tooltips, infoLine);
        if(!SkillTooltipManager.getAdditionalInfo(stack).equals("")){
            tempLine = insertIntoTooltips(tempLine, tooltips, Text.literal(SkillTooltipManager.getAdditionalInfo(stack)).formatted(Formatting.ITALIC, Formatting.DARK_GRAY));
        }

        return tooltips;
    }
    public static MutableText insertIntoTooltips(MutableText tempLine, List<Text> tooltips, MutableText content) {
        tempLine.append(content);
        tooltips.add(tempLine);
        return Text.literal("");
    }
    public static float calculateValue(Map<StatType, Double> statsMap,float baseValue, Map<StatType, Double> coefficientsMap){
        float totalValue =baseValue;
        for(StatType statType: coefficientsMap.keySet()){
            totalValue += (float) (statsMap.get(statType) * coefficientsMap.get(statType));
        }
        return totalValue;
    }
    public static String valueWithCoefficientText(ItemStack stack, Map<StatType, Double> playerStatsMap,
            float baseValue, Map<StatType, Double> coefficientsMap){
        float totalDamage= calculateValue(playerStatsMap, baseValue,coefficientsMap);
        String damageCoefficientString = String.format("=( %.2f ",baseValue);
        for(StatType statType : Objects.requireNonNull(coefficientsMap).keySet()) {
//            double statValue = playerStatsMap.get(statType);
//            totalDamage += (float) (statValue * damageCoefficientsMap.get(statType));
            damageCoefficientString += String.format("+ %.2f%% %s", coefficientsMap.get(statType)*100, getSpecialCharByStatType(statType));
        }
        damageCoefficientString+=" )";
        damageCoefficientString=String.format("%.2f ", totalDamage)+ damageCoefficientString;
        return damageCoefficientString;
    }
    public static boolean canMakeTooltip(ItemStack stack){
        Item item = stack.getItem();
        return (item instanceof SkillTooltipItem && SkillTooltipManager.hasSkillTooltip(stack));
    }
}
