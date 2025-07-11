package com.altale.esperis.player_data.stat_data;

import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.util.Identifier;

public interface PlayerStatComponent extends Component {
    ComponentKey<PlayerStatComponent> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            new Identifier("esperis", "player_stat_component"), PlayerStatComponent.class
    );

}
