package com.altale.esperis.client.item.toolTipManager;

import com.altale.esperis.items.itemFunction.SpecialBowItem;
import com.altale.esperis.player_data.equipmentStat.EquipmentInfoManager;
import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.item.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.*;

import static com.altale.esperis.player_data.equipmentStat.EquipmentInfoManager.sumEquipmentStats;

public class TooltipManager {
    public static TextColor getStatColor(StatType statType) {
        TextColor statColor;
        switch (statType) {
            case ATK -> statColor = TextColor.fromRgb(0xFF5203);//redOrange
            case DEF -> statColor = TextColor.fromRgb(0x848381);//회색
            case STR -> statColor = TextColor.fromRgb(0xFD2525);//빨
            case DEX -> statColor = TextColor.fromRgb(0x68e3be);//민트
            case LUK -> statColor = TextColor.fromRgb(0xd4e04f);//(약간 주황) 노란색
            case DUR -> statColor = TextColor.fromRgb(0x405dcf);//푸른색
            case MAX_HEALTH -> statColor = TextColor.fromRgb(0x28a113);//초록
            case SPD -> statColor = TextColor.fromRgb(0x96f2fa);//하늘색
            case ATTACK_SPEED -> statColor = TextColor.fromRgb(0xd9b338);//노랑주황
            case AVD -> statColor = TextColor.fromRgb(0x6d9da1);//파란색쪽 매우 어두운색
            case ACC -> statColor = TextColor.fromRgb(0x77a16d);//초록색쪽 매우 어두운 색
            case CRIT ,CRIT_DAMAGE -> statColor = TextColor.fromRgb(0xede440);// 노란색
            case FinalDamagePercent -> statColor = TextColor.fromRgb(0xEEE3FD);//하얀색
            case DefPenetrate -> statColor = TextColor.fromRgb(0xa12828);//어두운 빨간색
            default -> statColor = TextColor.fromRgb(0xFFFFFF);
        }//⬆ fa96f0:유니크
        return statColor;
    }
    public Formatting[] getFormatting(TooltipTextInfoType textInfoType) {
        Formatting[] formatting;
        switch (textInfoType) {
            case Stats, Damage, ShowCoefficient ->formatting= new Formatting[]{Formatting.RESET};
            case CoolTime -> formatting= new Formatting[]{Formatting.AQUA};
            case AdditionalInfo -> formatting = new Formatting[]{Formatting.DARK_GREEN, Formatting.ITALIC};
            default -> formatting= new Formatting[]{Formatting.RESET};
        }
        return formatting;
    }
    //for로 ToolTipStatType 반복한거에 이거 넣기




    public static List<Text> makeStatText(ItemStack stack) {
        Map<StatType, Double> totalStatsMap = sumEquipmentStats(stack);
            List<Text> tooltips = new ArrayList<>(List.of());
            MutableText basicStatLine=Text.literal("");
            for(StatType basicStat: StatType.getBasicStatType()){
                if(totalStatsMap.containsKey(basicStat)){
                    basicStatLine.append(Text.literal(String.format("%s : +%.1f  ",basicStat.getDisplayName(),totalStatsMap.get(basicStat)))
                            .setStyle(Style.EMPTY.withColor(getStatColor(basicStat))));
                    totalStatsMap.remove(basicStat);
                }
            }
        if(! basicStatLine.equals(Text.literal(""))){
            tooltips.add(basicStatLine);
        }

        MutableText normalStatLine = Text.literal("");
            for(StatType normalStat: StatType.getNormalStatType()){
                if(totalStatsMap.containsKey(normalStat)){
                    normalStatLine.append(Text.literal(String.format("%s : +%.0f  ",normalStat.getDisplayName(),totalStatsMap.get(normalStat)))
                            .setStyle(Style.EMPTY.withColor(getStatColor(normalStat))));

                    totalStatsMap.remove(normalStat);
                }
            }
        if(! normalStatLine.equals(Text.literal(""))){
            tooltips.add(normalStatLine);
        }



        MutableText specialStatLine = Text.literal("");
            int count =0;
            for(StatType specialStat: StatType.getSpecialStatType()){
                if(totalStatsMap.containsKey(specialStat)){
                    String name= specialStat.getDisplayName();
                    double value = totalStatsMap.get(specialStat);
                    if(Arrays.stream(StatType.getPercentStats()).anyMatch(stat -> stat.getDisplayName().equals(specialStat.getDisplayName()))){
                        value *= 100;
                        specialStatLine.append(Text.literal(String.format("%s : +%.2f%%  ",name,value))
                                .setStyle(Style.EMPTY.withColor(getStatColor(specialStat))));
                        totalStatsMap.remove(specialStat);
                    }else{
                        value= (value) *100;
                        specialStatLine.append(Text.literal(String.format("%s : +%.2f%%  ",name,value))
                                .setStyle(Style.EMPTY.withColor(getStatColor(specialStat))));
                        totalStatsMap.remove(specialStat);
                    }
                    count++;
                    if(count ==2){
                        tooltips.add(specialStatLine);
                        specialStatLine = Text.literal("");
                        count=0;
                }
            }

        }
        if(! specialStatLine.equals(Text.literal(""))){
            tooltips.add(specialStatLine);
        }

        return tooltips;
    }

    public static boolean canMakeTooltip(ItemStack stack){
        Item item = stack.getItem();
        if(item instanceof ArmorItem) return true;
        if(item instanceof ToolItem) return true;
        if(item instanceof SwordItem) return true;
//        if(item instanceof SpecialBowItem) return true;
        return false;
    }

}
