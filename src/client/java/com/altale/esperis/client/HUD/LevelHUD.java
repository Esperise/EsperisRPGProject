package com.altale.esperis.client.HUD;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.Objects;

public class LevelHUD {
    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            PlayerLevelComponent levelComponent= PlayerLevelComponent.KEY.get(Objects.requireNonNull(client.player));
            int level = levelComponent.getLevel();
            int currentExp = levelComponent.getCurrentExp();
            int maxExp = levelComponent.getMaxExp();
            int expBarWidth= 80;
            double expPercentage = Math.round((double)currentExp * 10000 / (double)maxExp)/100.0;
            int currentExpBar= (int) (((double) currentExp / (double)maxExp) * expBarWidth);
            int levelBarX = client.getWindow().getScaledWidth()/2 -7;
            int levelBarY = client.getWindow().getScaledHeight() - 36;
            int ExpBarX =levelBarX +18;
            int levelTextX = levelBarX+3;
            if(level <10){
                levelTextX +=3;
            }
            //level
            drawContext.fillGradient(levelBarX,levelBarY-1,ExpBarX-1,levelBarY+13,0xFFBBBBBB, 0xFF444444);//level 테두리
            drawContext.fillGradient(levelBarX,levelBarY,ExpBarX-1,levelBarY+12,0xFF666666, 0xFF000000);//level
            //expBar 테두리
            drawContext.fillGradient(ExpBarX-1,levelBarY-1,ExpBarX+expBarWidth+1,levelBarY+13,0xFFBBBBBB, 0xFF444444);
            //expBar
            drawContext.fillGradient(ExpBarX,levelBarY,ExpBarX+expBarWidth,levelBarY+12,0xFF555555, 0xFF000000);
            //경험치 채워진 양
            drawContext.fillGradient(ExpBarX,levelBarY,ExpBarX+currentExpBar,levelBarY+12,0xFF33FF33, 0xFF115511);

            String levelText = String.format("%d", level );
            String levelText2 = String.format("%.2f", expPercentage );
            TextRenderer renderer = client.textRenderer;
            MatrixStack matrices = drawContext.getMatrices();
            OrderedText text = Text.literal(levelText).asOrderedText();
            OrderedText text2 = Text.literal(levelText2+"%").asOrderedText();


            renderer.drawWithOutline(
                    text2, ExpBarX+23, levelBarY+2,
                    0xFFFFFF, // 글자색
                    0x000000, // 테두리색
                    matrices.peek().getPositionMatrix(),
                    drawContext.getVertexConsumers(),
                    15728880
            );
            renderer.drawWithOutline(
                text,
                levelTextX, levelBarY+2,
                0xFFFFFF, // 글자색
                0x000000, // 테두리색
                matrices.peek().getPositionMatrix(),
                drawContext.getVertexConsumers(),
                15728880
            );

        });
    }
}
