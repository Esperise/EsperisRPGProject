package com.altale.esperis.client.screen;

import com.altale.esperis.client.packet.EquipmentAdditionalStatRerollRequestSender;
import com.altale.esperis.player_data.equipmentStat.AdditionalStatMaker;
import com.altale.esperis.player_data.equipmentStat.ChangeEquipmentStat;
import com.altale.esperis.player_data.equipmentStat.EquipmentInfoManager;
import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import com.ibm.icu.text.BidiTransform;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.client.gui.tooltip.BundleTooltipComponent.TEXTURE;

@Environment(EnvType.CLIENT)
public class RerollAdditionalStatScreen extends HandledScreen<AdditionalStatMaker> {
    private static final Identifier CONTAINER_TEXTURE = new Identifier("minecraft", "textures/gui/container/generic_54.png");
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public RerollAdditionalStatScreen(AdditionalStatMaker handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, Text.literal(""));

        this.backgroundWidth = 400 ;
        this.backgroundHeight= 240;
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

    }


    @Override
    protected void init() {
        super.init(); // 반드시 호출해야 버튼 리셋이 잘 됩니다.

        // 닫기 버튼
        this.addDrawableChild(ButtonWidget.builder(Text.literal("닫기"), btn -> {

            this.close();
        }).dimensions(x  - 60, y + backgroundHeight - 30, 50, 20).build());

        // 장비 재설정 버튼
        this.addDrawableChild(ButtonWidget.builder(Text.literal("스탯 재설정"), btn -> {
            // 서버에 패킷 보내기 등
            ItemStack itemStack = handler.getSlot(0).getStack();
            EquipmentAdditionalStatRerollRequestSender.sendRerollRequest(itemStack);
        }).dimensions(x + 30, y + backgroundHeight - 151, 80, 20).build());

    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        context.fillGradient(x, y, x + backgroundWidth, y + backgroundHeight, 0xFF999999, 0xFFAAAAAA);
        context.fillGradient(x+200+5, y + 35,x+400-5, y+240-35, 0xFF555555, 0xFF000000 );
        context.drawTexture(CONTAINER_TEXTURE, x+22, y+119, 0, 136, 176, 96);
        context.drawTexture(CONTAINER_TEXTURE,x+137, y+90 ,7, 89, 18, 18);
        context.fill(x+3, y+65, x+ 88, y+ 80, 0xFF999999);
        PlayerEntity player = client.player;
        PlayerMoneyComponent moneyComponent= PlayerMoneyComponent.KEY.get(Objects.requireNonNull(player));
        int money= moneyComponent.getBalance();
        MatrixStack matrices = context.getMatrices();
        TextRenderer renderer = client.textRenderer;
        String str1= String.format("%,7d esp", money);
        OrderedText text1 = Text.literal(str1).asOrderedText();
        renderer.drawWithOutline(
                text1,
                x+8, y+68,
                0xFFFFFF, // 글자색
                0x000000, // 테두리색
                matrices.peek().getPositionMatrix(),
                context.getVertexConsumers(),
                15728880
        );

        ItemStack itemStack = handler.getSlot(0).getStack();
        if(EquipmentInfoManager.getEquipmentCanChangeAdditionalNum(itemStack) >=0.0){
            renderer.drawWithOutline(
                    Text.literal(String.format("소모 esp: %d",(EquipmentInfoManager.getRarityLevel(itemStack)+1)* ChangeEquipmentStat.REROLL_COST)).asOrderedText(),
                    x+100, y+68,
                    0xFFFFFF,
                    0x000000,
                    matrices.peek().getPositionMatrix(),
                    context.getVertexConsumers(),
                    15728880
            );renderer.drawWithOutline(
                    Text.literal(String.format("변경 가능 횟수 : %d",(EquipmentInfoManager.getEquipmentCanChangeAdditionalNum(itemStack)))).asOrderedText(),
                    x+100, y+48,
                    0xFFFFFF,
                    0x000000,
                    matrices.peek().getPositionMatrix(),
                    context.getVertexConsumers(),
                    15728880
            );
        }


        String statText= "";
        if(!EquipmentInfoManager.hasEquipmentInfo(itemStack)) {
            statText="스탯 없음";
            renderer.drawWithOutline(
                    Text.literal(statText).asOrderedText(),
                    x+215, y+40,
                    0xFFFFFF, // 글자색
                    0x000000, // 테두리색
                    matrices.peek().getPositionMatrix(),
                    context.getVertexConsumers(),
                    15728880
            );
        } else{
            Map<StatType, Double> statsMap =EquipmentInfoManager.sumEquipmentStats(itemStack);
            int statTextX= x+215;
            int statTextY= y+40;
            int textDeltaY = 15;
            for(StatType statType : StatType.values()) {
                double statValue= statsMap.getOrDefault(statType, 0.0);
                if(statValue == 0.0){
                    continue;
                }

                if(statType == StatType.ACC || statType == StatType.AVD || statType==StatType.CRIT || statType==StatType.CRIT_DAMAGE
                        || statType==StatType.DefPenetrate){
                    statText = String.format("%s  :  %.2f%%",statType.getDisplayName(),statValue*100 );
                }
                else if (statType== StatType.SPD || statType==StatType.ATTACK_SPEED || statType == StatType.FinalDamagePercent) {
                    statText = String.format("%s  :  +%.2f%%",statType.getDisplayName(),statValue*100 );
                }
                else{
                    statText = String.format("%s  :  +%.1f",statType.getDisplayName(),statValue );
                }


                OrderedText orderedText =Text.literal(statText).asOrderedText();
                renderer.drawWithOutline(
                        orderedText,
                        statTextX, statTextY,
                        0xFFFFFF, // 글자색
                        0x000000, // 테두리색
                        matrices.peek().getPositionMatrix(),
                        context.getVertexConsumers(),
                        15728880
                );
                statTextY+= textDeltaY;

            }
//            for(Map.Entry<StatType, Double> entry  : statsMap.entrySet()) {
//                if(entry.getKey() == StatType.ACC || entry.getKey() == StatType.AVD || entry.getKey()==StatType.CRIT || entry.getKey()==StatType.CRIT_DAMAGE
//                        || entry.getKey()==StatType.DefPenetrate){
//                    statText = String.format("%s  :  %.2f%%",entry.getKey().getDisplayName(),entry.getValue()*100 );
//                }
//                else if (entry.getKey()== StatType.SPD || entry.getKey()==StatType.ATTACK_SPEED || entry.getKey() == StatType.FinalDamagePercent) {
//                    statText = String.format("%s  :  +%.2f%%",entry.getKey().getDisplayName(),entry.getValue()*100 );
//                }
//                else{
//                    statText = String.format("%s  :  +%.1f",entry.getKey().getDisplayName(),entry.getValue() );
//                }
//
//
//                OrderedText orderedText =Text.literal(statText).asOrderedText();
//                renderer.drawWithOutline(
//                        orderedText,
//                        statTextX, statTextY,
//                        0xFFFFFF, // 글자색
//                        0x000000, // 테두리색
//                        matrices.peek().getPositionMatrix(),
//                        context.getVertexConsumers(),
//                        15728880
//                );
//                statTextY+= textDeltaY;
//            }
        }

        //AdditionalStatMaker와 연관 있ㅇ므
    }

}
