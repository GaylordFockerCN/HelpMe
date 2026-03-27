package com.p1nero.fast_tpa.network.packet.client;

import com.p1nero.fast_tpa.FastTPAMod;
import com.p1nero.fast_tpa.config.ClientConfig;
import com.p1nero.fast_tpa.network.packet.server.HandleJoinMessagePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record GetJoinMessagePacket(String uuid) implements CustomPacketPayload {
    public static final Type<@NotNull GetJoinMessagePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(FastTPAMod.MOD_ID, "get_join_message_packet"));
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull GetJoinMessagePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            GetJoinMessagePacket::uuid,
            GetJoinMessagePacket::new);

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }

    public static void execute(GetJoinMessagePacket packet, IPayloadContext context) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().level != null){
            Component message = FastTPAMod.fromJson(ClientConfig.WHEN_JOIN.get());
            ClientPacketDistributor.sendToServer(new HandleJoinMessagePacket(message, packet.uuid, ClientConfig.SOUND_ID.get()));
        }
    }
}
