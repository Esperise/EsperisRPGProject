package com.altale.esperis.player_data.skill_data.passive;

import com.altale.esperis.items.itemFunction.SpecialBowItem;
import com.altale.esperis.player_data.skill_data.PlayerSkillComponent;
import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.serverSide.Utilities.HealthHanlder;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.buff.HealBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import com.altale.esperis.skills.debuff.DotTypeVer2;
import com.altale.esperis.skills.statSkills.dexStatSkill.FastAccurateAdvanced;
import com.altale.esperis.skills.statSkills.dexStatSkill.StormsPoem;
import com.altale.esperis.skills.statSkills.durSkill.PassiveBarrierBash;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PassiveSkillManager {


    public static float getDamageFlag(PlayerEntity player,LivingEntity attacker ,float damage){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        if(playerSkillComponent.hasPassiveSkill(SkillsId.STR_150)){
            damage= damage/ 2;
            DotDamageVer2.giveDotDamage(player, player,
                    80, 20, damage, DotTypeVer2.DamageSource_Generic,true, 1, SkillsId.STR_150.getSkillName());
            AbilityBuff.giveBuff(player, SkillsId.STR_150.getSkillName(), StatType.SPD,80,0,0.04,5);
            AbilityBuff.giveBuff(player, SkillsId.STR_150.getSkillName(), StatType.ATTACK_SPEED,80,0,0.04,5);
            AbilityBuff.giveBuff(player, SkillsId.STR_150.getSkillName(), StatType.DEF,80,0,4,5);
            System.out.println("죽음의 저항으로 유예된 피해: " + damage);
        }
        if(playerSkillComponent.hasPassiveSkill(SkillsId.DUR_100)){
            if(attacker != null && !CoolTimeManager.isOnCoolTime((ServerPlayerEntity) player, "패시브: 반격")){
                CoolTimeManager.setCoolTime((ServerPlayerEntity) player, "패시브: 반격", 30);
                float def = (float) playerFinalStatComponent.getFinalStat(StatType.DEF);
                attacker.damage(player.getDamageSources().playerAttack(player), def * 0.045f);
            }
            if(!CoolTimeManager.isOnCoolTime((ServerPlayerEntity) player, SkillsId.DUR_100.getSkillName())){
                float maxHealth = (float) playerFinalStatComponent.getFinalStat(StatType.MAX_HEALTH);
                float barrierAmount = maxHealth * 0.04f;
                if(barrierAmount > damage){
                    player.setAbsorptionAmount(barrierAmount);
                    barrierAmount-=damage;
                }else{
                    player.setAbsorptionAmount(barrierAmount);
                    barrierAmount=0;
                }

                AbsorptionBuff.giveAbsorptionBuff((ServerWorld) player.getWorld(), player, SkillsId.DUR_100.getSkillName(), barrierAmount, 40);
                CoolTimeManager.setCoolTime((ServerPlayerEntity) player,SkillsId.DUR_100.getSkillName(),200 );
            }

        }
        return damage;
    }
    public static double giveDamage(PlayerEntity player,LivingEntity target, float damage){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        if(playerSkillComponent.hasPassiveSkill(SkillsId.STR_50)){
            PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
            double atk = playerFinalStatComponent.getFinalStat(StatType.ATK);
            if(atk*0.0075 < 0.15){
                AbilityBuff.giveBuff(player,SkillsId.STR_50.getSkillName(), StatType.ATK,160,0,0.15,20);
            }else{
                AbilityBuff.giveBuff(player,SkillsId.STR_50.getSkillName(), StatType.ATK,160,0.75,0,20);
            }
            System.out.println(AbilityBuff.getBuffStack(player, SkillsId.STR_50.getSkillName()));
            if(AbilityBuff.getBuffStack(player, SkillsId.STR_50.getSkillName()) >=20){
                player.heal((float) (damage * 0.13));
                    Runnable task= ()->{
                        if (player.getWorld() instanceof ServerWorld serverWorld) {
                            Vec3d pos = player.getPos();
                            serverWorld.spawnParticles(ParticleTypes.GLOW,
                                    pos.x, pos.y, pos.z, 12, 0.3, 1.8, 0.3, 0);
                        }
                    };
                    DelayedTaskManager.addTask((ServerWorld) player.getWorld(),player, task, 5, SkillsId.STR_50.getSkillName()+" 최대 스택 효과",28);
            }
        }
        if(AbilityBuff.hasBuff(player, SkillsId.DUR_175.getSkillName())){
            player.heal((float) (damage * 0.25));
        }




        return damage;
    }
    public static void criticalFlag(PlayerEntity player, LivingEntity target, float damage){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);

        if(playerSkillComponent.hasPassiveSkill(SkillsId.LUK_50)){
            player.heal(damage * 0.15f);
        }

        if(target instanceof PlayerEntity targetPlayer){

        }else{

        }
    }
    public static void hpRegenFlag(PlayerEntity player){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        if (player.getWorld().getTime() % 80 == 0) {
            if(playerSkillComponent.hasPassiveSkill(SkillsId.DUR_50)){
                float lostHealth = player.getMaxHealth() - player.getHealth();
                System.out.println("잃은 체력: " + lostHealth);
                System.out.println("패시브 회복: " + Math.max(4 , 2+ lostHealth/25));
                player.heal(Math.max(4 , 2+ lostHealth/40));
            }
        }
    }
    public static void bowHit(PlayerEntity player, LivingEntity target){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        if(playerSkillComponent.isUnlockedSkill(SkillsId.DEX_75)){
            CoolTimeManager.specificCoolTimeReduction((ServerPlayerEntity) player, SkillsId.DEX_75.getSkillName(), FastAccurateAdvanced.hitCooltimeReduce);
        }
        if(playerSkillComponent.isUnlockedSkill(SkillsId.DEX_75)){
            CoolTimeManager.specificCoolTimeReduction((ServerPlayerEntity) player, SkillsId.DEX_175.getSkillName(), StormsPoem.bowHitCooltimeReduceTick);
        }
        if(playerSkillComponent.hasPassiveSkill(SkillsId.DEX_100)){
            AbilityBuff.giveBuff(player, SkillsId.DEX_100.getSkillName(), StatType.ATTACK_SPEED,240,0,0.05,15);
        }
    }
    public static float bowHitAddDamage(PlayerEntity player, LivingEntity target, float damage){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        ItemStack itemStack = player.getInventory().getMainHandStack();

        if(itemStack.hasNbt()){
            NbtCompound nbtCompound = itemStack.getOrCreateNbt();
                if(playerSkillComponent.hasPassiveSkill(SkillsId.DEX_50)){
                    damage = damage+  4 + target.getMaxHealth()*1/20;
            }

                if(playerSkillComponent.hasPassiveSkill(SkillsId.DEX_150)){
                    DotDamageVer2.giveDotDamage(target, player, 60, 10,
                            2+target.getMaxHealth()/25, DotTypeVer2.Bleed,true, 1, SkillsId.DEX_150.getSkillName());
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
                HealBuff.giveHealBuff(player, 100, 5,player.getMaxHealth()*3/20,skillName);
                AbsorptionBuff.giveAbsorptionBuff((ServerWorld) player.getWorld(),player, skillName, barrierAmount,100);
                CoolTimeManager.setCoolTime((ServerPlayerEntity) player, skillName, 1200);
            }
            if(playerSkillComponent.hasPassiveSkill(SkillsId.LUK_150) && !CoolTimeManager.isOnCoolTime((ServerPlayerEntity) player, SkillsId.LUK_150.getSkillName())){
                String skillName = SkillsId.LUK_150.getSkillName();
                if (player.getWorld() instanceof ServerWorld serverWorld) {
                    Vec3d pos = player.getPos();
                    serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                            pos.x, pos.y, pos.z, 3000, 2, 2.2, 2, 0.002);
                }
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 80,4));
                AbilityBuff.giveBuff(player, skillName, StatType.SPD, 50,0, 0.25,1);
                AbilityBuff.giveBuff(player, skillName, StatType.AVD, 50,0, 1,1);
                CoolTimeManager.specificCoolTimePercentReduction((ServerPlayerEntity) player, SkillsId.LUK_75.getSkillName(), 100);
                CoolTimeManager.setCoolTime((ServerPlayerEntity) player, skillName, 1200);
            }
        }
    }

    public static void getBarrierFlag(LivingEntity target, float barrierAmount){
        if(target instanceof PlayerEntity player){
            PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
            if(playerSkillComponent.hasPassiveSkill(SkillsId.DUR_150)){
                PassiveBarrierBash.PassiveBarrierBash((ServerWorld) player.getWorld(), (ServerPlayerEntity) player, barrierAmount);
            }
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
    public static void instantDotDamageFlag(PlayerEntity player, LivingEntity target, double damage){
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        if(playerSkillComponent.hasPassiveSkill(SkillsId.LUK_100)){
            System.out.println("원본 데미지: " + damage +"\n광역피해: "+ damage *0.3);
            PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
            Box box= player.getBoundingBox().expand(2.5f,0,2.5f);
            AbilityBuff.giveBuff(player, SkillsId.LUK_100.getSkillName(),StatType.ATK ,80, 3, 0, 5);
            List<Entity> entities = player.getWorld().getOtherEntities(player, box,
                    entity -> entity instanceof LivingEntity && entity.isAlive() && !(entity.isRemoved()));
            for(Entity entity : entities){
                if(entity instanceof LivingEntity livingEntity){
                    livingEntity.damage(target.getWorld().getDamageSources().playerAttack(player), (float) (damage*0.3));
                }
            }
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                Vec3d pos = player.getPos();
                serverWorld.spawnParticles(new DustParticleEffect(new Vector3f(1.0f,0.0f,0.0f),0.5f),
                        pos.x, pos.y, pos.z, 250, 2.5, 0, 2.5, 0.0);
                serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL,
                        pos.x, pos.y, pos.z, 10, 2.5, 0, 2.5, 0.0);
            }
        }
    }
    public static void killEntityFlag(PlayerEntity player, LivingEntity target){
        //expMixin으로
        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
        PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
        if(playerSkillComponent.hasPassiveSkill(SkillsId.STR_150)){
            double atk= playerFinalStatComponent.getFinalStat(StatType.ATK);
            HealBuff.giveHealBuff(player, 60, 4, atk*0.85 ,SkillsId.STR_150.getSkillName());
        }
    }



}
