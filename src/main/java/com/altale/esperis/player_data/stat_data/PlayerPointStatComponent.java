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
        void setAtk(int atkValue);
        void setMaxHealth(int maxHealthValue);
        void setDef(int defValue);
        void setStr(int strValue);
        void setDex(int dexValue);
        void setLuk(int lukValue);
        void setDur(int durValue);
        void setSpd(int spdValue);

        //getter
        int getAtk();
        int getMaxHealth();
        int getDef();
        int getStr();
        int getDex();
        int getLuk();
        int getDur();
        int getSpd();

    //adder
    void addStat(String strType, int statValue);
    void subtractStat(String strType, int statValue);


}
