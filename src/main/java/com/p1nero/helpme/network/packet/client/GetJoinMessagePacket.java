package com.p1nero.helpme.network.packet.client;

import com.p1nero.helpme.Config;
import com.p1nero.helpme.network.PacketHandler;
import com.p1nero.helpme.network.PacketRelay;
import com.p1nero.helpme.network.packet.BasePacket;
import com.p1nero.helpme.network.packet.server.HandleJoinMessagePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

/**
 * 根据客户端的需求来播动画
 */
public record GetJoinMessagePacket() implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {

    }

    public static GetJoinMessagePacket decode(FriendlyByteBuf buf){
        return new GetJoinMessagePacket();
    }

    @Override
    public void execute(Player player) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().level != null){
            PacketRelay.sendToServer(PacketHandler.INSTANCE, new HandleJoinMessagePacket(Component.Serializer.fromJson(Config.WHEN_JOIN.get())));
        }
    }
}
