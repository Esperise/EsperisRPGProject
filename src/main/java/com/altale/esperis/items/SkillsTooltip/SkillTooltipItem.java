package com.altale.esperis.items.SkillsTooltip;

import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Map;

public class SkillTooltipItem extends Item {
    private final String skillType;
    private final String info;
    private final String addtionalInfo;
    private final String skillName;
    private final StatType requiredStatType;
    private final int requiredStatAmount;
    private final float baseDamage;
    private final Map<StatType, Float> damageCoefficients;
    private final float baseBarrier;
    private final Map<StatType, Float> barrierCoefficients;
    private final float baseHeal;
    private final Map<StatType, Float> healCoefficients;
    private final int cooltimeTicks;



    public SkillTooltipItem(Settings settings,String skillType, String skillName, StatType requiredStatType, int requiredStatAmount
            , String info, float baseDamage, Map<StatType, Float> damageCoefficients, float baseBarrier, Map<StatType, Float> barrierCoefficients
            , float baseHeal, Map<StatType, Float> healCoefficients, int cooltimeTicks, String additionalInfo) {
        super(settings.maxCount(1));
        this.skillType = skillType;
        this.info = info;
        this.addtionalInfo = additionalInfo;
        this.skillName = skillName;
        this.requiredStatType = requiredStatType;
        this.requiredStatAmount = requiredStatAmount;
        this.baseDamage = baseDamage;
        this.damageCoefficients =damageCoefficients;
        this.baseBarrier = baseBarrier;
        this.barrierCoefficients = barrierCoefficients;
        this.baseHeal = baseHeal;
        this.healCoefficients = healCoefficients;
        this.cooltimeTicks = cooltimeTicks;
    }
    @Override
    public Text getName(ItemStack stack){
        return Text.literal(String.format("[%s] : %s", skillType, skillName));
    }
    @Override
    public ItemStack getDefaultStack(){
        ItemStack stack = new ItemStack(this);
        SkillTooltipManager.makeSkillTooltipNbt(stack);
        //(스킬 이름) , 요구 스탯 타입, 요구 스탯 타입의 양, 패시브/액티브인 스킬 종류
        SkillTooltipManager.setEssentials(stack, skillName, requiredStatType.getDisplayName(), requiredStatAmount, skillType);
        //데미지 관련 nbt 설정
        if(!(baseDamage == 0 && damageCoefficients.isEmpty())){
            SkillTooltipManager.setDamageTooltip(stack, baseDamage, damageCoefficients);
        }
        //보호막 관련 nbt 설정
        if(!(baseBarrier ==0 && barrierCoefficients.isEmpty())){
            SkillTooltipManager.setBarrierTooltip(stack, baseBarrier, barrierCoefficients);
        }
        //힐 관련 nbt 설정
        if(!(baseHeal ==0 && healCoefficients.isEmpty())){
            SkillTooltipManager.setHealTooltip(stack, baseHeal, healCoefficients);
        }
        //쿨타임 nbt 설정
        if(cooltimeTicks != 0){
            SkillTooltipManager.setCooltimeTooltip(stack, cooltimeTicks);
        }
        SkillTooltipManager.setInfo(stack, info);
        SkillTooltipManager.setAdditionalInfo(stack, addtionalInfo);
        return stack;
    }



}
