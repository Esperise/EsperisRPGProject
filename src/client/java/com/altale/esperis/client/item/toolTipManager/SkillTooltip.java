package com.altale.esperis.client.item.toolTipManager;

import com.altale.esperis.items.SkillsTooltip.MakeSkillTooltipNbts;
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
import net.minecraft.text.TextColor;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        if(!MakeSkillTooltipNbts.hasSkillTooltip(stack)) return tooltips;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return tooltips;
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        Map<StatType, Double> statsMap = playerFinalStatComponent.getAllFinalStat();

        if(MakeSkillTooltipNbts.getCooltime(stack) > 0){
            tempLine.append(String.format("쿨타임: %.2f", MakeSkillTooltipNbts.getCooltime(stack)));
            tooltips.add(tempLine);
            tempLine= Text.literal("");
        }
        String info= MakeSkillTooltipNbts.getInfo(stack);
//        String[] splits = info.split("(_damageFlag)|(_barrierFlag)|(_healFlag)");
        if(MakeSkillTooltipNbts.hasDamage(stack)){
            float baseDamage = MakeSkillTooltipNbts.getBaseDamage(stack);
            Map<StatType, Double> damageCoefficientsMap = MakeSkillTooltipNbts.getDamageCoefficients(stack);
            float totalDamage= baseDamage;
            String damageCoefficientString = String.format("=( %.2f ",baseDamage);
            for(StatType statType : damageCoefficientsMap.keySet()) {
                double statValue = statsMap.get(statType);
                totalDamage += (float) (statValue * damageCoefficientsMap.get(statType));
                damageCoefficientString += String.format("+ %.2f%% %s", damageCoefficientsMap.get(statType), getSpecialCharByStatType(statType));
            }
            damageCoefficientString+=" )";
            damageCoefficientString=String.format("%.2f ", totalDamage)+ damageCoefficientString;
            info= info.replace("{damageFlag}", damageCoefficientString);
        }


        tempLine.append(MakeSkillTooltipNbts.getInfo(stack));
        tooltips.add(tempLine);

        return tooltips;
    }
//    public static String convertFlag2String(ItemStack stack){
//        PlayerEntity player = MinecraftClient.getInstance().player;
//        PlayerFinalStatComponent cmp= PlayerFinalStatComponent.KEY.get(player);
//        Map<StatType, Double> map= cmp.getAllFinalStat();
//
//
//    }
    public static boolean canMakeTooltip(ItemStack stack){
        Item item = stack.getItem();
        return (item instanceof SkillTooltipItem && MakeSkillTooltipNbts.hasSkillTooltip(stack));
    }
}
