package com.p1nero.helpme.network;

import com.p1nero.helpme.HelpMe;
import com.p1nero.helpme.network.packet.BasePacket;
import com.p1nero.helpme.network.packet.client.GetJoinMessagePacket;
import com.p1nero.helpme.network.packet.server.HandleJoinMessagePacket;
import com.p1nero.helpme.network.packet.server.SOSPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(HelpMe.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    private static int index;

    public static synchronized void register() {
        register(SOSPacket.class, SOSPacket::decode);
        register(HandleJoinMessagePacket.class, HandleJoinMessagePacket::decode);

        register(GetJoinMessagePacket.class, GetJoinMessagePacket::decode);
    }

    private static <MSG extends BasePacket> void register(final Class<MSG> packet, Function<FriendlyByteBuf, MSG> decoder) {
        INSTANCE.messageBuilder(packet, index++).encoder(BasePacket::encode).decoder(decoder).consumer(BasePacket::handle).add();
    }
}
