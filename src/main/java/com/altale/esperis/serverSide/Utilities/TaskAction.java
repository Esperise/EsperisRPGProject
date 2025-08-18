package com.altale.esperis.serverSide.Utilities;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface TaskAction {
    void run(ServerWorld serverWorld, Entity entity, int step);
    static TaskAction of(Runnable runnable){
        return (world,entity,step) -> runnable.run();
    }
    static TaskAction of(IntConsumer consumer){
        return (world, entity, step) -> consumer.accept(step);
    }
    static TaskAction of(BiConsumer<ServerWorld, Integer> biConsumer){
        return (world, entity, step) -> biConsumer.accept(world, step);
    }
    static TaskAction of(TriConsumer<ServerWorld, Entity, Integer> triConsumer){
        return triConsumer::accept;
    }
}
