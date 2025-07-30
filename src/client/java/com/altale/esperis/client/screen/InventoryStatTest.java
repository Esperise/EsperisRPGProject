package com.altale.esperis.client.screen;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponentImp;
import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.Objects;

import static com.google.common.primitives.UnsignedInts.max;
import static com.google.common.primitives.UnsignedInts.min;

public class InventoryStatTest {
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (!(screen instanceof InventoryScreen inv)) return;

            // vanilla GUI 크기
            final int guiW = 176;
            int guiH = (client.getWindow().getScaledHeight() * 7 / 9)+3;
            int x = (client.getWindow().getScaledWidth()   / 100);
            int y = (client.getWindow().getScaledHeight() / 9);
            int x2= (client.getWindow().getScaledWidth() *30   / 100);

            // 렌더 콜백 등록
            ScreenEvents.afterRender(screen).register((scr, ctx, mouseX, mouseY, tickDelta) -> {
                // 레시피 북 오픈 여부 감지
                if (inv.getRecipeBookWidget().isOpen()) return;
                PlayerEntity player = client.player;
                PlayerLevelComponent lvComp= PlayerLevelComponent.KEY.get(Objects.requireNonNull(player));
                PlayerFinalStatComponent finalStatComp= PlayerFinalStatComponent.KEY.get(Objects.requireNonNull(player));
                int lv= lvComp.getLevel();
                int currExp= lvComp.getCurrentExp();
                int maxExp= lvComp.getMaxExp();
                // 반투명 배경 패널
                ctx.fill(x, y, x2, y + guiH, 0x77CACACA);
                MatrixStack matrices = ctx.getMatrices();
                TextRenderer renderer = client.textRenderer;
                renderer.drawWithOutline(
                        Text.literal(String.format("레벨: %d",lv)).asOrderedText(),
                        x+5, y+3,
                        0xFFFFFF, // 글자색
                        0x000000, // 테두리색
                        matrices.peek().getPositionMatrix(),
                        ctx.getVertexConsumers(),
                        15728880
                );
                //경험치
                renderer.drawWithOutline(
                        Text.literal(String.format(" (%d / %d) ",currExp,maxExp)).asOrderedText(),
                        x+42, y+3,0xFFFFFF, // 글자색
                        0x000000, // 테두리색
                        matrices.peek().getPositionMatrix(),
                        ctx.getVertexConsumers(),
                        15728880
                );
                int statBaseX= x+13;
                int statBaseY= y+22;
//                int lineHeight= 12;//delta
                int lineHeight  = (guiH - y ) / (StatType.values().length);
                for(StatType statType: StatType.values()){
                    String label= statType.getDisplayName();
                    if(statType == StatType.ACC || statType == StatType.AVD || statType==StatType.CRIT || statType==StatType.CRIT_DAMAGE
                            || statType == StatType.FinalDamagePercent || statType==StatType.DefPenetrate){
                        double value= finalStatComp.getFinalStat(statType) *100 ;
                        renderer.drawWithOutline(
                        Text.literal(String.format("%s : %.1f%%",label,value)).asOrderedText(),
                        statBaseX,statBaseY,0xFFFFFF, // 글자색
                        0x000000, // 테두리색
                        matrices.peek().getPositionMatrix(),
                        ctx.getVertexConsumers(),
                        15728880
                );
                    } else if (statType== StatType.SPD || statType==StatType.ATTACK_SPEED) {
                        double value= (finalStatComp.getFinalStat(statType)-1) *100 ;
                        renderer.drawWithOutline(
                                Text.literal(String.format("%s : +%.2f%%",label,value)).asOrderedText(),
                                statBaseX,statBaseY,0xFFFFFF, // 글자색
                                0x000000, // 테두리색
                                matrices.peek().getPositionMatrix(),
                                ctx.getVertexConsumers(),
                                15728880
                        );
                    } else{
                        double value=  finalStatComp.getFinalStat(statType);
                        renderer.drawWithOutline(
                                Text.literal(String.format("%s : %.2f",label,value)).asOrderedText(),
                                statBaseX,statBaseY,0xFFFFFF, // 글자색
                                0x000000, // 테두리색
                                matrices.peek().getPositionMatrix(),
                                ctx.getVertexConsumers(),
                                15728880
                        );
                    }
                    statBaseY+= lineHeight;
                }
            });
        });
    }
}
