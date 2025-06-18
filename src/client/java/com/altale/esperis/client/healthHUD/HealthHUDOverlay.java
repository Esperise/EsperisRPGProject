package com.altale.esperis.client.healthHUD;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

public class HealthHUDOverlay {
    public static void register() {
        HudRenderCallback.EVENT.register((DrawContext ctx, float tickDelta) -> {
            MatrixStack matrices = ctx.getMatrices();
            // 1) 클라이언트 가져오기
            MinecraftClient client = MinecraftClient.getInstance();
            // 또는 Fabric API 버전에서 제공한다면
            // MinecraftClient client = ctx.getClient();

            if (client.player != null) {
                float cur = client.player.getHealth();
                float max = client.player.getMaxHealth();
                String disp = String.format("%.0f/%.0f", cur, max);

                OrderedText text = Text.literal(disp).asOrderedText();
                Matrix4f matrix = matrices.peek().getPositionMatrix();
                var consumers = ctx.getVertexConsumers();
                int light = 15728880;

                client.textRenderer.drawWithOutline(
                    text,
                    10f, 10f,
                    0xFFFFFF,
                    0x000000,
                    matrix,
                    consumers,
                    light
                );
            }
        });
    }
}
