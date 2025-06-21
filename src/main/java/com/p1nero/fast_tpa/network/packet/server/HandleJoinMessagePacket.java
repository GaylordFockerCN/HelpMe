package com.p1nero.fast_tpa.network.packet.server;

import com.p1nero.fast_tpa.FastTPA;
import com.p1nero.fast_tpa.config.ServerConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record HandleJoinMessagePacket(String message, String id) implements CustomPacketPayload {
    public static final Type<HandleJoinMessagePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FastTPA.MOD_ID, "handle_join_message_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HandleJoinMessagePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(262144),
            HandleJoinMessagePacket::message,
            ByteBufCodecs.STRING_UTF8,
            HandleJoinMessagePacket::id,
            HandleJoinMessagePacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void execute(HandleJoinMessagePacket packet, IPayloadContext context) {
        if(context.player() instanceof ServerPlayer self){
            Component component = Component.Serializer.fromJson(packet.message, self.registryAccess());
            if(component == null) {
                return;
            }
            MutableComponent formattedMessage = FastTPA.getFormattedName(self).append(component);
            if(ServerConfig.BROADCAST.get()) {
                for(ServerPlayer serverPlayer : self.server.getPlayerList().getPlayers()) {
                    serverPlayer.displayClientMessage(formattedMessage, false);
                }
            } else {
                Player original = self.serverLevel().getPlayerByUUID(UUID.fromString(packet.id()));
                if(original != null) {
                    original.displayClientMessage(formattedMessage, false);
                    self.displayClientMessage(formattedMessage, false);
                }
            }
        }
    }
}
