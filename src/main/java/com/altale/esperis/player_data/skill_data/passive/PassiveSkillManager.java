package com.altale.esperis.player_data.skill_data.passive;

import com.altale.esperis.items.itemFunction.SpecialBowItem;
import com.altale.esperis.player_data.skill_data.PlayerSkillComponent;
import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.buff.HealBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import com.altale.esperis.skills.debuff.DotTypeVer2;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.concurrent.ThreadLocalRandom;

public class PassiveSkillManager {

    // 피해를 입을 시
    // 평타 공격시/ 피해를 줄 때

    public static float getDamageFlag(PlayerEntity player, float damage){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        if(playerSkillComponent.hasPassiveSkill(SkillsId.STR_150)){
            damage= damage/ 2;
            DotDamageVer2.giveDotDamage(player, player,
                    100, 20, damage, DotTypeVer2.DamageSource_Generic,true, 1, SkillsId.STR_150.getSkillName());
            AbilityBuff.giveBuff(player, SkillsId.STR_150.getSkillName(), StatType.SPD,100,0,0.04,4);
            AbilityBuff.giveBuff(player, SkillsId.STR_150.getSkillName(), StatType.ATTACK_SPEED,100,0,0.04,4);
            AbilityBuff.giveBuff(player, SkillsId.STR_150.getSkillName(), StatType.DEF,100,0,4,4);
            System.out.println("죽음의 저항으로 유예된 피해: " + damage);
        }
        return damage;
    }
    public static void criticalFlag(PlayerEntity player, LivingEntity target){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);

        if(playerSkillComponent.hasPassiveSkill(SkillsId.LUK_50)){
            float atk= (float) playerFinalStatComponent.getFinalStat(StatType.ATK);
            HealBuff.giveHealBuff(player, 140, 7, 5+ atk, SkillsId.LUK_50.getSkillName());
        }

        if(target instanceof PlayerEntity targetPlayer){

        }else{

        }
    }
    public static void tickFlag(PlayerEntity player){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        if (player.getWorld().getTime() % 40 == 0) {
            if(playerSkillComponent.hasPassiveSkill(SkillsId.DUR_50)){
                float lostHealth = player.getMaxHealth() - player.getHealth();
                player.heal(Math.min(4 , 2+ lostHealth/25));
            }
        }
    }
    public static void bowHit(PlayerEntity player, LivingEntity target){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if(playerSkillComponent.isUnlockedSkill(SkillsId.DEX_75)){
            CoolTimeManager.specificCoolTimeReduction((ServerPlayerEntity) player, SkillsId.DEX_75.getSkillName(),20);
        }
        if(playerSkillComponent.hasPassiveSkill(SkillsId.DEX_100)){
            AbilityBuff.giveBuff(player, SkillsId.DEX_100.getSkillName(), StatType.ATTACK_SPEED, 160,0, 0.07,8);
        }
    }
    public static float bowHitAddDamage(PlayerEntity player, LivingEntity target, float damage){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        ItemStack itemStack = player.getInventory().getMainHandStack();

        if(itemStack.hasNbt()){
            NbtCompound nbtCompound = itemStack.getOrCreateNbt();
                if(playerSkillComponent.hasPassiveSkill(SkillsId.DEX_50)){
                    player.sendMessage(Text.literal("패시브: 거인학살자"));
                    damage = damage+  6 + target.getMaxHealth()*3/100;
            }
                if(playerSkillComponent.hasPassiveSkill(SkillsId.DEX_150)){
                    DotDamageVer2.giveDotDamage(target, player, 80, 10,
                            target.getMaxHealth()/20, DotTypeVer2.Bleed,true, 1, SkillsId.DEX_50.getSkillName());
                }
        }

        return damage;
    }
    public static void bowHitApplyEffect(PlayerEntity player, LivingEntity target){
//        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
//        if(playerSkillComponent.hasPassiveSkill(SkillsId.DEX_150)){
//            target.setFrozenTicks(target.getFrozenTicks() + 40);
//        }
    }
    public static void hpFallBelowXPercent(PlayerEntity player,float damage ,float percent){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        float currentHealthPercentage= ((player.getHealth()-damage) /player.getMaxHealth()) * 100;
        if(currentHealthPercentage < percent){
            if(playerSkillComponent.hasPassiveSkill(SkillsId.STR_100) && !CoolTimeManager.isOnCoolTime((ServerPlayerEntity) player, SkillsId.STR_100.getSkillName() )){
                String skillName = SkillsId.STR_100.getSkillName();
                float barrierAmount = player.getMaxHealth()/5;
                if(barrierAmount > damage){
                    System.out.println("보호막 일단 바로 적용: " + damage);
                    player.setAbsorptionAmount(barrierAmount);
                    barrierAmount-=damage;
                }else{
                    System.out.println("보호막 바로 깨짐");
                    player.setAbsorptionAmount(barrierAmount);
                    barrierAmount=0;
                }
                HealBuff.giveHealBuff(player, 80, 4,player.getMaxHealth()*3/25,skillName);
                AbsorptionBuff.giveAbsorptionBuff((ServerWorld) player.getWorld(),player, skillName, barrierAmount,80);
                CoolTimeManager.setCoolTime((ServerPlayerEntity) player, skillName, 1200);
            }
            if(playerSkillComponent.hasPassiveSkill(SkillsId.LUK_150) && !CoolTimeManager.isOnCoolTime((ServerPlayerEntity) player, SkillsId.LUK_150.getSkillName())){
                String skillName = SkillsId.LUK_150.getSkillName();
//                float atk = (float) playerFinalStatComponent.getFinalStat(StatType.ATK);
//                float barrierAmount = 10+atk*2;
//                if(barrierAmount > damage){
//                    System.out.println("보호막 일단 바로 적용: " + damage);
//                    player.setAbsorptionAmount(barrierAmount);
//                    barrierAmount-=damage;
//                }else{
//                    System.out.println("보호막 바로 깨짐");
//                    player.setAbsorptionAmount(barrierAmount);
//                    barrierAmount=0;
//                }
//                AbsorptionBuff.giveAbsorptionBuff((ServerWorld) player.getWorld(),player, skillName,barrierAmount ,40);
                AbilityBuff.giveBuff(player, skillName, StatType.SPD, 40,0, 0.25,1);
                AbilityBuff.giveBuff(player, skillName, StatType.AVD, 40,0, 1,1);
                CoolTimeManager.specificCoolTimePercentReduction((ServerPlayerEntity) player, SkillsId.LUK_75.getSkillName(), 100);
                CoolTimeManager.setCoolTime((ServerPlayerEntity) player, skillName, 1200);
            }
        }
    }

    public static void getBarrierFlag(PlayerEntity player, LivingEntity target){
        if(target instanceof PlayerEntity targetPlayer){

        }else{

        }
    }
    public static void avdFlag(PlayerEntity player, LivingEntity target){
        if(target instanceof PlayerEntity targetPlayer){

        }else{

        }
    }
    public static void normalAttackFlag(PlayerEntity player, LivingEntity target){
        if(target instanceof PlayerEntity targetPlayer){

        }else{

        }
    }
    public static void dotExplodeFlag(PlayerEntity player, LivingEntity target){

    }
    public static void killEntityFlag(PlayerEntity player, LivingEntity target){
        //expMixin으로
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        if(playerSkillComponent.hasPassiveSkill(SkillsId.STR_150)){
            double atk= playerFinalStatComponent.getFinalStat(StatType.ATK);
            HealBuff.giveHealBuff(player, 40, 4, atk*0.5 ,SkillsId.STR_150.getSkillName());
        }
    }



}
