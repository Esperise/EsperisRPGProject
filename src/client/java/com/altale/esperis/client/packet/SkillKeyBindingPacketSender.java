package com.altale.esperis.client.packet;

import com.altale.esperis.player_data.skill_data.skillKeybind.KeyId;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SkillKeyBindingPacketSender {
    public static final Identifier ID= new Identifier("esperis","skill_keybinding");
    public static void sendSkillKeyBindingPacket(String keyId, Boolean isHolding) {
        ClientPlayerEntity player= MinecraftClient.getInstance().player;
        if (player == null) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(keyId);
        buf.writeBoolean(isHolding);//true: 꾹 누르는 중, false: 눌러다가 뗄 때
        ClientPlayNetworking.send(ID, buf);

    }
}
