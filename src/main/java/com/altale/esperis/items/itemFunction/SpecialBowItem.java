package com.altale.esperis.items.itemFunction;

import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.SpecialBowSkill;
import com.altale.esperis.skills.buff.AbsorptionBuff;
import com.altale.esperis.skills.debuff.DotDamageVer2;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SpecialBowItem extends Item {
    public SpecialBowItem() {
        super(new FabricItemSettings().maxCount(1));
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
            SpecialBowSkill.useSpecialBow((ServerPlayerEntity) user, (ServerWorld) world);
            PlayerFinalStatComponent statComponent = PlayerFinalStatComponent.KEY.get(user);
            double spd = statComponent.getFinalStat(StatType.SPD);

            int shotCooltime = (int) Math.max(15,100*(2-spd));
            AbsorptionBuff.giveAbsorptionBuff((ServerWorld) world, user, "", 5,20);
            cooldownManager.set(this, shotCooltime );
        }
        return super.use(world, user, hand);
    }

}
