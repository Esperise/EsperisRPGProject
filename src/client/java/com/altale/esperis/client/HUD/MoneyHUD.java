package com.altale.esperis.client.HUD;

import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class MoneyHUD {
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (!(screen instanceof InventoryScreen inv)) return;

            // vanilla GUI 크기
            final int guiWidth  = 176;
            final int guiHeight = 166;
            // 화면 크기에서 중앙 배치 좌표 계산
            int x = (client.getWindow().getScaledWidth()  - guiWidth)  / 2;
            int y = (client.getWindow().getScaledHeight() - guiHeight) / 2;

            // 렌더 콜백 등록
            ScreenEvents.afterRender(screen).register((scr, ctx, mouseX, mouseY, tickDelta) -> {
                // 레시피 북 오픈 여부 감지
                if (inv.getRecipeBookWidget().isOpen()) return;
                // 투명 검정 배경 패널
                MatrixStack matrices = ctx.getMatrices();
                TextRenderer renderer = client.textRenderer;
                if (client.player != null){
                    PlayerMoneyComponent moneyComponent =PlayerMoneyComponent.KEY.get(client.player);
                    int money= moneyComponent.getBalance ();
                    int moneyX= x+103;
                    int moneyY =y+171;
                    ctx.fill(moneyX-7, moneyY-5, moneyX +71, moneyY+12, 0xAA000000);
                    ctx.fill(moneyX-6, moneyY-4, moneyX +70, moneyY+11, 0xDD777777);
                    ctx.fill(moneyX-5, moneyY-3, moneyX +69, moneyY+10, 0xFFFFFFFF);//하단 우측 흰
                    ctx.fill(moneyX-5, moneyY-3, moneyX +68, moneyY+9, 0xFFCCCCCC);//상단 좌측 검
//                    ctx.fill(moneyX-5, moneyY-3, moneyX +69, moneyY+10, 0xFFCCCCCC);//상단 좌측 검
                    String str1= String.format("%,7d esp", money);
                    OrderedText text1 = Text.literal(str1).asOrderedText();
                    renderer.drawWithOutline(
                            text1,
                            moneyX, moneyY,
                            0xFFFFFF, // 글자색
                            0x000000, // 테두리색
                            matrices.peek().getPositionMatrix(),
                            ctx.getVertexConsumers(),
                            15728880
                    );
                }
            });
        });
    }
}
