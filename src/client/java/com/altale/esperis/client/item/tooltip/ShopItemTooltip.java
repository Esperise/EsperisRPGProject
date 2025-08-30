package com.altale.esperis.client.item.tooltip;

import com.altale.esperis.items.itemFunction.ExpCoupon;
import com.altale.esperis.shop.ShopItemManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class ShopItemTooltip {
    public static void registerTooltip() {
        ItemTooltipCallback.EVENT.register(
                (stack, ctx, lines) -> {
                    if(!ShopItemManager.hasShopInfo(stack)) return;
                    int purchasePrice = ShopItemManager.getPurchasePrice(stack);
                    int salesPrice = ShopItemManager.getSalesPrice(stack);
                    Text tooltip = Text.literal("");
//                    tooltip = tooltip.copy().append(Text.literal("\n"))
                    tooltip = Text.literal("");
                    lines.add(tooltip);
                    if(purchasePrice > 0){
                        tooltip = tooltip.copy().append(Text.literal("[구매]").copy().formatted(Formatting.BOLD, Formatting.AQUA))
                                .append(Text.literal(String.format(": %d esp",ShopItemManager.getPurchasePrice(stack))));
                    }else{
                        tooltip = tooltip.copy().append(Text.literal("판매 불가").copy().formatted(Formatting.BOLD, Formatting.RED));
                    }
                    lines.add(tooltip);
                    tooltip = Text.literal("");
                    if(salesPrice >0){
                        tooltip = tooltip.copy().append(Text.literal("[판매]").copy().formatted(Formatting.BOLD, Formatting.RED))
                                .append(Text.literal(String.format(": %d esp",ShopItemManager.getSalesPrice(stack))));
                    }else{
                        tooltip = tooltip.copy().append(Text.literal("구매 불가").copy().formatted(Formatting.BOLD, Formatting.RED));
                    }
                    lines.add(tooltip);
                    tooltip = Text.literal("")
                            .append(Text.literal("[Shift] ").copy().formatted(Formatting.BOLD, Formatting.GREEN))
                            .append(Text.literal("를 누르면서 클릭시 최대 64개씩 판매"));
                    lines.add(tooltip);

                });
    }
}
