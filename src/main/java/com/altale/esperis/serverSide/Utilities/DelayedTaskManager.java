package com.altale.esperis.serverSide.Utilities;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class DelayedTaskManager {
    private static class TaskData{
        Entity target;
        int nextRunTimeDelay;
        Runnable task;
        String taskSkillId;
        int maxRepeatCount;
        int currentRepeatCount;
        Long startTime;
        Long nextRunTime;
        Long endTime ;

        TaskData(Entity target, int nextRunTimeDelay, Runnable task, String taskSkillId
                , int maxRepeatCount, Long startTime, Long nextRunTime){
            this.target = target;
            this.nextRunTimeDelay = nextRunTimeDelay;
            this.task = task;
            this.taskSkillId = taskSkillId;
            this.maxRepeatCount = maxRepeatCount;
            this.startTime = startTime;
            this.endTime = startTime + ((long) nextRunTimeDelay * maxRepeatCount);
            this.nextRunTime = nextRunTime;
        }
    }
    private static final Map<ServerWorld, Map<UUID, Map<String,TaskData>>> delayedTaskMap = new HashMap<>();
    private static final Map<ServerWorld, Map<UUID, Map<String,TaskData> >> pendingAdds = new HashMap<>();
    public static void register(){
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!pendingAdds.isEmpty()) {
                for (var worldEntry : pendingAdds.entrySet()) {
                    ServerWorld world = worldEntry.getKey();
                    long now = world.getTime();

                    Map<UUID, Map<String, TaskData>> destWorld =
                            delayedTaskMap.computeIfAbsent(world, k -> new HashMap<>());

                    for (var uEntry : worldEntry.getValue().entrySet()) {
                        UUID uuid = uEntry.getKey();
                        Map<String, TaskData> incomingByTask =
                                uEntry.getValue();

                        Map<String, TaskData> destByTask =
                                destWorld.computeIfAbsent(uuid, k -> new HashMap<>());

                        for (var tEntry : incomingByTask.entrySet()) {
                            String taskId = tEntry.getKey();
                            TaskData incoming = tEntry.getValue();

                            TaskData existing = destByTask.get(taskId);
                            if (existing != null) {
                                // ★ 동일 taskId 재요청 → 스케줄 리셋
                                if(now+ existing.nextRunTimeDelay > existing.nextRunTime){

                                }else{
                                    long base = Math.max(now, existing.nextRunTime);
                                    existing.startTime        = base;
                                    existing.nextRunTime      = base + existing.nextRunTimeDelay;
                                    existing.endTime          = base + (long) existing.nextRunTimeDelay * existing.maxRepeatCount;
                                    existing.currentRepeatCount = 0;
                                }

                                // (선택) 작업/대상 갱신이 필요하면:
//                                existing.task   = incoming.task;
//                                existing.target = incoming.target;
                            } else {
                                destByTask.put(taskId, incoming);
                            }
                        }
                    }
                }
                pendingAdds.clear();
            }
            for(ServerWorld world : server.getWorlds()){
                Map<UUID, Map<String,TaskData>> worldTaskMap = delayedTaskMap.getOrDefault(world, new HashMap<>());
                if(worldTaskMap.isEmpty()) continue;
                Long worldTime = world.getTime();
                Iterator<Map.Entry<UUID, Map<String,TaskData>>> uuidIter = worldTaskMap.entrySet().iterator();
                while(uuidIter.hasNext()){
                    Map.Entry<UUID, Map<String,TaskData>> uuidEntry = uuidIter.next();
                    Map<String, TaskData> taskMap = uuidEntry.getValue();
                    Iterator<Map.Entry<String,TaskData>> taskIdMapIter = uuidEntry.getValue().entrySet().iterator();
                    while(taskIdMapIter.hasNext()){
                        Map.Entry<String,TaskData> taskIdEntry = taskIdMapIter.next();
                        String taskId = taskIdEntry.getKey();
                        TaskData taskData = taskIdEntry.getValue();
                            Entity target = taskData.target;
                            if(target == null || target.isRemoved()|| target instanceof LivingEntity livingEntity && !livingEntity.isAlive()){
                                taskIdMapIter.remove();
                            }
                            Long nextRunTime = taskData.nextRunTime;
                            if(worldTime>= (nextRunTime)){
                                taskData.task.run();
                                taskData.currentRepeatCount++;
                                taskData.nextRunTime += taskData.nextRunTimeDelay;
                            }
                            if(taskData.currentRepeatCount >= taskData.maxRepeatCount){
                                System.out.println("taskList 삭제");
                                taskIdMapIter.remove();
                            }
                    }
                    if(taskMap.isEmpty()){
                        uuidIter.remove();
                    }
                }
                if(worldTaskMap.isEmpty()){
                    delayedTaskMap.remove(world);
                }
            }
        });
    }



    public static void addTask(ServerWorld serverworld, Entity target, Runnable task, int delayTime, String taskSkillId, int maxRepeatCount) {
        long startTime = serverworld.getTime();
        UUID uuid = target.getUuid();
        long nextRunTime = serverworld.getTime() + delayTime;
        TaskData newData = new TaskData(target, delayTime, task, taskSkillId, maxRepeatCount, startTime, nextRunTime);
        pendingAdds
                .computeIfAbsent(serverworld, k -> new HashMap<>())
                .computeIfAbsent(uuid,  k -> new HashMap<>())
                .put(taskSkillId, newData);


    }
}
