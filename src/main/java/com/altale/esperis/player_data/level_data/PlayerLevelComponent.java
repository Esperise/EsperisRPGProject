package com.altale.esperis.player_data.level_data;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.util.Identifier;

import java.io.Serializable;

public interface PlayerLevelComponent extends Component{
    ComponentKey<PlayerLevelComponent> KEY= ComponentRegistryV3.INSTANCE.getOrCreate(
            new Identifier("esperis", "player_level_component"), PlayerLevelComponent.class);
    int getLevel();
    void setLevel(int level);
    int getCurrentExp();
    void setCurrentExp(int currentExp);
    int getMaxExp();
    void setMaxExp(int maxExp);
    boolean canLevelUp();
    void levelUp();
}
