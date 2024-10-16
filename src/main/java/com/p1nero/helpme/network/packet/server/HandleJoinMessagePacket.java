package com.p1nero.helpme.network.packet.server;

import com.p1nero.helpme.network.packet.BasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * 根据客户端的需求来播动画
 */
public record HandleJoinMessagePacket(Component component) implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(component);
    }

    public static HandleJoinMessagePacket decode(FriendlyByteBuf buf){
        return new HandleJoinMessagePacket(buf.readComponent());
    }

    @Override
    public void execute(Player player) {
        if(player instanceof ServerPlayer serverPlayer){
            for(ServerPlayer target : serverPlayer.getLevel().getServer().getPlayerList().getPlayers()){
                target.displayClientMessage(serverPlayer.getDisplayName().copy().append(component), false);
            }
        }
    }
}
