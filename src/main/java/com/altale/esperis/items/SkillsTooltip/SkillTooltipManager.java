package com.altale.esperis.items.SkillsTooltip;

import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SkillTooltipManager {
    public static final String SKILL_TOOLTIP = "skill_tooltip";
        public static final String SKILL_TOOLTIP_INFO="skill_tooltip:info";
        public static final String SKILL_TOOLTIP_ADDITIONAL_INFO="skill_tooltip:additional_info";
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
            public static final String FUNCTION_HEAL = "skill_tooltip:function:heal";
                public static final String FUNCTION_HEAL_BASE = "skill_tooltip:function:heal:base";
                public static final String FUNCTION_HEAL_STATS_COEFFICIENT = "skill_tooltip:function:heal:stats_coefficient";
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
    public static void setInfo(ItemStack stack, String info){
        if(!hasSkillTooltip(stack)) return;
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip= root.getCompound(SKILL_TOOLTIP);
        skillTooltip.putString(SKILL_TOOLTIP_INFO, info);
    }
    public static String getInfo(ItemStack stack){
        if(!hasSkillTooltip(stack)) return "";
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip= root.getCompound(SKILL_TOOLTIP);
        return skillTooltip.getString(SKILL_TOOLTIP_INFO);
    }
    public static void setAdditionalInfo(ItemStack stack, String additionalInfo){
        if(!hasSkillTooltip(stack)) return;
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip= root.getCompound(SKILL_TOOLTIP);
        skillTooltip.putString(SKILL_TOOLTIP_ADDITIONAL_INFO, additionalInfo);
    }
    public static String getAdditionalInfo(ItemStack stack){
        if(!hasSkillTooltip(stack)) return "";
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip= root.getCompound(SKILL_TOOLTIP);
        return skillTooltip.getString(SKILL_TOOLTIP_ADDITIONAL_INFO);
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
    public static float getCooltime(ItemStack stack){
        if(!hasSkillTooltip(stack)) return -1f;
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!skillTooltip.contains(SKILL_TOOLTIP_COOLTIME)) return -1f;
        return skillTooltip.getFloat(SKILL_TOOLTIP_COOLTIME);
    }
    public static void setDamageTooltip(ItemStack stack, float baseDamage, Map<StatType, Float> statsCoefficients){
        //baseDamage와 특정 스탯에 대한 계수가 하위 nbt로 저장됨.
        if(!hasSkillTooltip(stack)) return;
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!hasFunctionTooltip(stack)){
            skillTooltip.put(SKILL_TOOLTIP_FUNCTION, new NbtCompound());
        }
        //baseDamage
        NbtCompound functionTooltip = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        NbtCompound damage = new NbtCompound();
        damage.putFloat(FUNCTION_DAMAGE_BASE, baseDamage);
        //stat:coefficient(계수 0.75) - ATK = 0.75 형태로 저장 , 공격력 계수 75%라는 의미
        NbtCompound statsCoefficientNbt = new NbtCompound();
        for(StatType statType: statsCoefficients.keySet()){
            statsCoefficientNbt.putFloat(statType.toString(), statsCoefficients.get(statType));
        }
        damage.put(FUNCTION_DAMAGE_STATS_COEFFICIENT, statsCoefficientNbt);
        functionTooltip.put(FUNCTION_DAMAGE, damage);
    }
    public static boolean hasDamage(ItemStack stack){
        if(!hasSkillTooltip(stack)) return false;
        NbtCompound root = stack.getOrCreateNbt();
        if(!root.contains(SKILL_TOOLTIP)) return false;
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!skillTooltip.contains(SKILL_TOOLTIP_FUNCTION)) return false;
        NbtCompound function = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        return function.contains(FUNCTION_DAMAGE);
    }
    public static float getBaseDamage(ItemStack stack){
        if(!hasSkillTooltip(stack)) return -1f;
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!skillTooltip.contains(SKILL_TOOLTIP_FUNCTION)) return -1f;
        NbtCompound function = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        if(!function.contains(FUNCTION_DAMAGE)) return -1f;
        NbtCompound damage = function.getCompound(FUNCTION_DAMAGE);
        return damage.getFloat(FUNCTION_DAMAGE_BASE);
    }
    public static Map<StatType, Double> getDamageCoefficients(ItemStack stack){
        if(!hasSkillTooltip(stack)) return Collections.emptyMap();
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!skillTooltip.contains(SKILL_TOOLTIP_FUNCTION)) return Collections.emptyMap();
        NbtCompound function = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        if(!function.contains(FUNCTION_DAMAGE)) return Collections.emptyMap();
        NbtCompound functionDamage = function.getCompound(FUNCTION_DAMAGE);
        NbtCompound dmgCoefficients = functionDamage.getCompound(FUNCTION_DAMAGE_STATS_COEFFICIENT);
        Map<StatType, Double> coefficients = new HashMap<StatType, Double>();
        for(String stringStatType: dmgCoefficients.getKeys()){
            StatType statType= StatType.valueOf(stringStatType);
            coefficients.put(statType, dmgCoefficients.getDouble(stringStatType));
        }
        return coefficients;
    }

    public static void setBarrierTooltip(ItemStack stack , float baseAmount , Map<StatType, Float> statsCoefficients){
        if(!hasSkillTooltip(stack)) return;
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!hasFunctionTooltip(stack)){
            skillTooltip.put(SKILL_TOOLTIP_FUNCTION, new NbtCompound());
        }
        NbtCompound functionTooltip = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        NbtCompound barrier = new NbtCompound();
        barrier.putFloat(FUNCTION_BARRIER_BASE, baseAmount);
        NbtCompound statsCoefficientNbt = new NbtCompound();
        for(StatType statType: statsCoefficients.keySet()){
            statsCoefficientNbt.putFloat(statType.toString(), statsCoefficients.get(statType));
        }
        barrier.put(FUNCTION_BARRIER_STATS_COEFFICIENT, statsCoefficientNbt);
        functionTooltip.put(FUNCTION_BARRIER, barrier);
    }
    public static boolean hasBarrier(ItemStack stack){
        if(!hasSkillTooltip(stack)) return false;
        NbtCompound root = stack.getOrCreateNbt();
        if(!root.contains(SKILL_TOOLTIP)) return false;
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!skillTooltip.contains(SKILL_TOOLTIP_FUNCTION)) return false;
        NbtCompound functionTooltip = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        return functionTooltip.contains(FUNCTION_BARRIER);
    }
    public static float getBaseBarrier(ItemStack stack){
        if(!hasSkillTooltip(stack)) return -1f;
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!skillTooltip.contains(SKILL_TOOLTIP_FUNCTION)) return -1f;
        NbtCompound function = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        if(!function.contains(FUNCTION_BARRIER)) return -1f;
        NbtCompound barrier = function.getCompound(FUNCTION_BARRIER);
        return barrier.getFloat(FUNCTION_BARRIER_BASE);

    }
    public static Map<StatType, Double> getBarrierCoefficients(ItemStack stack){
        if(!hasSkillTooltip(stack)) return Collections.emptyMap();
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!skillTooltip.contains(SKILL_TOOLTIP_FUNCTION)) return Collections.emptyMap();
        NbtCompound function = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        if(!function.contains(FUNCTION_BARRIER)) return Collections.emptyMap();
        NbtCompound functionBarrier = function.getCompound(FUNCTION_BARRIER);
        NbtCompound barrierCoefficients = functionBarrier.getCompound(FUNCTION_BARRIER_STATS_COEFFICIENT);
        Map<StatType, Double> coefficients = new HashMap<StatType, Double>();
        for(String stringStatType: barrierCoefficients.getKeys()){
            StatType statType= StatType.valueOf(stringStatType);
            coefficients.put(statType, barrierCoefficients.getDouble(stringStatType));
        }
        return coefficients;
    }
    public static void setHealTooltip(ItemStack stack, float baseAmount, Map<StatType, Float> statsCoefficients){
        if(!hasSkillTooltip(stack)) return;
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!hasFunctionTooltip(stack)){
            skillTooltip.put(SKILL_TOOLTIP_FUNCTION, new NbtCompound());
        }
        NbtCompound functionTooltip = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        NbtCompound heal = new NbtCompound();
        heal.putFloat(FUNCTION_HEAL_BASE, baseAmount);
        NbtCompound statsCoefficientNbt = new NbtCompound();
        for(StatType statType: statsCoefficients.keySet()){
            statsCoefficientNbt.putFloat(statType.toString(), statsCoefficients.get(statType));
        }
        heal.put(FUNCTION_HEAL_STATS_COEFFICIENT, statsCoefficientNbt);
        functionTooltip.put(FUNCTION_HEAL, heal);
    }
    public static boolean hasHeal(ItemStack stack){
        if(!hasSkillTooltip(stack)) return false;
        NbtCompound root = stack.getOrCreateNbt();
        if(!root.contains(SKILL_TOOLTIP)) return false;
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!skillTooltip.contains(SKILL_TOOLTIP_FUNCTION)) return false;
        NbtCompound functionTooltip = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        return functionTooltip.contains(FUNCTION_HEAL);


    }
    public static float getBaseHeal(ItemStack stack){
        if(!hasSkillTooltip(stack)) return -1f;
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!skillTooltip.contains(SKILL_TOOLTIP_FUNCTION)) return -1f;
        NbtCompound function = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        if(!function.contains(FUNCTION_HEAL)) return -1f;
        NbtCompound damage = function.getCompound(FUNCTION_HEAL);
        return damage.getFloat(FUNCTION_HEAL_BASE);
    }
    public static Map<StatType, Double> getHealCoefficients(ItemStack stack){
        if(!hasSkillTooltip(stack)) return Collections.emptyMap();
        NbtCompound root = stack.getOrCreateNbt();
        NbtCompound skillTooltip = root.getCompound(SKILL_TOOLTIP);
        if(!skillTooltip.contains(SKILL_TOOLTIP_FUNCTION)) return Collections.emptyMap();
        NbtCompound function = skillTooltip.getCompound(SKILL_TOOLTIP_FUNCTION);
        if(!function.contains(FUNCTION_HEAL)) return Collections.emptyMap();
        NbtCompound functionHeal = function.getCompound(FUNCTION_HEAL);
        NbtCompound healCoefficients = functionHeal.getCompound(FUNCTION_HEAL_STATS_COEFFICIENT);
        Map<StatType, Double> coefficients = new HashMap<StatType, Double>();
        for(String stringStatType: healCoefficients.getKeys()){
            StatType statType= StatType.valueOf(stringStatType);
            coefficients.put(statType, healCoefficients.getDouble(stringStatType));
        }
        return coefficients;
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
