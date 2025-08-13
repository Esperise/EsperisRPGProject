package com.altale.esperis.client.item.tooltip;

import com.altale.esperis.client.item.toolTipManager.TooltipManager;
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
import net.minecraft.item.*;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.Objects;

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
                                double as = playerFinalStatComponent.getFinalStat(StatType.ATTACK_SPEED);
                                double dex = playerFinalStatComponent.getFinalStat(StatType.DEX);
                                double damage= 3+ (atk* 0.45)+(dex * 0.1);
                                SpecialBowItem spb = (SpecialBowItem) stack.getItem();//게임 시작될때 아이템 레지스트리가 이미 “잠겨(lock)” 있는 시점이어서, 내부적으로 Item 생성자에서 레지스트리에 항목을 등록하려다가 예외가 발생하는 겁니다.
                                double OriginalSpecialBowAs= spb.getSpecialBowAttackSpeed();
                                double finalAs = (1/ Math.max(0.01, 1/(OriginalSpecialBowAs*as)));

                                Text tooltip= Text.literal("");
                                if(player.getInventory().contains(Items.ARROW.getDefaultStack())){
                                    damage+=4;
                                    tooltip= tooltip.copy().append(Text.literal(String.format("%.2f",damage)+" 피해").formatted(Formatting.LIGHT_PURPLE));
                                    tooltip= tooltip.copy().append((Text.literal(" = (3(+4) + ⚔ 30% + dex 10%)  ")));
                                }else{
                                    if(stack.getNbt() != null && stack.getNbt().contains("UsageCount")){
                                        int count = stack.getNbt().getInt("UsageCount");
                                        if(count ==3){
                                            damage += 15;

                                        }
                                    }

                                        tooltip= tooltip.copy().append(Text.literal(String.format("%.2f",damage)+" 피해").formatted(Formatting.LIGHT_PURPLE));
                                        tooltip= tooltip.copy().append((Text.literal(" = (3 + ⚔ 30% + dex 10%) ")));


                                }
                                lines.add(tooltip);
//                                lines.add(Text.of(""));
                                lines.add(Text.literal(String.format("공격 속도: %.2f / s                                         ",finalAs)).formatted( Formatting.UNDERLINE));
                                lines.add(Text.of(""));
                                lines.add(Text.literal("거리에 비례하여 피해량이 최대 50% 까지 감소합니다.").formatted( Formatting.DARK_GRAY,Formatting.ITALIC));
//                                lines.add(Text.literal("테스트 Test 123 @ ")
//                                        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xEEE3FD))));
                            }
                        }
                    });
        }

    }

