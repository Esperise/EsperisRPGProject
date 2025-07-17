package com.altale.esperis.client.HUD;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Long.max;
import static java.lang.Math.ceil;

public class HealthBarOverlay {
    private static final Map<LivingEntity, Float> beforeHpMap = new HashMap<>();
    private static final Map<LivingEntity, Float> hpDiffMap = new HashMap<>();
    private static final Map<LivingEntity, Long> hpDiffTimeMap = new HashMap<>();
    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player != null) {
                float cur = client.player.getHealth();
                float max = client.player.getMaxHealth();
                float absorption = client.player.getAbsorptionAmount();
                float healthWithAbsorption = max+absorption;
                float curPlusAbsorption = cur+absorption;
                float hpDiff =0;
                long hpDiffCurrentTime= client.player.getWorld().getTime();
                if(hpDiffMap.containsKey(client.player)) {
                    hpDiff = hpDiffMap.get(client.player);
                }
                float hpDiffTemp=0;
                float beforeHp=0;
                if(beforeHpMap.containsKey(client.player)) {
                    beforeHp= beforeHpMap.get(client.player);
                    hpDiffTemp=hpDiff;
                    if(curPlusAbsorption < beforeHp) {
                        hpDiff= hpDiff+ (beforeHp- curPlusAbsorption);
                    }
                    else if(curPlusAbsorption > beforeHp) {
                        hpDiff=0;
                    }
                }
                beforeHpMap.put(client.player, curPlusAbsorption);
                hpDiffMap.put(client.player, hpDiff);
//                System.out.println("hpDiffMap.get(player): "+hpDiffMap.get(client.player));
//                System.out.println("hpDiffTemp: "+hpDiffTemp);
                hpDiffTimeMap.putIfAbsent(client.player, hpDiffCurrentTime+30);//보는 즉시 그 대상이 Map에 없으면 1초 생성 계속셈
                long hpTime= hpDiffTimeMap.get(client.player);//hpTime: hp 변동 초기화 하는 시간
                if(beforeHp != curPlusAbsorption) {//보호막에 피해시에도  추가 지속
                    hpDiffTimeMap.put(client.player, max(hpTime+3,hpDiffCurrentTime+10));
                }
                if(hpDiffTimeMap.get(client.player)-hpDiffCurrentTime <=0 ){
//                    System.out.println("time <=0 -> Map clear");
                    beforeHpMap.clear();
                    hpDiffMap.clear();
                    hpDiffTimeMap.clear();
                }
                // 바 길이 설정
                int barWidth = 81;
                int barHeight = 12;
                int filledWidth = (int) ceil((cur / healthWithAbsorption) * barWidth);
                int absorptionBar = (int)((absorption / healthWithAbsorption) * barWidth);
                int hpDiffBar = (int) ceil((hpDiff/healthWithAbsorption)*barWidth);
                if(hpDiff>0){
                    filledWidth = (int) ceil((cur / (healthWithAbsorption+hpDiff)) * barWidth);
                    absorptionBar = (int)((absorption / ((healthWithAbsorption)+hpDiff)) * barWidth);
                    hpDiffBar = (int) ceil((hpDiff/(healthWithAbsorption+hpDiff))*barWidth);
                }

                String healthText = "";float textX=0;float textY=0;
                int x = client.getWindow().getScaledWidth()/2 -barWidth -9;
                int y = client.getWindow().getScaledHeight() - 36;
                if(absorption > 0) {
                    healthText = String.format("%.0f(+%.0f)/%.0f",cur,absorption,max);
                                textX = x+2;
                                textY = y+2;
                }
                else if(absorption == 0) {
                    healthText = String.format("%.0f / %.0f",cur,max);
                    textX = x+20;
                    textY = y+2;
                }
                // 위치 (왼쪽 아래)

                // 배경
                drawContext.fillGradient(x-1, y-1, x + barWidth+2, y + barHeight+1,0xFFBBBBBB, 0xFF444444);

                // 체력 바
                MatrixStack matrices = drawContext.getMatrices();

                drawContext.fillGradient( x+1 , y , x + barWidth , y + barHeight ,0xFF555555, 0xFF000000);//빈 체력

                if(hpDiff>0){
                    drawContext.fill(x + filledWidth+absorptionBar,y, x+filledWidth+absorptionBar+hpDiffBar, y+barHeight, 0xFF444444);
                }

                drawContext.fillGradient( x, y, x + filledWidth+1, y +barHeight, 0xFFFF4444, 0xFFAA0000); //빨간 체력
                drawContext.fillGradient( x+filledWidth, y, x + filledWidth+absorptionBar, y +barHeight, 0xFFFFFFFF,0xFFAAAAAA);//하얀 체력
                // 3. 텍스트 렌더링 (흰색 + 검정 outline)
            TextRenderer renderer = client.textRenderer;
            OrderedText text = Text.literal(healthText).asOrderedText();//



            renderer.drawWithOutline(
                text,
                textX, textY,
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
