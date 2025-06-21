package com.p1nero.fast_tpa.network.packet.client;

import com.p1nero.fast_tpa.config.ClientConfig;
import com.p1nero.fast_tpa.network.PacketHandler;
import com.p1nero.fast_tpa.network.PacketRelay;
import com.p1nero.fast_tpa.network.packet.BasePacket;
import com.p1nero.fast_tpa.network.packet.server.HandleJoinMessagePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record GetJoinMessagePacket(UUID uuid) implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
    }

    public static GetJoinMessagePacket decode(FriendlyByteBuf buf){
        return new GetJoinMessagePacket(buf.readUUID());
    }

    @Override
    public void execute(Player player) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().level != null){
            PacketRelay.sendToServer(PacketHandler.INSTANCE, new HandleJoinMessagePacket(Component.Serializer.fromJson(ClientConfig.WHEN_JOIN.get()), uuid));
        }
    }
}
