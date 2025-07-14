package com.altale.esperis.player_data.stat_data;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.util.Identifier;

public interface PlayerPointStatComponent extends Component {
    ComponentKey<PlayerPointStatComponent> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            new Identifier("esperis", "player_stat_component"), PlayerPointStatComponent.class
    );


    //stat
        //setter
        void setPointStat(StatType statType, double amount);


        //getter
        double getPointStat(StatType statType);


    //adder
    void addStat(StatType statType, double statValue);
    void subtractStat(StatType statType, double statValue);


}
