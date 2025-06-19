package com.altale.esperis.client.healthHUD;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class HealthBarOverlay {
    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player != null) {
                float cur = client.player.getHealth();
                float max = client.player.getMaxHealth();

                // 바 길이 설정
                int barWidth = 80;
                int barHeight = 10;
                int filledWidth = (int)((cur / max) * barWidth);

                // 위치 (왼쪽 아래)
                int x = 10;
                int y = client.getWindow().getScaledHeight() - 20;

                // 배경
                drawContext.fill(x, y, x + barWidth, y + barHeight, 0xFF333333);

                // 체력 바
                drawContext.fill(x, y, x + filledWidth, y + barHeight, 0xFFFF4444);

                // 숫자 텍스트
                String text = String.format("%.0f / %.0f", cur, max);
                drawContext.drawText(client.textRenderer, text, x + barWidth + 5, y, 0xFFFFFF, true);
            }
        });
    }
}
