package com.altale.esperis.skills.statSkills.dexStatSkill;

import com.altale.esperis.items.itemFunction.SpecialBowItem;
import com.altale.esperis.player_data.skill_data.PlayerSkillComponent;
import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.skill_data.passive.PassiveSkillManager;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.serverSide.Utilities.DelayedTaskManager;
import com.altale.esperis.serverSide.Utilities.GetEntityLookingAt;
import com.altale.esperis.skills.coolTime.CoolTimeManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class TripleShot {
    public static final String skillName = SkillsId.DEX_25.getSkillName();
    public static void tripleShot(ServerPlayerEntity player, ServerWorld world) {
        if(CoolTimeManager.isOnCoolTime(player, skillName)){

        }else{
            PlayerFinalStatComponent statComponent = PlayerFinalStatComponent.KEY.get(player);
            double as = statComponent.getFinalStat(StatType.ATTACK_SPEED);
            int cooltime = (int) Math.round(1/ Math.max(0.01,0.1*as)*20);
            CoolTimeManager.setCoolTime(player, skillName,cooltime);
            ItemStack hand = player.getMainHandStack();
            if (!(hand.getItem() instanceof SpecialBowItem bow)) {
                player.sendMessage(Text.literal("특수 활을 들고 있어야 사용 가능합니다."), false);
                return;
            }
            Runnable task = () -> bow.useSpecialBow(player, world,0,20);
            DelayedTaskManager.addTask(world, player, task, 2, skillName, 3);


        }

    }

}


