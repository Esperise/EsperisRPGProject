package com.altale.esperis.serverSide.packet;

import com.altale.esperis.player_data.equipmentStat.ChangeEquipmentStat;
import com.altale.esperis.player_data.equipmentStat.EquipmentInfoManager;
import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EquipmentAdditionalStatRerollRequestSender {
    public static final Identifier REROLL_REQUEST = new Identifier("esperis", "additional_stat_reroll_request");
    public static void register(){
        ServerPlayNetworking.registerGlobalReceiver(REROLL_REQUEST, (server, player, handler, buf, responseSender) -> {
//            ItemStack stack = buf.readItemStack();
            server.execute(() -> {
                ItemStack stack = player.currentScreenHandler.getSlot(0).getStack();
                System.out.println(stack);
                if(EquipmentInfoManager.hasEquipmentInfo(stack)){
                    ChangeEquipmentStat.changeStat(stack, player);
                }
                else{
                    Map<StatType , Double> map = new HashMap<>();
                    map.put(StatType.MAX_HEALTH,2.0);
                    map.put(StatType.DEF,2.0);

                    EquipmentInfoManager.setEquipmentInfo(stack,1,0,10,5
                    ,map);
                    player.currentScreenHandler.sendContentUpdates();
                }
            });
        });
    }
}
