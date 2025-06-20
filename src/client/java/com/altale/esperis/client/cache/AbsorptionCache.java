package com.altale.esperis.client.cache;

import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AbsorptionCache {
    private static final Map<Integer, Float> absorptionMap = new HashMap<>();

    public static void setAbsorption(LivingEntity entity, float value) {
        absorptionMap.put(entity.getId(), value);
    }

    public static float getAbsorption(LivingEntity entity) {
        return absorptionMap.getOrDefault(entity.getId(), 0f);
    }
    public static Set<Integer> getAllIds() {
        return absorptionMap.keySet();
    }

    public static void remove(int entityId) {
        absorptionMap.remove(entityId);
    }
}
