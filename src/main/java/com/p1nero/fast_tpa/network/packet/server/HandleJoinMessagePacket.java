package com.p1nero.fast_tpa.network.packet.server;

import com.p1nero.fast_tpa.FastTPAMod;
import com.p1nero.fast_tpa.config.ServerConfig;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record HandleJoinMessagePacket(Component message, String id, String soundId) implements CustomPacketPayload {
    public static final Type<@NotNull HandleJoinMessagePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(FastTPAMod.MOD_ID, "handle_join_message_packet"));
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull HandleJoinMessagePacket> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC,
            HandleJoinMessagePacket::message,
            ByteBufCodecs.STRING_UTF8,
            HandleJoinMessagePacket::id,
            ByteBufCodecs.STRING_UTF8,
            HandleJoinMessagePacket::soundId,
            HandleJoinMessagePacket::new);

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }

    public static void execute(HandleJoinMessagePacket packet, IPayloadContext context) {
        if(context.player() instanceof ServerPlayer self){
            Component component = packet.message;
            if(component == null) {
                return;
            }
            MutableComponent formattedMessage = FastTPAMod.getFormattedName(self).append(component);
            if(ServerConfig.shouldBroadcast()) {
                for(ServerPlayer serverPlayer : self.level().getServer().getPlayerList().getPlayers()) {
                    serverPlayer.sendSystemMessage(formattedMessage);
                }
            } else {
                Player original = self.level().getPlayerByUUID(UUID.fromString(packet.id()));
                if(original != null) {
                    original.sendSystemMessage(formattedMessage);
                    self.sendSystemMessage(formattedMessage);
                }
            }
            if(ServerConfig.shouldPlaySound()) {
                Holder<@NotNull SoundEvent> soundEventHolder = Holder.direct(SoundEvent.createVariableRangeEvent(Identifier.parse(packet.soundId)));
                self.level().playSound(null, self.getX(), self.getY(), self.getZ(), soundEventHolder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }
}
