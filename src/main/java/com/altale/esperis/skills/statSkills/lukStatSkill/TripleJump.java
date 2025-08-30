package com.altale.esperis.skills.statSkills.lukStatSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.visualEffect.DrawCircle;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.core.jmx.Server;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.google.common.primitives.Floats.max;

public class TripleJump {
    private static final Map<ServerWorld, Map<UUID, Map<Long, Runnable>>> delayedTasksTripleJump = new HashMap<>();
    public static final String skillName= SkillsId.LUK_1.getSkillName();
    public static final int cooltime = 100;
    public static void tripleJump(ServerPlayerEntity player, ServerWorld  world) {
        long now = world.getTime();

        if(CoolTimeManager.isOnCoolTime(player,skillName)){

        }
        else{
            CoolTimeManager.setCoolTime(player, skillName,cooltime);
            doJump(player,world);
            for(long trig=now; trig<=now+10; trig+=10){
                delayedTasksTripleJump
                        .computeIfAbsent(world, w -> new HashMap<>())
                        .computeIfAbsent(player.getUuid(), u -> new HashMap<>())
                        .put(trig, () -> doJump((ServerPlayerEntity) player, world));
            }
        }
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerWorld world1 : server.getWorlds()) {
                Map<UUID, Map<Long, Runnable>> UuidTasks = delayedTasksTripleJump.get(world1);
                if (UuidTasks == null) continue;
                else{
                    for (Map.Entry<UUID, Map<Long, Runnable>> uuidEntry : UuidTasks.entrySet()) {
                        Map<Long, Runnable> taskMap = uuidEntry.getValue();
                        long t = world1.getTime();
                        Runnable task = taskMap.remove(t);
                        if (task != null) task.run();
                        if (UuidTasks.isEmpty()) delayedTasksTripleJump.remove(world1);
                    }

                }

            }
        });
    }
    private static void doJump(ServerPlayerEntity player, ServerWorld world) {
//        AbsorptionBuff.giveAbsorptionBuff(world,player,"triple_jump",20,20);
//        CoolTimeManager.specificCoolTimeReduction(player, "double_step_rc", 20);
        Vec3d look= player.getRotationVec(1.0f);
        PlayerFinalStatComponent playerFinalStatComponent= PlayerFinalStatComponent.KEY.get(player);
        double spd= playerFinalStatComponent.getFinalStat(StatType.SPD);
        double power= 0.6 * (1+(spd/2)) ;
        Vec3d velocity = new Vec3d(look.x * power, Math.max(0.3* (1+(spd/2)), look.y), look.z * power);
        player.addVelocity(velocity.x, velocity.y, velocity.z);
        player.velocityModified = true;
//        Vec3d offsetEye = eye.add(lateral);
//        Vec3d pos = offsetEye.add(dir);
//        if (player.getWorld() instanceof ServerWorld serverWorld){
//            serverWorld.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
//                    pos.x, pos.y, pos.z, 20, 0.5, 0.5, 0.5, 0.1);
//        }
        DrawCircle.spawnCircle(player, world, 1.5, 1.5, 50, 0,-1,0,
                0,0,0,0,0,0,1.0f,50);
    }
}
