package com.altale.esperis.serverSide.packet;

import com.altale.esperis.player_data.skill_data.PlayerSkillComponent;
import com.altale.esperis.player_data.skill_data.SkillManager;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerPointStatComponent;
import com.altale.esperis.player_data.stat_data.StatManager;
import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class StatAddRequestReceiver {
    public static final Identifier ID2 = new Identifier("esperis", "stat_add_request");
    public static void register(){
        ServerPlayNetworking.registerGlobalReceiver(ID2, (server, player, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            StatType statType =  buf.readEnumConstant(StatType.class);
            int value = buf.readInt();
            server.execute(() -> {
                ServerPlayerEntity playerTarget = server.getPlayerManager().getPlayer(uuid);
                if (playerTarget != null) {
                    PlayerPointStatComponent playerPointStatComponent = PlayerPointStatComponent.KEY.get(playerTarget);
                    PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(playerTarget);
                    playerPointStatComponent.useSP(statType, value);//미사용 sp감소 , addSp
                    StatManager.statUpdate(playerTarget);
                    playerSkillComponent.setUnlockedSkill();

                }
            });
        });
    }
}
