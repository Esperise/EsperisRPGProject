package com.altale.esperis.skills.statSkills.durSkill;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.serverSide.Utilities.ParticleHelper;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import com.altale.esperis.skills.debuff.KnockedAirborneVer2;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.IntConsumer;

public class GroundSlam {
    public static final String GROUND_SLAM = SkillsId.DUR_25.getSkillName();
    public static final float hpCoeffi = 0.02f;
    public static final float defCoeffi = 0.05f;
    public static final float baseDamage = 1f;
    public static final float allOutAttackAtkCoeffi = 0.6f;
    public static final float XZrange = 4f;


    public static void GroundSlam(ServerPlayerEntity player,ServerWorld world ) {
        if(CoolTimeManager.isOnCoolTime( player, GROUND_SLAM)){

        }else{
            CoolTimeManager.setCoolTime(player,GROUND_SLAM, 80 );
            doGroundSlam(player,world);
        }
    }
    public static void doGroundSlam(ServerPlayerEntity player,ServerWorld world){
        PlayerFinalStatComponent playerStatComponent = PlayerFinalStatComponent.KEY.get(player);
        double hp = player.getMaxHealth();
        double def = playerStatComponent.getFinalStat(StatType.DEF);
        double atk = playerStatComponent.getFinalStat(StatType.ATK);
        float damage = (float) (baseDamage+ hp*hpCoeffi + def*defCoeffi);
        boolean allOutAttack = false;
        Vec3d vec= new Vec3d(0,-1,0);
        Box range = player.getBoundingBox().expand(XZrange, -1.2f, XZrange).stretch(vec).expand(0,2.0f,0);
        if(AbilityBuff.hasBuff(player, SkillsId.DUR_175.getSkillName())){
            damage= (float) (5+ atk* allOutAttackAtkCoeffi);
            CoolTimeManager.setCoolTime(player,GROUND_SLAM, 40 );
            allOutAttack=true;
            Vec3d playerLook = player.getRotationVec(1.0f);
            player.getBoundingBox().expand(XZrange + 1.5f, -1.2f, XZrange+1.5f).stretch(vec).expand(0,2.0f,0);
        }
        List<Entity> entities = player.getWorld().getOtherEntities(player, range);
        DamageSource src= player.getDamageSources().playerAttack(player);

        for(Entity entity : entities){
            if(!(entity instanceof LivingEntity livingTarget)) continue;
            if(livingTarget.isOnGround() && !allOutAttack){
                KnockedAirborneVer2.giveKnockedAirborneVer2(livingTarget, 10,2);
                if(livingTarget instanceof PlayerEntity targetPlayer){
                    AbilityBuff.giveBuff(targetPlayer, GROUND_SLAM, StatType.SPD,30,45,0,1);
                }else{
                    livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS , 30, 2,false, false));
                }
            }else if( !livingTarget.isOnGround() && !allOutAttack){
                if(livingTarget instanceof PlayerEntity targetPlayer){
                    AbilityBuff.giveBuff(targetPlayer, GROUND_SLAM, StatType.SPD,30,45,0,1);
                }else{
                    livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS , 30, 2,false, false));
                }
            }


            livingTarget.damage(src, damage);
        }
        Runnable task;
        if (!allOutAttack) {
            task = ()->{
                for(int i =0; i<10; i++){
                    player.getWorld().playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.BLOCK_ANCIENT_DEBRIS_PLACE,
                            SoundCategory.PLAYERS,
                            1.0f,
                            0.7f
                    );
                }

            };
            IntConsumer task2 = ParticleHelper.expandingCircleXZ(
                    (ServerWorld) player.getWorld(), player,
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()),  // 원하는 파티클로 교체 가능
                    0.3, 0.72, 0.15, 120
            );
            DelayedTaskManager.addTask(world, player, task2, 1, GROUND_SLAM+" Effect", 5);



        }else{
            task = ()->{
                for(int i =0; i<10; i++) {
                    player.getWorld().playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.BLOCK_NETHER_ORE_BREAK,
                            SoundCategory.PLAYERS,
                            1.0f,
                            0.6f
                    );
                }
            };

            IntConsumer task2 = ParticleHelper.expandingCircleXZ(
                    (ServerWorld) player.getWorld(), player,
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIAMOND_BLOCK.getDefaultState()),  // 원하는 파티클로 교체 가능
                    0.3, 1.04, 0.15, 180
            );
            DelayedTaskManager.addTask(world, player, task2, 1, GROUND_SLAM+" Effect", 5);
        }

        DelayedTaskManager.addTask(world, player, task, 1, GROUND_SLAM+" sound", 5);



    }
}
