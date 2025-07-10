package com.altale.esperis.player_data.money_data;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.util.Identifier;

public interface PlayerMoneyComponent extends Component {
ComponentKey<PlayerMoneyComponent> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(
            new Identifier("esperis", "player_money_component"), PlayerMoneyComponent.class
    );

    void setBalance(int balance);

    int getBalance();

    int[] withdraw(int amount);

    int[] deposit(int amount);

}
