package com.altale.esperis.client.screen.Button;

import com.altale.esperis.player_data.stat_data.StatPointType;
import com.altale.esperis.player_data.stat_data.StatType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SpButtonFactory {
    private static final Map<StatType, Integer> valueMap = new EnumMap<>(StatType.class);
    private static final Map<StatPointType, Integer> SpMap = new EnumMap<>(StatPointType.class);

    public static ButtonWidget createSpDecrease(StatType statType, int x, int y, int width, int height) {
        MinecraftClient client= MinecraftClient.getInstance();
        ButtonWidget decreseBtn = ButtonWidget.builder(Text.literal("◀"),btn->{
            int valueTemp= valueMap.getOrDefault(statType ,0);
            int spTemp= SpMap.getOrDefault(StatPointType.UnusedSP,0);
            if(valueTemp<=0){
                return;
            }else{
                valueMap.put(statType,valueTemp-1);// statType에 사용예정인 sp 1 감소-> 예정 미사용 sp map값은 1 증가
                SpMap.put(StatPointType.UnusedSP,spTemp+1);
            }
        }).dimensions(x,y,width,height).build();
        return decreseBtn;
    }
    public static ButtonWidget createSpIncrease(StatType statType, int x, int y, int width, int height) {
        MinecraftClient client= MinecraftClient.getInstance();
        ButtonWidget increseBtn = ButtonWidget.builder(Text.literal("▶"),btn->{
            int valueTemp= valueMap.getOrDefault(statType ,0);
            int spTemp= SpMap.getOrDefault(StatPointType.UnusedSP,0);
                valueMap.put(statType,valueTemp+1);// statType에 사용예정인 sp 1 증가-> 예정 미사용 sp map값은 1 감소
                SpMap.put(StatPointType.UnusedSP,spTemp-1);

        }).dimensions(x,y,width,height).build();
        return increseBtn;
    }


    public static ButtonWidget createSpButton(
            StatType statType, int x , int y, int width, int height,
            Supplier<Integer> getCurrent,
            Supplier<Integer> geUnused,
            BiConsumer<StatType, Integer> onChange,
            String label){
        return ButtonWidget.builder(Text.literal(label), btn ->{
            int current = getCurrent.get();
            int unused = geUnused.get();
            boolean isIncrease = "▶".equals(label);
            if(isIncrease && unused<=0) return; //감소버튼이고 미사용 sp가 0보다 작거나 같을 때 아무것도 실행 안 시킴
            if(!isIncrease && current<=0) return; //감소 버튼이고 current가 0보다 작거나 같을 때 아무것도 실행 안시킴
            int newValue = isIncrease ? current+1 : current-1;
            onChange.accept(statType, newValue);//-> StatType, Integer로 값넘겨짐 실제 구현에서 (t, u)->{...} 로 써서 값 넘김
        }).dimensions(x,y,width,height).build();
    }

}
