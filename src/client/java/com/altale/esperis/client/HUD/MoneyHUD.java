package com.altale.esperis.client.HUD;

import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class MoneyHUD {
    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null){
                PlayerMoneyComponent moneyComponent =PlayerMoneyComponent.KEY.get(client.player);
                int money= moneyComponent.getBalance ();
                int x= client.getWindow().getScaledWidth()-74;
                int y =6;
                drawContext.fill(x-6, y-4, x +70, y+11, 0x88000000);
                drawContext.fill(x-4, y-2, x +68, y+9, 0xFFFFFFFF);
                MatrixStack matrices = drawContext.getMatrices();
                TextRenderer renderer = client.textRenderer;
                String str= String.format("%,7d esp", money);
                OrderedText text = Text.literal(str).asOrderedText();
                renderer.drawWithOutline(
                        text,
                        x, y,
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
