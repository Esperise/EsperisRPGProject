package com.altale.esperis.client.HUD;

import com.altale.esperis.client.cache.CoolTimeTextCache;
import com.altale.esperis.skills.buff.AbilityBuff;
import com.altale.esperis.skills.buff.HealBuff;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CurrentBuffHUD {
    public static void register() {
        HudRenderCallback.EVENT.register((DrawContext ctx, float tickDelta) -> {
            MatrixStack matrices = ctx.getMatrices();
            // 1) 클라이언트 가져오기
            MinecraftClient client = MinecraftClient.getInstance();
            // 또는 Fabric API 버전에서 제공한다면
            // MinecraftClient client = ctx.getClient();
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            var consumers = ctx.getVertexConsumers();
            int x = client.getWindow().getScaledWidth()/2 + 100;
            int light = 15728880;
            if (client.player != null) {
                Map<String, Map<Integer, Integer>> buffMap = AbilityBuff.getBufInfoForDisplay((PlayerEntity)client.player);
                List<HealBuff.HealData> healDataList = HealBuff.getHealData(client.player);
                Set<Map.Entry<String, Map<Integer, Integer>>> buffEntry= buffMap.entrySet();
                String buffName;
                int buffRemainingTicks;
                int buffStack;
                int y=0;
                if(!buffMap.isEmpty()){
                    for(Map.Entry<String, Map<Integer, Integer>> entry : buffEntry) {
                        buffName = entry.getKey();
                        for(Map.Entry<Integer, Integer> buffEntry1 : entry.getValue().entrySet()) {
                            buffStack = buffEntry1.getValue()+1;
                            buffRemainingTicks = buffEntry1.getKey();
                            String textString = String.format("%s ", buffName);
                            if(buffStack ==1 ){
                                textString = textString+" : ";
                            }else{
                                textString = textString+"| "+buffStack+" : ";
                            }
                            OrderedText text;
                            textString = textString + String.format("%.1f", buffRemainingTicks/20.0f) +"초";
                            if(buffRemainingTicks < 40){
                                text = Text.literal(textString).formatted(Formatting.RED).asOrderedText();
                            }else{
                                text = Text.literal(textString).asOrderedText();
                            }
                            client.textRenderer.drawWithOutline(
                                    text,
                                    x, (float) (client.getWindow().getScaledHeight() - (9.0*(y+1))),
                                    0xFFFFFF,
                                    0x000000,
                                    matrix,
                                    consumers,
                                    light
                            );
                            y++;
                        }
                    }
                }
                int healY =0;
                if(healDataList!=null && !healDataList.isEmpty()){
                    for(int i =0; i<healDataList.size(); i++){
                        HealBuff.HealData healData = healDataList.get(i);
                        float remainingHeal = (float) (healData.getRemainingTicks()*healData.getHealAmount()/ healData.getDuration());
                        OrderedText healText= Text.literal(String.format("%s : %.0f",healData.getSkillId(), remainingHeal )).formatted(Formatting.GREEN, Formatting.BOLD).asOrderedText();
                        client.textRenderer.drawWithOutline(
                                healText,
                                x+150, (float) (client.getWindow().getScaledHeight() - (9.0*(healY+1))),
                                0xFFFFFF,
                                0x000000,
                                matrix,
                                consumers,
                                light
                        );
                        healY++;
                    }
                }

            }
        });
    }
}
