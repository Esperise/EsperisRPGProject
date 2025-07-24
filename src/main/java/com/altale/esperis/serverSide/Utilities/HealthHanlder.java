package com.altale.esperis.serverSide.Utilities;

import net.minecraft.entity.LivingEntity;

public class HealthHanlder {
    public static float getLostHealthRatio(LivingEntity target, boolean addCoefficientBoolean) {
        if(target.isAlive() && !target.isRemoved()){
            float maxHealth = target.getMaxHealth();
            float health = target.getHealth();
            if(addCoefficientBoolean){
                return (float) (((maxHealth-health)/maxHealth)+1.0);
            }
            return (maxHealth-health)/maxHealth;
        }
        else{
            if (addCoefficientBoolean) {
                return 1.0F;
            }
            return 0.0F;
        }
    }
    public static float getLostHealthRatio(LivingEntity target, boolean addCoefficientBoolean, double coefficient) {
        if(target.isAlive() && !target.isRemoved()){
            float maxHealth = target.getMaxHealth();
            float health = target.getHealth();
            if(addCoefficientBoolean){
                return (float) ((((maxHealth-health)*coefficient/maxHealth)+1.0));
            }
            return (float) (((maxHealth-health)*coefficient/maxHealth));
        }
        else{
            if (addCoefficientBoolean) {
                return 1.0F;
            }
            return 0.0F;
        }
    }
}
