package com.p1nero.fast_tpa.network.packet.client;

import com.p1nero.fast_tpa.FastTPA;
import com.p1nero.fast_tpa.config.ClientConfig;
import com.p1nero.fast_tpa.network.packet.server.HandleJoinMessagePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record GetJoinMessagePacket(String uuid) implements CustomPacketPayload {
    public static final Type<GetJoinMessagePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FastTPA.MOD_ID, "get_join_message_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GetJoinMessagePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            GetJoinMessagePacket::uuid,
            GetJoinMessagePacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void execute(GetJoinMessagePacket packet, IPayloadContext context) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().level != null){
            PacketDistributor.sendToServer(new HandleJoinMessagePacket(ClientConfig.WHEN_JOIN.get(), packet.uuid));
        }
    }
}
