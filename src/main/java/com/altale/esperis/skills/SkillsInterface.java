package com.altale.esperis.skills;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public interface SkillsInterface {
    void doSkill(ServerPlayerEntity player, ServerWorld world);
}
