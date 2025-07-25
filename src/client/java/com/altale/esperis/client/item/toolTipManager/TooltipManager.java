package com.altale.esperis.client.item.toolTipManager;

import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class TooltipManager {
    public TextColor getStatColor(StatType statType) {
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
    public Text makeStatText(Boolean hasDamage ,StatType statType, TooltipTextInfoType textInfoType) {
        Text tooltipText= Text.literal("");
        if(hasDamage) {
            //데미지 있는거(무기 스킬 등)
        }else{//데미지 없는 방어구 같은 일반 장비
            //text.append

        }


        return tooltipText;
    }
    Text tooltip= Text.literal("");

}
