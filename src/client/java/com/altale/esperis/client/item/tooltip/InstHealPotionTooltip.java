package com.altale.esperis.client.item.tooltip;

import com.altale.esperis.items.ModItems;
import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class InstHealPotionTooltip {
    public static void registerTooltip() {
        ItemTooltipCallback.EVENT.register(
                (stack, ctx, lines) -> {
                    if (stack.getItem() == ModItems.INSTANT_HEAL_POTION) {
                        ClientPlayerEntity player = MinecraftClient.getInstance().player;
                        if (player != null) {
                            float maxHp = player.getMaxHealth();
                            PlayerLevelComponent levelComponent = PlayerLevelComponent.KEY.get(player);
                            int level = levelComponent.getLevel();

                            Text tooltip= Text.literal("")
                                            .append(Text.literal(String.valueOf(Math.round(12 + maxHp * 0.07 ))+" 회복").formatted(Formatting.GREEN))
                                                    .append( " = ( 12 + ♥ 7%) ");

                            lines.add(tooltip);
//                            lines.add(Text.literal(Math.round(10 + maxHp * 0.05 ) + " = ( 10 + ♥ 5%)  즉시 회복").fillStyle());
                            lines.add(Text.literal("사용 쿨타임 : 8초").formatted(Formatting.UNDERLINE));
                        }
                    }
                });
    }
}
