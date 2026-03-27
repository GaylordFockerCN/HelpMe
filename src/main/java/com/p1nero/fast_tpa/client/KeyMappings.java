package com.p1nero.fast_tpa.client;

import com.p1nero.fast_tpa.config.ClientConfig;
import com.p1nero.fast_tpa.FastTPAMod;
import com.p1nero.fast_tpa.network.PacketHandler;
import com.p1nero.fast_tpa.network.PacketRelay;
import com.p1nero.fast_tpa.network.packet.server.SOSPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = {Dist.CLIENT},bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyMappings {
    public static final KeyMapping SEND = new KeyMapping("key.fast_tpa.send", GLFW.GLFW_KEY_RIGHT_ALT, "key.fast_tpa.category");
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(SEND);
    }

    @Mod.EventBusSubscriber(modid = FastTPAMod.MOD_ID)
    public static class HandleClientTick{

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if(event.phase.equals(TickEvent.Phase.END)){
                while (SEND.consumeClick()){
                    PacketRelay.sendToServer(PacketHandler.INSTANCE, new SOSPacket(Component.Serializer.fromJson(ClientConfig.SOS_MESSAGE.get())));
                }
            }
        }

    }
}
