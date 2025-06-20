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
                float absorption = client.player.getAbsorptionAmount();
                float healthWithAbsorption = max+absorption;

                // 바 길이 설정
                int barWidth = 80;
                int barHeight = 11;
                int filledWidth = (int)((cur / healthWithAbsorption) * barWidth);
                int absorptionBar = (int)((absorption / healthWithAbsorption) * barWidth);

                String healthText = "";float textX=0;float textY=0;
                int x = client.getWindow().getScaledWidth()/2 -barWidth -8;
                int y = client.getWindow().getScaledHeight() - 41;
                if(absorption > 0) {
                    healthText = String.format("%.0f (+%.0f) / %.0f",cur,absorption,max);
                                textX = x+8;
                                textY = y+2;
                }
                else if(absorption == 0) {
                    healthText = String.format("%.0f / %.0f",cur,max);
                    textX = x+18;
                    textY = y+2;
                }
                // 위치 (왼쪽 아래)


                // 배경
                drawContext.fill(x-1, y-1, x + barWidth+1, y + barHeight+1, 0x55FFFFFF);

                // 체력 바
                MatrixStack matrices = drawContext.getMatrices();

                drawContext.fill( x , y , x + barWidth , y + barHeight , 0xFF000000);//빈 체력

            drawContext.fill( x, y, x + filledWidth+1, y +barHeight, 0xFFFF3333); //빨간 체력
            drawContext.fill( x+filledWidth+1, y, x+1 + filledWidth+absorptionBar, y +barHeight, 0xFFFFFFFF);//하얀 체력
            // 3. 텍스트 렌더링 (흰색 + 검정 outline)
            TextRenderer renderer = client.textRenderer;
            OrderedText text = Text.literal(healthText).asOrderedText();



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
