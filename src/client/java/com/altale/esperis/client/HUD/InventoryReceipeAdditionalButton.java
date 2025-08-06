package com.altale.esperis.client.HUD;

import com.altale.esperis.client.screen.EquipmentEnhancementScreen;
import com.altale.esperis.client.screen.InventoryStatScreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
public class InventoryReceipeAdditionalButton {
        public static void register() {
            ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                if (!(screen instanceof InventoryScreen inv)) return;

                // GUI 크기 상수 (vanilla 인벤토리)
                final int guiWidth  = 176;
                final int guiHeight = 166;
                // 화면 크기에서 중앙 배치 좌표 계산
                int x = (client.getWindow().getScaledWidth()  - guiWidth)  / 2;
                int y = (client.getWindow().getScaledHeight() - guiHeight) / 2;
                //"▶"
                // 버튼 생성 (builder 사용)
                ButtonWidget recipeButton = ButtonWidget
                        .builder(Text.literal("SP 사용"), btn -> {
                            client.setScreen(new InventoryStatScreen());
                        })
                        .dimensions(x + guiWidth - 45, y + 59, 40, 20)
                        .build();
                recipeButton.visible = true;
                ButtonWidget goToEquipmentEnhancementScreenButton= ButtonWidget.builder(Text.literal("장비 강화"), btn ->{
                    client.setScreen(new EquipmentEnhancementScreen());//FIXME 누르면 장비 강화 스크린 -> 버튼 2개( a:장비 추가 스탯 재설정, b:장비 스크롤 부여)
                }).dimensions(x, y + 166, 63, 20).build();
                goToEquipmentEnhancementScreenButton.visible = true;

                // protected addDrawableChild 대신 Screens.getButtons 로 추가
                Screens.getButtons(screen).add(recipeButton);
                Screens.getButtons(screen).add(goToEquipmentEnhancementScreenButton);

                // 렌더 후마다 레시피 창 열림 상태 체크
                ScreenEvents.afterRender(screen).register((scr, ctx, mouseX, mouseY, tickDelta) -> {
                    boolean open = inv.getRecipeBookWidget().isOpen();

                    if (open) {
                        recipeButton.visible = false;
                        // 필요시 텍스트도 같이 그릴 수 있음
//                        ctx.drawText(
//                                MinecraftClient.getInstance().textRenderer,
//                                Text.literal("button"),
//                                x + guiWidth - 16,
//                                y + 30,
//                                0xFFFFFF,
//                                false
//                        );
                    }
                });
            });
        }
}
