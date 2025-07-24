package com.altale.esperis.client.item.tooltip;

import com.altale.esperis.items.ModItems;
import com.altale.esperis.items.itemFunction.SpecialBowItem;
import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
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
                                SpecialBowItem spb = (SpecialBowItem) stack.getItem();//게임 시작될때 아이템 레지스트리가 이미 “잠겨(lock)” 있는 시점이어서, 내부적으로 Item 생성자에서 레지스트리에 항목을 등록하려다가 예외가 발생하는 겁니다.
                                int OriginalSpecialBowCoolTime= spb.getSpecialBowCoolTime();
                                double coolTimeReduce = Math.min(80, (spd-1)*100);
                                double cooltime = Math.max(OriginalSpecialBowCoolTime/100.0, (OriginalSpecialBowCoolTime*(2-spd))/20);
                                Text tooltip= Text.literal("");
                                if(player.getInventory().contains(Items.ARROW.getDefaultStack())){
                                    damage+=4;
                                    tooltip= tooltip.copy().append(Text.literal(String.format("%.2f",damage)+" 피해").formatted(Formatting.LIGHT_PURPLE));
                                    tooltip= tooltip.copy().append((Text.literal(" = (2(+4) + ⚔ 25% + dex 15%)  ")));
                                }else{
                                    tooltip= tooltip.copy().append(Text.literal(String.format("%.2f",damage)+" 피해").formatted(Formatting.LIGHT_PURPLE));
                                    tooltip= tooltip.copy().append((Text.literal(" = (2 + ⚔ 25% + dex 15%) ")));
                                }
                                lines.add(tooltip);
//                                lines.add(Text.of(""));
                                lines.add(Text.literal(String.format("재사용 대기시간: %.2f초                                          ",cooltime)).formatted( Formatting.UNDERLINE));
//                                lines.add(Text.literal("                                                                ").formatted(Formatting.UNDERLINE));
                                lines.add(Text.of(""));
                                lines.add(Text.literal(String.format("재사용 대기시간은 최대 80%%까지만 감소 할 수 있습니다. 현재: %.2f%%",coolTimeReduce)).formatted( Formatting.DARK_GRAY,Formatting.ITALIC));
                                lines.add(Text.literal("거리에 비례하여 피해량이 최대 50% 까지 감소합니다.").formatted( Formatting.DARK_GRAY,Formatting.ITALIC));
//                                lines.add(Text.literal("테스트 Test 123 @ ")
//                                        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xEEE3FD))));
                            }
                        }
                    });
        }
    }

