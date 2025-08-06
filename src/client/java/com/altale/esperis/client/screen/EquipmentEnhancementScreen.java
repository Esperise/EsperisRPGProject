package com.altale.esperis.client.screen;

import com.altale.esperis.client.packet.ShowRerollGuiRequestSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class EquipmentEnhancementScreen extends Screen {
    private final int guiWidth = 200 , guiHeight= 150;
    private ButtonWidget goToRerollButton;
    private ButtonWidget goToScrollButton;
    private ButtonWidget goBackButton;
    public EquipmentEnhancementScreen() {
        super(Text.literal("EquipmentEnhancement"));
    }
    @Override
    protected void init() {
        int x= (this.width -guiWidth)/2, y= (this.height -guiHeight)/2;


        this.goBackButton = ButtonWidget.builder(Text.literal("닫기"), btn ->{
            MinecraftClient.getInstance().setScreen(null);
        }).dimensions(x+guiWidth-40, y+guiHeight -20, 40,20).build();


        this.goToRerollButton=ButtonWidget.builder(Text.literal("장비 추가 스탯 재설정"), btn ->{
            //패킷보내서 server에서 띄워야함
            ShowRerollGuiRequestSender.sendShowRerollGuiReqeust();
        }).dimensions(x+50, y+30, 90,20).build();


        this.goToScrollButton=ButtonWidget.builder(Text.literal("스크롤 부여"), btn ->{
            MinecraftClient.getInstance().setScreen(null);//장비 재설정 스크린으로 바꾸기
        }).dimensions(x+50, y+100, 90,20).build();
        this.addDrawableChild(goBackButton);
        this.addDrawableChild(goToRerollButton);
        this.addDrawableChild(goToScrollButton);
        goToRerollButton.visible= true;
        goToScrollButton.visible= true;
        goBackButton.visible= true;
    }
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // 반투명 배경 (기본 GUI 창 스타일)
        this.renderBackground(ctx);


        int x = (this.width - guiWidth) / 2;
        int y = (this.height - guiHeight) / 2;
        ctx.fill(x, y, x + guiWidth, y + guiHeight, 0x99000000);      // 검은 반투명
        ctx.fillGradient(x + 1, y + 1, x + guiWidth - 1, y + guiHeight - 1, 0xAA777777, 0xAA444444);// 회색 안쪽
        super.render(ctx, mouseX, mouseY, delta);
    }
}
