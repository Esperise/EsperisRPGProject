package com.altale.esperis.client.packet;


import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class EquipmentAdditionalStatRerollRequestSender {
    public static final Identifier REROLL_REQUEST = new Identifier("esperis", "additional_stat_reroll_request");
    public static void sendRerollRequest(ItemStack stack) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeItemStack(stack);
        ClientPlayNetworking.send(REROLL_REQUEST, buf);
    }
}
