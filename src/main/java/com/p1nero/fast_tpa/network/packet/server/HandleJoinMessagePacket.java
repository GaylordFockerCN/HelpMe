package com.p1nero.fast_tpa.network.packet.server;

import com.p1nero.fast_tpa.FastTPA;
import com.p1nero.fast_tpa.config.ServerConfig;
import com.p1nero.fast_tpa.network.packet.BasePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record HandleJoinMessagePacket(Component component, UUID id) implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(component);
        buf.writeUUID(id);
    }

    public static HandleJoinMessagePacket decode(FriendlyByteBuf buf){
        return new HandleJoinMessagePacket(buf.readComponent(), buf.readUUID());
    }

    @Override
    public void execute(Player player) {
        if(player instanceof ServerPlayer self){
            MutableComponent formattedMessage = FastTPA.getFormattedName(self).append(component);
            if(ServerConfig.BROADCAST.get()) {
                for(ServerPlayer serverPlayer : self.server.getPlayerList().getPlayers()) {
                    serverPlayer.displayClientMessage(formattedMessage, false);
                }
            } else {
                Player original = self.serverLevel().getPlayerByUUID(id);
                if(original != null) {
                    original.displayClientMessage(formattedMessage, false);
                    self.displayClientMessage(formattedMessage, false);
                }
            }
        }
    }
}
