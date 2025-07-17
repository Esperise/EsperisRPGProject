package com.altale.esperis.client.screen;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.Objects;

import static com.google.common.primitives.UnsignedInts.max;
import static com.google.common.primitives.UnsignedInts.min;

public class InventoryStatTest {
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (!(screen instanceof InventoryScreen inv)) return;

            // vanilla GUI 크기
            final int guiW = 176, guiH = 166;
            int x = (client.getWindow().getScaledWidth()   / 100);
            int y = (client.getWindow().getScaledHeight() / 6);
            int x2= (client.getWindow().getScaledWidth() *32   / 100);

            // 렌더 콜백 등록
            ScreenEvents.afterRender(screen).register((scr, ctx, mouseX, mouseY, tickDelta) -> {
                // 레시피 북 오픈 여부 감지
                if (inv.getRecipeBookWidget().isOpen()) return;
                PlayerEntity player = client.player;
                PlayerLevelComponent lvComp= PlayerLevelComponent.KEY.get(Objects.requireNonNull(player));
                int lv= lvComp.getLevel();
                int currExp= lvComp.getCurrentExp();
                int maxExp= lvComp.getMaxExp();
                // 투명 검정 배경 패널
                ctx.fill(x, y, x2, y + guiH, 0x77AAAAAA);
                MatrixStack matrices = ctx.getMatrices();
                TextRenderer renderer = client.textRenderer;
                String str= String.format("1234567890123456789012345");//최소 20 최대 24
                OrderedText text = Text.literal(str).asOrderedText();
                renderer.drawWithOutline(
                        text,
                        x+1, y+1,
                        0xFFFFFF, // 글자색
                        0x000000, // 테두리색
                        matrices.peek().getPositionMatrix(),
                        ctx.getVertexConsumers(),
                        15728880
                );
                renderer.drawWithOutline(
                        Text.literal(String.format("%d / %d",currExp,maxExp)).asOrderedText(),
                        x+1, y+10,0xFFFFFF, // 글자색
                        0x000000, // 테두리색
                        matrices.peek().getPositionMatrix(),
                        ctx.getVertexConsumers(),
                        15728880
                );
//                renderer.drawWithOutline(
//                        Text.literal(String.format("%d / %d",currExp,maxExp)).asOrderedText(),
//                        x+1, y+10,0xFFFFFF, // 글자색
//                        0x000000, // 테두리색
//                        matrices.peek().getPositionMatrix(),
//                        ctx.getVertexConsumers(),
//                        15728880
//                );
            });
        });
    }
}
