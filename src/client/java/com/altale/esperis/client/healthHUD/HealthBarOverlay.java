package com.altale.esperis.client.healthHUD;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;

public class HealthBarOverlay {
    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player != null) {
                float cur = client.player.getHealth();
                float max = client.player.getMaxHealth();
                float aborption = client.player.getAbsorptionAmount();
                float healthWithAborption = max+aborption;

                // 바 길이 설정
                int barWidth = 80;
                int barHeight = 7;
                int filledWidth = (int)((cur / max) * barWidth);
                String healthText = String.format("%.0f (+%.0f) / %.0f",cur,aborption,max);
                // 위치 (왼쪽 아래)
                int x = 125;
                int y = client.getWindow().getScaledHeight() - 45;

                // 배경
                drawContext.fill(x, y, x + barWidth, y + barHeight, 0xFF333333);

                // 체력 바
                MatrixStack matrices = drawContext.getMatrices();

                drawContext.fill( x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);

            drawContext.fill( x, y, x + filledWidth, y +barHeight, 0xFFFF5555);

            // 3. 텍스트 렌더링 (흰색 + 검정 outline)
            TextRenderer renderer = client.textRenderer;
            OrderedText text = Text.literal(healthText).asOrderedText();

            float textX = x+5;
            float textY = y+2;

            renderer.drawWithOutline(
                text,
                textX, textY,
                0xFFFFFF, // 글자색
                0x000000, // 테두리색
                matrices.peek().getPositionMatrix(),
                drawContext.getVertexConsumers(),
                15728880
            );
            }
        });
    }
}
