package com.altale.esperis.items.itemFunction;

import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAt;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAtDistance;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class SpecialBowItem extends Item {
    private final int specialBowCoolTime;
    private final float maxDistance;
    private final double atkCoeffi;
    private final double dexCoeffi;
    private final double baseDamage;

    public SpecialBowItem(int cooltime, float maxDistance, double atkCoeffi, double dexCoeffi, float baseDamage) {
        super(new FabricItemSettings().maxCount(1));
        this.specialBowCoolTime = cooltime;
        this.maxDistance =maxDistance;
        this.atkCoeffi = atkCoeffi;
        this.dexCoeffi = dexCoeffi;
        this.baseDamage = baseDamage;
    }
    public SpecialBowItem(){
        this(80, 40F,0.25,0.15,2);
    }
    public  int getSpecialBowCoolTime() {
        return specialBowCoolTime;
    }

    public int getCooltime(PlayerEntity user){
        PlayerFinalStatComponent statComponent = PlayerFinalStatComponent.KEY.get(user);
        double spd = statComponent.getFinalStat(StatType.SPD);
        return (int) Math.max((double) specialBowCoolTime /5 , specialBowCoolTime *(2-spd));
    }
    public double getMaxDistance(){
        return maxDistance;
    }
    public double getAtkCoeffi(){
        return atkCoeffi;
    }
    public double getDexCoeffi(){
        return dexCoeffi;
    }
    public double getBaseDamage(){
        return baseDamage;
    }
    public double specialBowDamage(PlayerEntity user){
        PlayerFinalStatComponent statComponent = PlayerFinalStatComponent.KEY.get(user);
        double atk= statComponent.getFinalStat(StatType.ATK);
        double dex= statComponent.getFinalStat(StatType.DEX);

        double shotDamage= baseDamage + (atk * atkCoeffi) + (dex * dexCoeffi);
        PlayerInventory inventory = user.getInventory();
        boolean hasArrow=inventory.contains(Items.ARROW.getDefaultStack());
        if(hasArrow){
            shotDamage += 4;
        }
        return shotDamage;
    }
    public double damageReducedByDistance(PlayerEntity user, LivingEntity target, double shotDamage, double maxDistance){
        double distance = GetEntityLookingAtDistance.getEntityLookingAtDistance((ServerPlayerEntity) user, target);
        double halfOfMaxDistance = maxDistance/2;
        if(distance > halfOfMaxDistance){
            double overDistanceCoefficient = (double)  Math.round(100* 0.02 *(distance - halfOfMaxDistance))/100.0;
            overDistanceCoefficient= Math.max(0.5, overDistanceCoefficient);
            shotDamage *= overDistanceCoefficient;
        }
        return shotDamage;
    }
    public void specialBowEffects(boolean targeted,ServerWorld world, PlayerEntity player, LivingEntity target){
        player.getWorld().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.PLAYERS,
                1.0f,
                0.4f
        );
        if(!targeted){
            //타겟팅 실패:
            Vec3d playerLookVec= player.getRotationVec(1.0f);
            Vec3d playerCameraPos= player.getCameraPosVec(1.0f);
            Vec3d dir = player.getRotationVec(1F).normalize();
            Vec3d start = player.getCameraPosVec(1.0f);
            Vec3d end = start.add(dir.multiply(30.0));
                for(double i = 0; i<= maxDistance; i+=0.1){
                    Vec3d pos = playerCameraPos.add(playerLookVec.multiply(i));
                    Vec3d pos2 = pos.add(playerLookVec.multiply(Math.min(maxDistance,i+1.5)));
                    world.spawnParticles(new DustParticleEffect(new Vector3f(0.0f, 0.0f, 0.0f),0.1f), pos.x, pos.y, pos.z, 5, 0, 0, 0, 0);
                    world.spawnParticles(
                            ParticleTypes.CRIT,
                            pos2.x, pos2.y, pos2.z,
                            1, 0.03, 0.03, 0.03, 0
                    );
            }
        }else{
            //타겟팅
            if(targeted){
                Vec3d playerLookVec= player.getRotationVec(1.0f).normalize();
                Vec3d playerCameraPos= player.getCameraPosVec(1.0f);
                double distance = GetEntityLookingAtDistance.getEntityLookingAtDistance((ServerPlayerEntity) player, target);
                for(double i=0; i<=(float) distance ;i+=0.1){
                    Vec3d pos = playerCameraPos.add(playerLookVec.multiply(i));
                    Vec3d pos2 = pos.add(playerLookVec.multiply(Math.min(distance,i+1.5)));
                    world.spawnParticles(new DustParticleEffect(new Vector3f(0.0f, 0.0f, 0.0f),0.1f), pos.x, pos.y, pos.z, 5, 0, 0, 0, 0);
                    world.spawnParticles(
                            ParticleTypes.CRIT,
                            pos2.x, pos2.y, pos2.z,
                            1, 0.03, 0.03, 0.03, 0
                    );
                }
                player.getWorld().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ITEM_TRIDENT_HIT,
                        SoundCategory.PLAYERS,
                        1.0f,
                        0.4f
                );
            }
        }
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        ItemCooldownManager cooldownManager= user.getItemCooldownManager();
        if(cooldownManager.isCoolingDown(this)){
            return TypedActionResult.fail(stack);
        }
        if(!world.isClient){
            // 스킬
            useSpecialBow((ServerPlayerEntity) user, (ServerWorld) world);
            PlayerInventory inventory = user.getInventory();
            boolean hasArrow=inventory.contains(Items.ARROW.getDefaultStack());
            if(hasArrow){
                ItemStack itemStack = user.getProjectileType(Items.BOW.getDefaultStack());
                itemStack.decrement(1);
            }
            cooldownManager.set(this, getCooltime(user) );
        }
        return super.use(world, user, hand);
    }

    public void useSpecialBow(ServerPlayerEntity player, ServerWorld world) {
        Entity target = GetEntityLookingAt.getEntityLookingAt(player, maxDistance, 0.2);
        //
        if( target == null  ){ //타겟팅 대상 없음
            specialBowEffects(false,world,player,null);
        } else {
            // 타겟팅 대상에게
            if(target instanceof LivingEntity targetEntity){
                specialBowEffects(true,world,player,targetEntity);
                DamageSource src = world.getDamageSources().playerAttack(player);
                targetEntity.timeUntilRegen = 0;
                targetEntity.hurtTime = 0;
                double shotDamage= specialBowDamage(player);
                shotDamage= damageReducedByDistance(player, targetEntity, shotDamage, maxDistance);
                targetEntity.damage(src, (float) shotDamage);
            }
            else{
                specialBowEffects(false,world,player,null);
            }
        }

    }

}
