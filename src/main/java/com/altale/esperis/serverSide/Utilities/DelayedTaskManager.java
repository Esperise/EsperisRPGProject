package com.altale.esperis.serverSide.Utilities;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

public class DelayedTaskManager {
    private static class TaskData{
        Entity target;
        int nextRunTimeDelay;
        TaskAction taskAction;
        String taskSkillId;
        int maxRepeatCount;
        int currentRepeatCount;
        Long startTime;
        Long nextRunTime;
        Long endTime ;

        TaskData(Entity target, int nextRunTimeDelay, TaskAction taskAction, String taskSkillId
                , int maxRepeatCount, Long startTime, Long nextRunTime){
            this.target = target;
            this.nextRunTimeDelay = nextRunTimeDelay;
            this.taskAction = taskAction;
            this.taskSkillId = taskSkillId;
            this.maxRepeatCount = maxRepeatCount;
            this.startTime = startTime;
            this.endTime = startTime + ((long) nextRunTimeDelay * maxRepeatCount);
            this.nextRunTime = nextRunTime;
        }
    }
    private static final Map<ServerWorld, Map<UUID, Map<String,TaskData>>> delayedTaskMap = new HashMap<>();
    private static final Map<ServerWorld, Map<UUID, Map<String,TaskData>>> delayedTaskCopyMap = new HashMap<>();
    private static final Map<ServerWorld, Map<UUID, Map<String,TaskData> >> pendingAdds = new HashMap<>();
    private static final Map<ServerWorld, Map<UUID,List<String> >> deleteRequest = new HashMap<>();
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
            if(!deleteRequest.isEmpty()){
                for(var worldEntry : deleteRequest.entrySet()) {
                    ServerWorld world = worldEntry.getKey();
                    Map<UUID, Map<String, TaskData>> destWorld =
                            delayedTaskMap.computeIfAbsent(world, k -> new HashMap<>());
                    for (var uEntry : worldEntry.getValue().entrySet()) {
                        UUID uuid = uEntry.getKey();
                        List<String> taskIdList = uEntry.getValue();
                        for(String taskId : taskIdList){
                            Map<String, TaskData> destByTask =
                                    destWorld.computeIfAbsent(uuid, k -> new HashMap<>());
                            TaskData existing =destByTask.get(taskId);
                            if(existing != null){
                                existing.currentRepeatCount = existing.maxRepeatCount;
                                destByTask.remove(taskId);
                                System.out.println(taskId+"삭제 완료");
                            }
                        }

                    }
                }
                deleteRequest.clear();
            }
            delayedTaskCopyMap.putAll(delayedTaskMap);


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
                            int currentRepeatCount = taskData.currentRepeatCount;
                            if(target == null || target.isRemoved()|| target instanceof LivingEntity livingEntity && !livingEntity.isAlive()){
                                taskIdMapIter.remove();
                            }
                            Long nextRunTime = taskData.nextRunTime;

                            if(taskData.currentRepeatCount >= taskData.maxRepeatCount){
                                System.out.println("taskList 삭제");
                                taskIdMapIter.remove();
                            }
                            if(worldTime>= (nextRunTime)){
                                taskData.taskAction.run(world, target, currentRepeatCount);
                                taskData.currentRepeatCount++;
                                taskData.nextRunTime += taskData.nextRunTimeDelay;
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



    public static void addTask(ServerWorld serverworld, Entity target, Runnable runnable, int delayTime, String taskSkillId, int maxRepeatCount) {
        addTaskAction(serverworld,target,  TaskAction.of(runnable)  , delayTime, taskSkillId, maxRepeatCount);
    }
    public static void addTask(ServerWorld serverworld, Entity target, Runnable runnable,Runnable last, int delayTime, String taskSkillId, int maxRepeatCount) {
        addTaskAction(serverworld,target,  TaskAction.of(runnable)  , delayTime, taskSkillId, maxRepeatCount);
    }
    public static void addTask(ServerWorld serverworld, Entity target, IntConsumer c, int delayTime, String taskSkillId, int maxRepeatCount) {
        addTaskAction(serverworld,target,  TaskAction.of(c)  , delayTime, taskSkillId, maxRepeatCount);
    }
    public static void addTask(ServerWorld serverworld, Entity target, BiConsumer<ServerWorld, Integer> biConsumer, int delayTime, String taskSkillId, int maxRepeatCount) {
        addTaskAction(serverworld,target,  TaskAction.of(biConsumer)  , delayTime, taskSkillId, maxRepeatCount);
    }
    public static void addTask(ServerWorld serverworld, Entity target, TriConsumer<ServerWorld,Entity, Integer> biConsumer, int delayTime, String taskSkillId, int maxRepeatCount) {
        addTaskAction(serverworld,target,  TaskAction.of(biConsumer)  , delayTime, taskSkillId, maxRepeatCount);
    }

    private static void addTaskAction(ServerWorld serverworld, Entity target, TaskAction taskAction, int delayTime, String taskSkillId, int maxRepeatCount) {
        long startTime = serverworld.getTime();
        UUID uuid = target.getUuid();
        long nextRunTime = serverworld.getTime() + delayTime;
        TaskData newData = new TaskData(target, delayTime, taskAction, taskSkillId, maxRepeatCount, startTime, nextRunTime);
        pendingAdds
                .computeIfAbsent(serverworld, k -> new HashMap<>())
                .computeIfAbsent(uuid,  k -> new HashMap<>())
                .put(taskSkillId, newData);
    }
    public static void deleteTask(ServerWorld world, Entity target, String taskId){
        UUID uuid = target.getUuid();
        System.out.println("삭제하려는 taskId:" + taskId);
        deleteRequest
                .computeIfAbsent(world, k-> new HashMap<>())
                .computeIfAbsent(uuid, k-> new ArrayList<>() )
                .add(taskId);
    }
    public static int getCurrentRepeatCount(ServerWorld world, Entity target, String taskId){
        UUID uuid = target.getUuid();
        TaskData targetTask = delayedTaskCopyMap.computeIfAbsent(world, k -> new HashMap<>())
                .computeIfAbsent(uuid,  k -> new HashMap<>())
                .get(taskId);
        if(targetTask != null){
            return targetTask.currentRepeatCount;
        }else{
            return -1;
        }
    }
}
