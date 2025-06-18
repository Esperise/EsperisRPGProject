package com.altale.esperis.skillTest1;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerFallHandler {
    private static final Map<UUID, Boolean> ignoreFallMap = new HashMap<>();

    public static void enableIgnoreFall(PlayerEntity player) {
        ignoreFallMap.put(player.getUuid(), true);
    }

    public static boolean shouldIgnoreFall(PlayerEntity player) {

        return ignoreFallMap.getOrDefault(player.getUuid(), false);
    }

    public static void consumeIgnoreFall(PlayerEntity player) {
        ignoreFallMap.put(player.getUuid(), false); // 1회 사용

    }
}
