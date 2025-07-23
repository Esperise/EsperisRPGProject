package com.altale.esperis.client.item.tooltip;

import com.altale.esperis.items.ModItems;
import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
@Environment(EnvType.CLIENT)
public class SpecialBow1Tooltip {
        public static void registerTooltip() {
            ItemTooltipCallback.EVENT.register(
                    (stack, ctx, lines) -> {
                        if (stack.getItem() == ModItems.SPECIAL_BOW_1) {
                            ClientPlayerEntity player = MinecraftClient.getInstance().player;
                            if (player != null) {

                                PlayerFinalStatComponent playerFinalStatComponent = PlayerFinalStatComponent.KEY.get(player);
                                double atk = playerFinalStatComponent.getFinalStat(StatType.ATK);
                                double spd = playerFinalStatComponent.getFinalStat(StatType.SPD);
                                double dex = playerFinalStatComponent.getFinalStat(StatType.DEX);
                                double damage= 2+ (atk* 0.25)+(dex * 0.15);
                                double cooltime = Math.max(0.75, (100*(2-spd))/20);
                                Text tooltip= Text.literal("")
                                                .append(Text.literal(String.format("%.2f",damage)).formatted(Formatting.LIGHT_PURPLE))
                                                .append(Text.literal(" = (2 + ⚔ 25% + dex 15%)  피해"));
                                lines.add(tooltip);
                                lines.add(Text.of(""));
                                lines.add(Text.literal(String.format("재사용 대기시간: %.2f초",cooltime)).formatted( Formatting.AQUA));
                                lines.add(Text.literal("                                                  ").formatted(Formatting.UNDERLINE));
                                lines.add(Text.of(""));
                                lines.add(Text.literal(String.format("재사용 대기시간은 0.75초까지만 감소 할 수 있습니다.",cooltime)).formatted( Formatting.DARK_GRAY,Formatting.ITALIC));
                            }
                        }
                    });
        }
    }

