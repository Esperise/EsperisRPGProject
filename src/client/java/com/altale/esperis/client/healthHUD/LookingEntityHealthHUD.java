package com.altale.esperis.client.healthHUD;
// com.altale.esperis.client.LookingEntityHealthHUD.java



import com.altale.esperis.client.cache.AbsorptionCache;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Long.max;


public class LookingEntityHealthHUD {
    private static final Map<LivingEntity, Float> beforeHpMap = new HashMap<>();
    private static final Map<LivingEntity, Float> hpDiffMap = new HashMap<>();
    private static final Map<LivingEntity, Long> hpDiffTimeMap = new HashMap<>();
    public static void register() {
        HudRenderCallback.EVENT.register((DrawContext ctx, float tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;
            if (player == null || client.world == null) return;

            double maxDistance = 17.0;
            Vec3d cameraPos = player.getCameraPosVec(tickDelta);
            Vec3d lookVec = player.getRotationVec(tickDelta);
            Vec3d end = cameraPos.add(lookVec.multiply(maxDistance));

            // 탐색 범위 박스 생성
            Box box = player.getBoundingBox().stretch(lookVec.multiply(maxDistance)).expand(0.5);

            // 후보 엔티티 검색
            List<Entity> entities = client.world.getOtherEntities(player, box,
                (entity) -> entity instanceof LivingEntity && entity.isAlive());

            LivingEntity closestEntity = null;
            double closestDist = maxDistance;

            for (Entity entity : entities) {
                Box entityBox = entity.getBoundingBox().expand(0.5);
                Vec3d intersection = entityBox.raycast(cameraPos, end).orElse(null);

                if (intersection != null) {
                    double dist = cameraPos.distanceTo(intersection);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closestEntity = (LivingEntity) entity;
                    }
                }
            }

            if (closestEntity != null) {
                float cur = closestEntity.getHealth();
                float hpDiff =0;
                float absorption = AbsorptionCache.getAbsorption(closestEntity);
                float curPlusAbsorption = cur+absorption;

                if(beforeHpMap.size()>=2){
                    beforeHpMap.clear();
                    hpDiffMap.clear();
                    hpDiffTimeMap.clear();
                }

                if(hpDiffMap.containsKey(closestEntity)) {
                    hpDiff = hpDiffMap.get(closestEntity);
                }
                float hpDiffTemp=0;
                float beforeHp=0;
                float hpDiffAbs= 0;//항상 양수
                if(beforeHpMap.containsKey(closestEntity)) {
                    beforeHp= beforeHpMap.get(closestEntity);
                    hpDiffTemp=hpDiff;
                    hpDiffAbs= beforeHp- curPlusAbsorption;

                    System.out.println("hpDiff: "+ hpDiff);
                    if(curPlusAbsorption < beforeHp) {
                        hpDiff= hpDiff+ (beforeHp- curPlusAbsorption);
                    }
                    else if(curPlusAbsorption > beforeHp) {
                        hpDiff=0;
                    }
                }
                beforeHpMap.put(closestEntity, curPlusAbsorption);
                hpDiffMap.put(closestEntity, hpDiff);
                System.out.println("hpDiffMap.get(closestEntity): "+hpDiffMap.get(closestEntity));
                System.out.println("hpDiffTemp: "+hpDiffTemp);
                long hpDiffCurrentTime= closestEntity.getWorld().getTime();
                hpDiffTimeMap.putIfAbsent(closestEntity, hpDiffCurrentTime+20);//보는 즉시 그 대상이 Map에 없으면 1초 생성 계속셈
                long hpTime= hpDiffTimeMap.get(closestEntity);//hpTime: hp 변동 초기화 하는 시간
                if(hpDiffTemp != hpDiffMap.get(closestEntity)) {
                    hpDiffTimeMap.put(closestEntity, max(hpTime+3,hpDiffCurrentTime+6));//남은 시간에 따라 0.3초 혹은 0.5초 증가
                }
                else if(beforeHp != curPlusAbsorption) {//보호막에 피해시에도  추가 지속
                    hpDiffTimeMap.put(closestEntity, max(hpTime+3,hpDiffCurrentTime+6));
                }

                System.out.println("hpDiffCurrentTime: "+hpDiffCurrentTime);
                System.out.println("현재 hpDiffTimeMap 값: "+hpDiffTimeMap.get(closestEntity));
                System.out.println(hpDiffTimeMap.get(closestEntity)- hpDiffCurrentTime);

                if(hpDiffTimeMap.get(closestEntity)-hpDiffCurrentTime <=0 ){
                    System.out.println("time <=0 -> Map clear");
                    beforeHpMap.clear();
                    hpDiffMap.clear();
                    hpDiffTimeMap.clear();
                }

//                System.out.println("Map: "+beforeHpMap);


                float max = closestEntity.getMaxHealth();

                float hpWithAbsorption = max + absorption;
                int barWidth = client.getWindow().getScaledWidth()/3;
                int barHeight = 9;
                int hpDiffBar = (int)((hpDiff/(hpWithAbsorption+hpDiff))*barWidth);
                int hpBar= (int)((cur/(hpWithAbsorption+hpDiff))*barWidth);
                int absorptionBar = (int)((absorption/(hpWithAbsorption+hpDiff))*barWidth);
//                int barLocateX= client.getWindow().getScaledWidth()/2- barWidth/2;
                int barLocateX= 7;
                int barLocateY=  barHeight/2;
                String healthText=""; float textX = 0; float textY = 0;
                if(absorptionBar>0){
                    healthText= String.format("%.0f (+%.0f) / %.0f",cur,absorption,max);
                    textX= barLocateX+ 10;
                    textY= barLocateY+1;

                }
                else if(absorptionBar==0){
                    healthText = String.format("%.0f / %.0f",cur,max);
                    textX = barLocateX+10;
                    textY = barLocateY+1;
                }
                ctx.fill(barLocateX-1,barLocateY-1,barLocateX+barWidth+1,barLocateY+barHeight+1,0x55FFFFFF);//테두리
                ctx.fill(barLocateX,barLocateY,barLocateX+barWidth,barLocateY+barHeight,0xFF000000);//안에 빈 체력(검정)

                ctx.fill(barLocateX,barLocateY,barLocateX+hpBar+1,barLocateY+barHeight,0xFFFF3333);//현재 체력(빨강)

                ctx.fill(barLocateX+hpBar+1 ,barLocateY,barLocateX+1+hpBar+absorptionBar,barLocateY+barHeight,0xFFFFFFFF);//보호막(하양)
                if(hpDiff>0){
                    ctx.fill(barLocateX+hpBar+1+absorptionBar ,barLocateY,barLocateX+1+hpBar+absorptionBar+hpDiffBar,barLocateY+barHeight,0xFF666666);//잃은 체력
//                    System.out.println("hpDiff: "+hpDiff);
                }
                TextRenderer renderer = client.textRenderer;
                OrderedText text = Text.literal(healthText).asOrderedText();

                MatrixStack matrices = ctx.getMatrices();
                renderer.drawWithOutline(
                        text,
                        textX,
                        textY,
                        0xFFFFFF,
                        0x000000,
                        matrices.peek().getPositionMatrix(),
                        ctx.getVertexConsumers(),
                        15728880
                );
                if(hpDiff>0){
                    String damageText= String.format("%.1f의 데미지 입힘.",hpDiff);
                    player.sendMessage(net.minecraft.text.Text.literal(damageText), true);
                }

//                OrderedText text = Text.literal(disp).asOrderedText();
//                var matrix = matrices.peek().getPositionMatrix();
//                var consumers = ctx.getVertexConsumers();
//                int light = 15728880;
//
//                client.textRenderer.drawWithOutline(
//                    text,
//                    10f, 30f,
//                    0xFF5555,
//                    0x000000,
//                    matrix,
//                    consumers,
//                    light
//                );
            }
        });
    }
}
