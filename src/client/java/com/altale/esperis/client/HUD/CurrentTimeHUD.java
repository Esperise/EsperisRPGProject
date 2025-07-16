package com.altale.esperis.client.HUD;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CurrentTimeHUD {
    public static void register(){
        HudRenderCallback.EVENT.register(((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            LocalTime time = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String currentTime= LocalTime.now().format(formatter);
            OrderedText text = Text.literal(currentTime).asOrderedText();
            int length= currentTime.length();
            int x= (int) (client.getWindow().getScaledWidth()-(length*6));
            int y= (int) (client.getWindow().getScaledHeight()-11);
            TextRenderer renderer = client.textRenderer;
            MatrixStack matrices = drawContext.getMatrices();
            renderer.drawWithOutline(
                    text, x, y,
                    0xFFFFFF, // 글자색
                    0x000000, // 테두리색
                    matrices.peek().getPositionMatrix(),
                    drawContext.getVertexConsumers(),
                    15728880
            );

        }));
    }

}
