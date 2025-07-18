package com.altale.esperis.client.screen;

import com.altale.esperis.client.screen.Button.SpButtonFactory;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerPointStatComponent;
import com.altale.esperis.player_data.stat_data.StatPointType;
import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class InventoryStatScreen extends Screen {
    private static final Map<StatType, Integer> spending = new EnumMap<>(StatType.class);
    MinecraftClient client= MinecraftClient.getInstance();
    PlayerPointStatComponent pointStatComponent = PlayerPointStatComponent.KEY.get(Objects.requireNonNull(client.player));
    private int unusedSP=pointStatComponent.getSP(StatPointType.UnusedSP);
    private int willBeUsedSP= 0;
    private final int guiWidth = 300, guiHeight = 200;
    private ButtonWidget closeButton;

    public InventoryStatScreen() {
        super(Text.literal("My Custom Screen"));  // 창 상단 타이틀
    }

    @Override
    protected void init() {
        // 화면 중앙 기준으로 버튼 배치
        int x = (this.width  - guiWidth)  / 2;
        int y = (this.height - guiHeight) / 2;

        // 닫기 버튼
        this.closeButton = ButtonWidget.builder(Text.literal("닫기"), btn -> {
                    // 버튼 클릭 시 이전 화면으로 돌아가기
                    MinecraftClient.getInstance().setScreen(null);
                })
                .dimensions(x + guiWidth - 50, y + guiHeight - 20, 40, 20)
                .build();
        this.addDrawableChild(closeButton);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // 반투명 배경 (기본 GUI 창 스타일)
        this.renderBackground(ctx);

        int x = (this.width  - guiWidth)  / 2;
        int y = (this.height - guiHeight) / 2;
        ctx.fill(x,y,x + guiWidth, y + guiHeight, 0x99000000);      // 검은 반투명
        ctx.fillGradient(x + 1, y + 1,x + guiWidth - 1, y + guiHeight - 1, 0xAA777777, 0xAA444444);// 회색 안쪽
        int btnX= x+30;
        int btnY= y+30;
        int buttonYDelta = 20;
        for(StatType statType : StatType.getNormalStatType()) {//STR->DEX->LUK->DUR 순서
                ButtonWidget spDecrease= SpButtonFactory.createSpButton(
                        statType,btnX,btnY,20,20,
                        ()->spending.getOrDefault(statType,0),
                        ()->unusedSP,
                        (type,newVal)->{//type은 위의 statType과 값이 같음
                            int oldVal = spending.getOrDefault(type,0);
                            spending.put(type,newVal);
                            unusedSP -= (newVal-oldVal);
                            this.init();
                        }, "◀"
                );
                ButtonWidget Increase= SpButtonFactory.createSpButton(
                        statType,btnX+46,btnY,20,20,
                        ()->spending.getOrDefault(statType,0),
                        ()->unusedSP,
                        (type,newVal)->{//type은 위의 statType과 값이 같음
                            int oldVal = spending.getOrDefault(type,0);
                            spending.put(type,newVal);
                            unusedSP -= (newVal-oldVal);

                            this.init();
                        }, "▶"
                );
                btnY += buttonYDelta;

        }




        int unusedSP= pointStatComponent.getSP(StatPointType.UnusedSP);
        TextRenderer renderer = Objects.requireNonNull(client).textRenderer;
            MatrixStack matrices = ctx.getMatrices();
        String unusedSpText= String.format("미사용 StatPoint: %d", unusedSP);
        OrderedText text = Text.literal(unusedSpText).asOrderedText();
            textRenderer.drawWithOutline(
                    text, x+5, y+5,
                    0xFFFFFF, // 글자색
                    0x000000, // 테두리색
                    matrices.peek().getPositionMatrix(),
                    ctx.getVertexConsumers(),
                    15728880
            );
        // 버튼 등 자식 위젯 렌더
        super.render(ctx, mouseX, mouseY, delta);
    }
}
