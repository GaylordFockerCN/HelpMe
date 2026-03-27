package com.p1nero.fast_tpa.client;

import com.p1nero.fast_tpa.config.ClientConfig;
import com.p1nero.fast_tpa.FastTPAMod;
import com.p1nero.fast_tpa.network.packet.server.SOSPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT)
public class KeyMappings {

    private static final KeyMapping.Category COMMON = new KeyMapping.Category(Identifier.fromNamespaceAndPath(FastTPAMod.MOD_ID, "common"));

    public static final KeyMapping SEND = new KeyMapping("key.fast_tpa.send", GLFW.GLFW_KEY_RIGHT_ALT, COMMON);
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(SEND);
    }

    @EventBusSubscriber(modid = FastTPAMod.MOD_ID)
    public static class HandleClientTick{

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            while (SEND.consumeClick()){
                if(Minecraft.getInstance().level != null) {
                    Component message = FastTPAMod.fromJson(ClientConfig.SOS_MESSAGE.get());
                    ClientPacketDistributor.sendToServer(new SOSPacket(message));
                }
            }
        }

    }
}
