package com.p1nero.fast_tpa.network.packet.server;

import com.p1nero.fast_tpa.FastTPAMod;
import com.p1nero.fast_tpa.config.ServerConfig;
import com.p1nero.fast_tpa.network.packet.BasePacket;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record HandleJoinMessagePacket(Component component, UUID id, String soundId) implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(component);
        buf.writeUUID(id);
        buf.writeUtf(soundId);
    }

    public static HandleJoinMessagePacket decode(FriendlyByteBuf buf){
        return new HandleJoinMessagePacket(buf.readComponent(), buf.readUUID(), buf.readUtf());
    }

    @Override
    public void execute(Player player) {
        if(player instanceof ServerPlayer self){
            MutableComponent formattedMessage = FastTPAMod.getFormattedName(self).append(component);
            if(ServerConfig.shouldBroadcast()) {
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
            if(ServerConfig.shouldPlaySound()) {
                System.out.println(soundId);
                Holder<SoundEvent> soundEventHolder = Holder.direct(SoundEvent.createVariableRangeEvent(new ResourceLocation(soundId)));
                self.level().playSound(null, self.getX(), self.getY(), self.getZ(), soundEventHolder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }
}
