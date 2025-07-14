package com.altale.esperis.player_data.stat_data;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.util.Identifier;

public interface PlayerFinalStatComponent extends Component {
    ComponentKey<PlayerFinalStatComponent> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            new Identifier("esperis","final_stat"), PlayerFinalStatComponent.class
    );
    void setFinalStat(StatType statType, double value);
    void setAllFinalStat();
    double getFinalStat(StatType statType);

}
