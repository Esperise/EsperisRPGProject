package com.altale.esperis.serverSide;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

public final class ModGameRuleEnforcer implements ModInitializer {

    @Override
    public void onInitialize() {
        // 서버가 완전히 켜진 뒤, 모든 로드된 월드에 적용
        ServerLifecycleEvents.SERVER_STARTED.register(this::applyToAllWorlds);

        // 이후 새로 로드되는 월드(네더/엔드 등)에도 적용
        ServerWorldEvents.LOAD.register((server, world) -> apply(world, server));
    }

    private void applyToAllWorlds(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            apply(world, server);
        }
    }

    private void apply(ServerWorld world, MinecraftServer server) {
        GameRules rules = world.getGameRules();
        // mobGriefing false
        rules.get(GameRules.DO_MOB_GRIEFING).set(false, server);
        // keepInventory true
        rules.get(GameRules.KEEP_INVENTORY).set(true, server);
    }
}
