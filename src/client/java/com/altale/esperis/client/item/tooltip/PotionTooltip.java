package com.altale.esperis.client.item.tooltip;

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
public class PotionTooltip {
    public static void registerTooltip() {
        ItemTooltipCallback.EVENT.register(
                (stack, ctx, lines) -> {
                    if (stack.getItem() instanceof HealingPotion ) {
                        ClientPlayerEntity player = MinecraftClient.getInstance().player;
                        if (player != null) {

                            float maxHp = player.getMaxHealth();
                            PlayerLevelComponent levelComponent = PlayerLevelComponent.KEY.get(player);
                            int level = levelComponent.getLevel();
                            Item item = stack.getItem();
                            if(item instanceof HealingPotion healingPotion) {
                                float baseHeal = healingPotion.getBaseHeal();
                                float hpCoeffi = healingPotion.getHpCoeff();
                                int duration = healingPotion.getDuration();
                                int cooltime= healingPotion.getCooltime();
                                String potionName = healingPotion.getPotionName();
                                Text tooltip;
                                if(duration <= 2 ){
                                    tooltip= Text.literal("")
                                            .append(Text.literal(String.format("%d = ( %.0f + ♥ %.2f%% )",Math.round(baseHeal + maxHp * hpCoeffi ),baseHeal,hpCoeffi*100 )).formatted(Formatting.GREEN))
                                            .append("를 즉시 회복");
                                }else{
                                    tooltip= Text.literal("")
                                            .append(Text.literal(String.format("%d = ( %.0f + ♥ %.2f%% )",Math.round(baseHeal + maxHp * hpCoeffi ),baseHeal,hpCoeffi*100 )).formatted(Formatting.GREEN))
                                            .append(String.format("를 %.1f초에 걸쳐서 회복", Math.round(100* duration /20f) /100f) );
                                }
                                lines.add(tooltip);
                                lines.add(Text.literal(String.format("사용 쿨타임 : %.1f초              ", Math.round(100* cooltime /20f) /100f)).formatted(Formatting.UNDERLINE));
                                if(healingPotion.isInfinite()){
                                    lines.add(Text.literal(""));
                                    tooltip = Text.literal("무한: 사용시 아이템을 소모하지 않음").formatted(Formatting.DARK_GRAY);
                                    lines.add(tooltip);
                                }

                            }

                        }
                    }
                });
    }
}
