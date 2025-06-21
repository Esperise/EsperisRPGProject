package com.altale.esperis.client.healthHUD;
// com.altale.esperis.client.LookingEntityHealthHUD.java


import com.altale.esperis.accessor.CustomAbsorptionAccessor;
import com.altale.esperis.client.cache.AbsorptionCache;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LookingEntityHealthHUD {
    private static final Map<LivingEntity, Float> beforeHpMap = new HashMap<>();
    private static final Map<LivingEntity, Float> hpDiffMap = new HashMap<>();
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
            Box box = player.getBoundingBox().stretch(lookVec.multiply(maxDistance)).expand(1.0);

            // 후보 엔티티 검색
            List<Entity> entities = client.world.getOtherEntities(player, box,
                (entity) -> entity instanceof LivingEntity && entity.isAlive());

            LivingEntity closestEntity = null;
            double closestDist = maxDistance;

            for (Entity entity : entities) {
                Box entityBox = entity.getBoundingBox().expand(1.3);
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
//                float hpDiff =0;
//                if(beforeHpMap.size()>=2){
//                    beforeHpMap.clear();
//                    hpDiffMap.clear();
//                }
//                if(hpDiffMap.containsKey(closestEntity)) {
//                    hpDiff = hpDiffMap.get(closestEntity);
//                }
//                if(beforeHpMap.containsKey(closestEntity)) {
//                    float beforeHp= beforeHpMap.get(closestEntity);
//                    if(cur < beforeHp) {
//                        hpDiff=  (beforeHp- cur);
//
//                    }
//                }
//                beforeHpMap.put(closestEntity, cur);
//                hpDiffMap.put(closestEntity, hpDiff);
//                System.out.println("Map: "+beforeHpMap);


                float max = closestEntity.getMaxHealth();
                float absorption = AbsorptionCache.getAbsorption(closestEntity);
                float hpWithAbsorption = max + absorption;
                int barWidth = client.getWindow().getScaledWidth()/3;
                int barHeight = 9;
//                int hpDiffBar = (int)((hpDiff/hpWithAbsorption)*barWidth);
                int hpBar= (int)((cur/hpWithAbsorption)*barWidth);
                int absorptionBar = (int)((absorption/hpWithAbsorption)*barWidth);
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
//                if(hpDiff>0){
//                    ctx.fill(barLocateX+hpBar+1+absorptionBar-hpDiffBar ,barLocateY,barLocateX+1+hpBar+absorptionBar,barLocateY+barHeight,0xFF666666);//잃은 체력
//                    System.out.println("hpDiff: "+hpDiff);
//                }
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
