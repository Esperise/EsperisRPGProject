package com.altale.esperis.client.healthHUD;
// com.altale.esperis.client.LookingEntityHealthHUD.java


import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;

public class LookingEntityHealthHUD {
    public static void register() {
        HudRenderCallback.EVENT.register((DrawContext ctx, float tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.crosshairTarget instanceof EntityHitResult entityHit
                    && entityHit.getEntity() instanceof LivingEntity target) {

                float curHp = target.getHealth();
                float maxHp = target.getMaxHealth();
                String display = String.format("체력: %.0f / %.0f", curHp, maxHp);

                MatrixStack matrices = ctx.getMatrices();
                ctx.drawText(client.textRenderer, Text.literal(display), 10, 30, 0xFF4444, true);
            }
        });
    }
}
