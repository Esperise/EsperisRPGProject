package com.altale.esperis.client.item.tooltip;

import com.altale.esperis.client.item.toolTipManager.TooltipManager;
import com.altale.esperis.items.ModItems;
import com.altale.esperis.items.itemFunction.Artifact.Tomori;
import com.altale.esperis.items.itemFunction.SpecialBowItem;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
@Environment(EnvType.CLIENT)
public class TomoriTooltip {
    public static void registerTooltip() {
        ItemTooltipCallback.EVENT.register(
                (stack, ctx, lines) -> {

                    if (stack.getItem() == ModItems.TOMORI) {
                        ClientPlayerEntity player = MinecraftClient.getInstance().player;
                        if (player != null) {
                            PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
                            double maxHealth= player.getMaxHealth();
                            Tomori tomori = (Tomori) stack.getItem();//게임 시작될때 아이템 레지스트리가 이미 “잠겨(lock)” 있는 시점이어서, 내부적으로 Item 생성자에서 레지스트리에 항목을 등록하려다가 예외가 발생하는 겁니다.
                            Text tooltip= Text.literal("");
                                tooltip= tooltip.copy().append(Text.literal(String.format(" ⏱ : 5초 / %.2f",10 +maxHealth*3/20)+" 보호막").formatted(Formatting.DARK_PURPLE));
                                tooltip= tooltip.copy().append((Text.literal(" = ( 10 + ❤ 15% )  ")));

                            lines.add(tooltip);
                            lines.add(Text.of("⌛ : 25초").copy().formatted(Formatting.UNDERLINE));
                            lines.addAll(TooltipManager.makeStatText(stack));
                        }
                    }
                });
    }
}
