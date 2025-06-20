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

import java.util.List;

public class LookingEntityHealthHUD {

    public static void register() {
        HudRenderCallback.EVENT.register((DrawContext ctx, float tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;
            if (player == null || client.world == null) return;

            double maxDistance = 15.0;
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


//                if( closestEntity instanceof CustomAbsorptionAccessor accessor) {
//                    absorption=accessor.getCustomAbsorption();
//                }
                float cur = closestEntity.getHealth();
                float max = closestEntity.getMaxHealth();
                float absorption = AbsorptionCache.getAbsorption(closestEntity);
                System.out.println(absorption);
                float hpWithAbsorption = max + absorption;
                int barWidth = 240;
                int barHeight = 9;
                int hpBar= (int)((cur/hpWithAbsorption)*barWidth);
                int aborptionBar = (int)((absorption/hpWithAbsorption)*barHeight);
                int barLocateX= client.getWindow().getScaledWidth()/2- barWidth/2;
                int barLocateY=  barHeight/3;
                String healthText=""; float textX = 0; float textY = 0;
                if(aborptionBar>0){
                    healthText= String.format("%.0f (+%.0f) / %.0f",cur,absorption,max);
                    textX= barLocateX+ 10;
                    textY= barLocateY;
                    System.out.println(aborptionBar);
                }
                else if(aborptionBar==0){
                    healthText = String.format("%.0f / %.0f",cur,max);
                    textX = barLocateX+10;
                    textY = barLocateY;
                }
                ctx.fill(barLocateX-1,barLocateY-1,barLocateX+barWidth+1,barLocateY+barHeight+1,0x55FFFFFF);
                ctx.fill(barLocateX,barLocateY,barLocateX+barWidth,barLocateY+barHeight,0xFF000000);

                ctx.fill(barLocateX,barLocateY,barLocateX+hpBar+1,barLocateY+barHeight,0xFFFF3333);

                ctx.fill(barLocateX+hpBar+1 ,barLocateY,barLocateX+hpBar+1+aborptionBar,barLocateY+barHeight,0xFFFFFFFF);
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
