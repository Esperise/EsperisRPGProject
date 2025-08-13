package com.altale.esperis.player_data.stat_data.StatComponents;

import com.altale.esperis.player_data.stat_data.StatType;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface BaseAbilityComponent extends Component {
    ComponentKey<BaseAbilityComponent> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            new Identifier("esperis", "player_base_ability_component"), BaseAbilityComponent.class
    );
    void setBaseAbility(StatType statType, Double value);
    void saveBaseAbility();
    public double getBaseAbility(StatType statType);
    Map<StatType, Double> getAbilityMap();
}
