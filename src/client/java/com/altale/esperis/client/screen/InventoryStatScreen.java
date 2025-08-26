package com.altale.esperis.client.screen;

import com.altale.esperis.client.packet.StatAddRequestSender;
import com.altale.esperis.client.packet.StatUpdateRequestSender;
import com.altale.esperis.client.screen.Button.SpButtonFactory;
import com.altale.esperis.player_data.stat_data.StatComponents.BaseAbilityComponentImp;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class InventoryStatScreen extends Screen {
    private static final Map<StatType, Integer> spending = new EnumMap<>(StatType.class);
    private static final String[] order = {
            "공격력","방어력","체력","이동속도 (%)","공격 속도(%)","치명타 확률(%)","치명타 데미지(%)"
    };
    MinecraftClient client= MinecraftClient.getInstance();
    PlayerPointStatComponent pointStatComponent = PlayerPointStatComponent.KEY.get(Objects.requireNonNull(client.player));
    PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(Objects.requireNonNull(client.player));
    private int unusedSP=pointStatComponent.getSP(StatPointType.UnusedSP);
    private int willBeUsedSP= 0;
    private final int guiWidth = 300, guiHeight = 200;
    private ButtonWidget closeButton;
    private ButtonWidget confirmButton;

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
                    spending.clear();
                    willBeUsedSP= 0;
                    MinecraftClient.getInstance().setScreen(null);
                })
                .dimensions(x + guiWidth - 50, y + guiHeight - 20, 40, 20)
                .build();
        this.confirmButton =ButtonWidget.builder(Text.literal("결정"), btn -> {
                    // 결정 버튼 클릭 시 스탯 추가 요청을 서버로 보내고 값을 0으로 만든 후 이전 화면으로 돌아가기
                    for(StatType statType: StatType.getNormalStatType()){
                        int value = spending.getOrDefault(statType, 0);
                        Objects.requireNonNull(client.player).sendMessage(Text.literal(String.format("%s: %d",statType.name(), value)), false);
                        if (value <=0){
                            continue;
                        }
                        StatAddRequestSender.sendAddStatRequest(statType, value);
                    }
                    spending.clear();
                    willBeUsedSP= 0;
                    MinecraftClient.getInstance().setScreen(null);
                })
                .dimensions(x + guiWidth - 110, y + guiHeight - 20, 40, 20)
                .build();
        this.addDrawableChild(closeButton);
        this.addDrawableChild(confirmButton);
        int btnX= (this.width  - guiWidth)  / 2+50;
        int btnY= (this.height - guiHeight) / 2+30;
        int buttonYDelta = 30;
        for(StatType statType : StatType.getNormalStatType()) {//STR->DEX->LUK->DUR 순서
                this.addDrawableChild(
                        SpButtonFactory.createSpButton(
                        statType,btnX,btnY,20,20,
                        ()->spending.getOrDefault(statType,0),
                        ()->unusedSP,
                        (type,newVal)->{//type은 위의 statType과 값이 같음
                            int oldVal = spending.getOrDefault(type,0);
                            spending.put(type,newVal);
                            unusedSP -= (newVal-oldVal);
                            willBeUsedSP += (newVal-oldVal);
                            this.init();}, "◀")
                        );
                this.addDrawableChild(
                        SpButtonFactory.createSpButton(
                        statType,btnX+56,btnY,20,20,
                        ()->spending.getOrDefault(statType,0),
                        ()->unusedSP,
                        (type,newVal)->{//type은 위의 statType과 값이 같음
                            int oldVal = spending.getOrDefault(type,0);
                            spending.put(type,newVal);
                            unusedSP -= (newVal-oldVal);
                            willBeUsedSP += (newVal-oldVal);
                            this.init();
                        }, "▶")
                );
                btnY += buttonYDelta;
            }
    }
    @Override
    public void removed(){
        super.removed();
        spending.clear();
        willBeUsedSP= 0;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // 반투명 배경 (기본 GUI 창 스타일)
        this.renderBackground(ctx);

        int x = (this.width  - guiWidth)  / 2;
        int y = (this.height - guiHeight) / 2;
        ctx.fill(x,y,x + guiWidth, y + guiHeight, 0x99000000);      // 검은 반투명
        ctx.fillGradient(x + 1, y + 1,x + guiWidth - 1, y + guiHeight - 1, 0xAA777777, 0xAA444444);// 회색 안쪽





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
            );textRenderer.drawWithOutline(
                    Text.literal(String.format("사용 예정 sp: %d",willBeUsedSP)).asOrderedText(),
                    x+105, y+5,
                    0xFFFFFF, // 글자색
                    0x000000, // 테두리색
                    matrices.peek().getPositionMatrix(),
                    ctx.getVertexConsumers(),
                    15728880
            );
            int textY= y+37;
            int yDelta= 30;
            for(StatType statType : StatType.getNormalStatType()) {
                OrderedText label = Text.literal(statType.name()+" :").asOrderedText();
                textRenderer.drawWithOutline(
                    label, x+9, textY,
                    0xFFFFFF, // 글자색
                    0x000000, // 테두리색
                    matrices.peek().getPositionMatrix(),
                    ctx.getVertexConsumers(),
                    15728880
            );

                textRenderer.drawWithOutline(
                    Text.literal(String.format("+%02d",spending.getOrDefault(statType,0))).asOrderedText(), x+80, textY,
                    0xFFFFFF, // 글자색
                    0x000000, // 테두리색
                    matrices.peek().getPositionMatrix(),
                    ctx.getVertexConsumers(),
                    15728880
            );
                textY+= yDelta;

            }
        Map<String, Double> abilityMap = Map.of(
                "공격력",
                (
                        (BaseAbilityComponentImp.StrAtk * spending.getOrDefault(StatType.STR, 0))
                                + (BaseAbilityComponentImp.DexAtk * spending.getOrDefault(StatType.DEX, 0))
                                + (BaseAbilityComponentImp.LukAtk * spending.getOrDefault(StatType.LUK, 0))
                ),
                "방어력",
                BaseAbilityComponentImp.DurDef * spending.getOrDefault(StatType.DUR, 0),
                "체력",
                (
                        (BaseAbilityComponentImp.StrHp * spending.getOrDefault(StatType.STR, 0))
                                + (BaseAbilityComponentImp.DurHp * spending.getOrDefault(StatType.DUR, 0))
                ),
                "이동속도 (%)",
                (
                        (BaseAbilityComponentImp.DexSpd * spending.getOrDefault(StatType.DEX, 0))
                                + (BaseAbilityComponentImp.LukSpd * spending.getOrDefault(StatType.LUK, 0))
                ) * 100,
                "공격 속도(%)",
                (
                        (BaseAbilityComponentImp.DexAs * spending.getOrDefault(StatType.DEX, 0))
                                + (BaseAbilityComponentImp.LukAs * spending.getOrDefault(StatType.LUK, 0))
                ) * 100,
                "치명타 확률(%)",
                (
                        (BaseAbilityComponentImp.DexCrit * spending.getOrDefault(StatType.DEX, 0))
                                + (BaseAbilityComponentImp.LukCrit * spending.getOrDefault(StatType.LUK, 0))
                ) * 100,
                "치명타 데미지(%)",
                (BaseAbilityComponentImp.LukCrit * spending.getOrDefault(StatType.LUK, 0) * 100)
        );


        int abilityY = y + 20;
        for (String key : order) {
            double value = abilityMap.getOrDefault(key, 0.0);
            if (Math.abs(value) < 1e-9) continue;
            OrderedText abilityText = Text.literal(String.format("%s : +%.2f", key, value)).asOrderedText();
            textRenderer.drawWithOutline(abilityText, x+180, abilityY, 0xFFFFFF, 0x000000,
                    matrices.peek().getPositionMatrix(), ctx.getVertexConsumers(), 15728880);
            abilityY += 14;
        }




        // 버튼 등 자식 위젯 렌더
        super.render(ctx, mouseX, mouseY, delta);
    }
}
