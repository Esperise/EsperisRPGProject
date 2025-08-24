package com.altale.esperis.items.itemFunction;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.skills.buff.HealBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;


public class HealingPotion extends Item {
    public static final Set<HealingPotion> INSTANCES = new HashSet<>();
    private final float baseHeal;
    private final float hpCoeff;
    private final int duration;
    private final int healTickDelta;
    private final int cooltime;
    private final String potionName;
    private final ParticleEffect particle;
    public HealingPotion(Settings settings,
                        float baseHeal, float hpCoeff, int duration, int healTickDelta, int cooltime,
                        String potionName, ParticleEffect particle) {
        super(settings.maxCount(64));
        this.baseHeal = baseHeal;
        this.hpCoeff = hpCoeff;
        this.duration = duration;
        this.cooltime = cooltime;
        this.healTickDelta = healTickDelta;
        this.potionName = potionName;
        this.particle = particle;
    }
    public HealingPotion(Settings settings,
                        float baseHeal, float hpCoeff, int duration, int healTickDelta,int cooltime,
                        String potionName) {
        super(settings.maxCount(64));
        this.baseHeal = baseHeal;
        this.hpCoeff = hpCoeff;
        this.duration = duration;
        this.healTickDelta = healTickDelta;
        this.cooltime = cooltime;
        this.potionName = potionName;
        this.particle = ParticleTypes.HAPPY_VILLAGER;
        INSTANCES.add(this);
    }

    public float getHpCoeff() {
        return hpCoeff;
    }
    public int getDuration() {
        return duration;
    }
    public int getHealTickDelta() {
        return healTickDelta;
    }
    public String getPotionName() {
        return potionName;
    }
    public float getBaseHeal() {
        return baseHeal;
    }
    public int getCooltime() {
        return cooltime;
    }

    @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
        ItemStack stack = user.getStackInHand(hand);
        if(user.getItemCooldownManager().isCoolingDown(this) || hand.equals(Hand.OFF_HAND)){
            return TypedActionResult.fail(stack);
        }
            if(!world.isClient()){
                givePotionHeal(user,baseHeal,hpCoeff,duration, healTickDelta,potionName);
                potionEffect(user, world);

                for (HealingPotion potion : INSTANCES) {
                    user.getItemCooldownManager().set(potion, cooltime);
                }
                stack.decrement(1);
            }
        return super.use(world, user, hand);
    }
    public boolean hasGlint(ItemStack stack) {
        if(potionName.equals("엘릭서")){
            return true;
        }
        return false; // 무조건 인챈트 반짝임
    }


    private void givePotionHeal(PlayerEntity user, float baseHeal, float hpCoeffi, int duration, int healTickDelta, String potionName){
        float healAmount= user.getMaxHealth()* hpCoeffi + baseHeal;
        HealBuff.giveHealBuff(user, duration, healTickDelta, healAmount,potionName);
        //FIXME
        if(potionName.equals("테스트용 쿨타임 감소 포션")){
            CoolTimeManager.allCoolTimePercentReduction((ServerPlayerEntity) user, 100);
        }
    }
    private void potionEffect(PlayerEntity user, World world){
        world.playSound(
                null,
                user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_GENERIC_DRINK,
                SoundCategory.PLAYERS, 1.0F, 1.0F

        );
        ((ServerWorld) user.getWorld()).spawnParticles(
                ParticleTypes.HAPPY_VILLAGER,
                user.getX(), user.getY(), user.getZ(), 35, 0.75,1.5,0.75,0
        );
    }
}
