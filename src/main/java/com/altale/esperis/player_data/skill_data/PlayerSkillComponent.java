package com.altale.esperis.player_data.skill_data;

import com.altale.esperis.player_data.stat_data.StatType;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PlayerSkillComponent extends Component {
    ComponentKey<PlayerSkillComponent> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            new Identifier("esperis", "player_skill_component"), PlayerSkillComponent.class);

    void setKeyBinding(String keyId, SkillsId skillId);
    SkillsId getKeyBoundSkill(String keyId);
    String getSkillKey(SkillsId skillId);
    Map<String , SkillsId> getKeyBindSkills();

    void setUnlockedSkill();
    Set<SkillsId> getUnlockedSkillsSet();
    Map<StatType, List<SkillsId>> getUnlockedStatSkillsMap();

    boolean isPassiveSkill(SkillsId skillId);
    boolean isKeydownSkill(SkillsId skillId);
    boolean isUnlockedSkill(SkillsId skillId);


}
