package com.altale.esperis.client.HUD;

import com.altale.esperis.client.cache.UseScopeCache;
import com.altale.esperis.skills.statSkills.dexStatSkill.Snipe;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scope {
    private static final Logger LOG = LoggerFactory.getLogger(Scope.class);
    public static final Identifier SCOPE_TEX = new Identifier("minecraft", "textures/misc/spyglass_scope.png");
    public static boolean ZOOMING = false;          // 토글 on/off
    public static float zoomProgress = 0.0f;
    public static void register(){
//        ClientTickEvents.END_CLIENT_TICK.register(client -> {
//            boolean isScope = UseScopeCache.isUseScope();
//            if(!isScope) return;
//            int scopeTime = UseScopeCache.getScopeTime();
//                    ZOOMING = isScope && scopeTime > 0;
//                try {
//                    // 1.20.1 Yarn: options.smoothCameraEnabled 필드/메서드 이름은 환경에 따라 다를 수 있음
//                    client.options.smoothCameraEnabled = ZOOMING;
//                } catch (Throwable ignored) {}
//
//
//            // 부드러운 전환 (lerp)
//            float speed = 0.2f;
//            if (ZOOMING) zoomProgress = Math.min(1.0f, zoomProgress + speed);
//            else         zoomProgress = Math.max(0.0f, zoomProgress - speed);
//            Log.info(LogCategory.LOG,"zooming: %b , zoomProgress : %.2f", ZOOMING, zoomProgress );
//        });

        // HUD에 스코프 텍스쳐 그리기
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (UseScopeCache.getScopeTime() < 1) return;
            final MinecraftClient mc = MinecraftClient.getInstance();

            int sw = mc.getWindow().getScaledWidth();
            int sh = mc.getWindow().getScaledHeight();
            int size = Math.min(sw, sh);                 // 정사각형(원) 영역
            int x = (sw - size) / 2;
            int y = (sh - size) / 2;

            float aimingMaxDamageTime= Snipe.aimingMaxDamageTime / 20.0f - UseScopeCache.getScopeTime() / 20.0f;
            float maxAimingTime = Snipe.maxAimingTime / 20.0f;
            OrderedText text;
            if(aimingMaxDamageTime > 0 ){
                text = Text.literal(String.format("최대 충전 까지 : %.2f | 남은 시간: %.2f",  aimingMaxDamageTime, maxAimingTime- (UseScopeCache.getScopeTime() / 20.0f) )).asOrderedText();
            }else{
                text = Text.literal(String.format("최대 충전 | 남은 시간: %.2f", maxAimingTime- (UseScopeCache.getScopeTime() / 20.0f) )).asOrderedText();
            }
            MatrixStack matrices = drawContext.getMatrices();
            // 1) 클라이언트 가져오기
            MinecraftClient client = MinecraftClient.getInstance();
            // 또는 Fabric API 버전에서 제공한다면
            // MinecraftClient client = ctx.getClient();
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            var consumers = drawContext.getVertexConsumers();
            mc.textRenderer.drawWithOutline(
                    text,
                    x , (float) (mc.getWindow().getScaledHeight() )-70,
                    0xFFFFFF,
                    0x000000,
                    matrix,
                    consumers,
                    15728880
            );
        });
    }

}
