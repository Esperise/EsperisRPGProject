package com.altale.esperis.skills.statSkills.dexStatSkill;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ScopeS2CPacket {
    public static final Identifier USE = new Identifier("esperis", "use_scope");
    public static final Identifier CANCEL = new Identifier("esperis", "cancel_scope");
    public static void send(ServerPlayerEntity player, boolean use, int currentUseTime){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(use);
        buf.writeInt(currentUseTime);
        ServerPlayNetworking.send(player,USE,buf);
    }
}
