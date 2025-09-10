package com.altale.esperis.items.SkillsTooltip;

import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Map;

public class SkillTooltipItems {
    public static final String SKILL_TOOLTIP = "skill_tooltip";
        public static final String SKILL_TOOLTIP_NAME = "skill_tooltip:name";
        public static final String SKILL_TOOLTIP_REQUIRE_STATS = "skill_tooltip:require_stat";
            public static final String SKILL_TOOLTIP_REQUIRE_STATS_TYPE = "skill_tooltip:require_stat:type";
            public static final String SKILL_TOOLTIP_REQUIRE_STATS_AMOUNT = "skill_tooltip:require_stat:amount";
        public static final String SKILL_TOOLTIP_COOLTIME = "skill_tooltip:cooltime";
        public static final String SKILL_TOOLTIP_SKILL_TYPE= "skill_tooltip:skill_type";
        public static final String SKILL_TOOLTIP_FUNCTION = "skill_tooltip:function";
            public static final String FUNCTION_DAMAGE = "skill_tooltip:function:damage";
                public static final String FUNCTION_DAMAGE_BASE = "skill_tooltip:function:damage:base";
                public static final String FUNCTION_DAMAGE_STATS_COEFFICIENT = "skill_tooltip:function:damage:stats_coefficient";
            public static final String FUNCTION_BUFF = "skill_tooltip:function:buff";
            public static final String FUNCTION_BARRIER = "skill_tooltip:function:barrier";
            public static final String FUNCTION_BARRIER_BASE = "skill_tooltip:function:barrier:base";
            public static final String FUNCTION_BARRIER_STATS_COEFFICIENT = "skill_tooltip:function:barrier:stats_coefficient";
    public static void makeSkillTooltipNbt(ItemStack stack){
        if(hasSkillTooltip(stack)){
            return;
        }else{
            if(stack.hasNbt()){
                NbtCompound root = stack.getOrCreateNbt();
                root.put(SKILL_TOOLTIP, new NbtCompound());
            }
        }
    }
    public static void setEssentials(ItemStack stack, String skillName, String requiredStat, int requiredStatAmount, String skillType){
        if(hasSkillTooltip(stack)){
            makeSkillTooltipNbt(stack);
            setEssentials(stack, skillName, requiredStat,requiredStatAmount, skillType);
        }else{
            NbtCompound root = stack.getOrCreateNbt();
            NbtCompound skillTooltip = stack.getOrCreateSubNbt(SKILL_TOOLTIP);
            //이름
            skillTooltip.putString(SKILL_TOOLTIP_NAME, skillName);
            //요구 스탯 -타입, 수치
            NbtCompound required = new NbtCompound();
            required.putString(SKILL_TOOLTIP_REQUIRE_STATS_TYPE,requiredStat);
            required.putInt(SKILL_TOOLTIP_REQUIRE_STATS_AMOUNT, requiredStatAmount);
            skillTooltip.put(SKILL_TOOLTIP_REQUIRE_STATS, required);
            //스킬 종류 (액티브/패시브)
            skillTooltip.putString(SKILL_TOOLTIP_SKILL_TYPE, skillType);
        }
    }
    public static void setCooltimeTooltip(ItemStack stack, int cooltimeTicks){
        if(!hasSkillTooltip(stack)) return;
        float cooltimeSeconds = Math.round(cooltimeTicks*100/20f)/100f;
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        skillTooltip.putFloat(SKILL_TOOLTIP_COOLTIME, cooltimeSeconds);
    }
    public static void setDamageTooltip(ItemStack stack, int baseDamage, Map<StatType, Float> statsCoefficients){
        //baseDamage와 특정 스탯에 대한 계수가 하위 nbt로 저장됨.
        if(!hasSkillTooltip(stack)) return;
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!hasFunctionTooltip(stack)){
            skillTooltip.put(SKILL_TOOLTIP_FUNCTION, new NbtCompound());
        }
        //baseDamage
        NbtCompound functionTooltip = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        functionTooltip.putFloat(FUNCTION_DAMAGE_BASE, baseDamage);
        //stat:coefficient(percent, *100 된 상태) - ATK = 75 형태로 저장 , 공격력 계수 75%라는 의미
        NbtCompound statsCoefficientNbt = new NbtCompound();
        for(StatType statType: statsCoefficients.keySet()){
            statsCoefficientNbt.putFloat(statType.toString(), statsCoefficients.get(statType));
        }
        functionTooltip.put(FUNCTION_DAMAGE_STATS_COEFFICIENT, statsCoefficientNbt);
    }
    public static void setBarrierTooltip(ItemStack stack , int baseAmount , Map<StatType, Float> statsCoefficients){
        if(!hasSkillTooltip(stack)) return;
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!hasFunctionTooltip(stack)){
            skillTooltip.put(SKILL_TOOLTIP_FUNCTION, new NbtCompound());
        }
        NbtCompound functionTooltip = skillTooltip.getCompound(SKILL_TOOLTIP);
        functionTooltip.putFloat(FUNCTION_BARRIER_BASE, baseAmount);
        NbtCompound statsCoefficientNbt = new NbtCompound();
        for(StatType statType: statsCoefficients.keySet()){
            statsCoefficientNbt.putFloat(statType.toString(), statsCoefficients.get(statType));
        }
        functionTooltip.put(FUNCTION_BARRIER_STATS_COEFFICIENT, statsCoefficientNbt);
    }
    public static boolean hasSkillTooltip(ItemStack stack){
        if(stack.hasNbt()){
            NbtCompound nbt = stack.getOrCreateNbt();
            return nbt.contains(SKILL_TOOLTIP);
        }
        return false;
    }
    public static boolean hasFunctionTooltip(ItemStack stack){
        if(stack.hasNbt()){
            NbtCompound root  = stack.getOrCreateNbt();
            if(root.contains(SKILL_TOOLTIP)){
                NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
                return skillTooltip.contains(SKILL_TOOLTIP_FUNCTION);
            }

        }
        return false;
    }

}
