package com.altale.esperis.client.HUD;

import com.altale.esperis.client.cache.CoolTimeTextCache;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

import java.util.List;

public class CoolTimeHUD {
    public static void register() {
        HudRenderCallback.EVENT.register((DrawContext ctx, float tickDelta) -> {
            MatrixStack matrices = ctx.getMatrices();
            // 1) 클라이언트 가져오기
            MinecraftClient client = MinecraftClient.getInstance();
            // 또는 Fabric API 버전에서 제공한다면
            // MinecraftClient client = ctx.getClient();
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            var consumers = ctx.getVertexConsumers();
            int light = 15728880;
            if (client.player != null) {
                List<String> coolTimeList= CoolTimeTextCache.getCoolTimeTextList();
                String coolTime;
                for (int i = 0; i < coolTimeList.size(); i++) {
                    coolTime = coolTimeList.get(i);
                    OrderedText text = Text.literal(coolTime).asOrderedText();
                    client.textRenderer.drawWithOutline(
                            text,
                            13f, (float) (client.getWindow().getScaledHeight() - (9.0*(i+1))),
                            0xFFFFFF,
                            0x000000,
                            matrix,
                            consumers,
                            light
                    );
                }

            }
        });
    }
}
