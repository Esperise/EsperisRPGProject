package com.altale.esperis.player_data.stat_data.StatComponents;

import com.altale.esperis.player_data.stat_data.StatPointType;
import com.altale.esperis.player_data.stat_data.StatType;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface PlayerFinalStatComponent extends Component {
    ComponentKey<PlayerFinalStatComponent> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            new Identifier("esperis","player_final_stat_component"), PlayerFinalStatComponent.class
    );
    void setFinalStat(StatType statType, double value);
    void setAllFinalStat();
    double getFinalStat(StatType statType);
    Map<StatType, Double> getAllFinalStat();

}
