package com.altale.esperis.client.HUD;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;

public class InventoryScreenHUD {
    public static void register() {
            // 화면이 초기화될 때마다(=인벤토리 화면이 열릴 때마다)
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof InventoryScreen)) return;

            ScreenEvents.afterRender(screen).register(
                    (scr, drawContext, mouseX, mouseY, tickDelta) -> {
                        InventoryScreen inv = (InventoryScreen) scr;
                        // 화면 가운데 기준으로 좌표 계산
                        final int guiWidth  = 176;
                        final int guiHeight = 166;
                        int x = (client.getWindow().getScaledWidth()  - guiWidth)  / 2;
                        int y = (client.getWindow().getScaledHeight() - guiHeight) / 2;

                        // 레시피 북 옆에 그리기
                        drawContext.drawText(
                                client.textRenderer,
                                Text.literal("내가 추가한 글자"),
                                x + guiWidth + 6,
                                y + 6,
                                0xFFFFFF,
                                false
                        );
                    }
            );
        });
    }
}
