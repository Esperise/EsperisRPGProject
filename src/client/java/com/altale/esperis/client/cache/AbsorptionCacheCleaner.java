package com.altale.esperis.client.cache;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

public class AbsorptionCacheCleaner {
    public static void register() {
        ClientTickEvents.END_WORLD_TICK.register(clientWorld -> {
            if (clientWorld == null) return;

            // AbsorptionCache 내부 Map의 키(엔티티 ID)를 복사해서 순회
            Set<Integer> ids = new HashSet<>(AbsorptionCache.getAllIds());
            for (int id : ids) {
                Entity e = clientWorld.getEntityById(id);
                // 월드에 없거나, 살아있는 LivingEntity가 아니면 제거
                if (!(e instanceof LivingEntity)) {
                    AbsorptionCache.remove(id);
                }
            }
        });
    }
}