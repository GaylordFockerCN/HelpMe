package com.p1nero.fast_tpa;

import com.p1nero.fast_tpa.config.ClientConfig;
import com.p1nero.fast_tpa.config.ServerConfig;
import com.p1nero.fast_tpa.network.packet.client.GetJoinMessagePacket;
import com.p1nero.fast_tpa.network.packet.server.HandleJoinMessagePacket;
import com.p1nero.fast_tpa.network.packet.server.SOSPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(FastTPA.MOD_ID)
public class FastTPA {
    public static final String MOD_ID = "fast_tpa";
    public static final String ACCEPTED = "accepted";
    public static final String LAST_SEND_TIME = "last_send_time";
    public FastTPA(ModContainer modContainer, IEventBus bus){
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, ServerConfig.SPEC);

        bus.addListener(this::registerPackets);
    }

    public void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MOD_ID).versioned("1.0.0").optional();

        // CLIENTBOUND
        registrar.playToClient(GetJoinMessagePacket.TYPE, GetJoinMessagePacket.STREAM_CODEC, GetJoinMessagePacket::execute);
        // SERVERBOUND
        registrar.playToServer(HandleJoinMessagePacket.TYPE, HandleJoinMessagePacket.STREAM_CODEC, HandleJoinMessagePacket::execute);
        registrar.playToServer(SOSPacket.TYPE, SOSPacket.STREAM_CODEC, SOSPacket::execute);
    }

    public static MutableComponent getFormattedName(Player player) {
        if(player.getDisplayName() != null) {
            return Component.literal("[").append(player.getDisplayName()).append("] : ");
        }
        return Component.literal("[").append(player.getScoreboardName()).append("] : ");
    }

}
