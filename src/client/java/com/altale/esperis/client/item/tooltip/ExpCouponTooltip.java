package com.altale.esperis.client.item.tooltip;

import com.altale.esperis.items.itemFunction.ExpCoupon;
import com.altale.esperis.items.itemFunction.HealingPotion;
import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class ExpCouponTooltip {
    public static void registerTooltip() {
        ItemTooltipCallback.EVENT.register(
                (stack, ctx, lines) -> {
                    if (stack.getItem() instanceof ExpCoupon) {
                        ClientPlayerEntity player = MinecraftClient.getInstance().player;
                        if (player != null) {
                            Item item = stack.getItem();
                            if(item instanceof ExpCoupon coupon) {
                                int amount = coupon.getExpAmount(item);
                                Text tooltip= Text.literal("")
                                        .append(Text.literal("사용시 "))
                                        .append(Text.literal(String.format("%d ",amount)).formatted(Formatting.GREEN, Formatting.BOLD))
                                        .append(Text.literal("경험치 획득"));
                                lines.add(tooltip);
                            }

                        }
                    }
                });
    }
}
