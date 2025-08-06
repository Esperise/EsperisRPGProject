package com.altale.esperis.player_data.stat_data.StatComponents;

import com.altale.esperis.player_data.stat_data.StatPointType;
import com.altale.esperis.player_data.stat_data.StatType;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface PlayerPointStatComponent extends Component {
    ComponentKey<PlayerPointStatComponent> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            new Identifier("esperis", "player_point_stat_component"), PlayerPointStatComponent.class
    );


    //stat
        //setter
        void setPointStat(StatType statType, double amount);


        //getter
        double getPointStat(StatType statType);
        Map<StatType, Double> getAllPointStat();
        void setSP(StatPointType spType, int amount);
        void useSP(StatType statType, int amount);
        int getSP(StatPointType statPointType);
        void addSP(StatPointType statPointType, int amount);
        void subtractSP(StatPointType statPointType, int amount);


    //adder
    void addStat(StatType statType, double statValue);
    void subtractStat(StatType statType, double statValue);


}
