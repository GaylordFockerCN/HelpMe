package com.p1nero.fast_tpa.client;

import com.p1nero.fast_tpa.config.ClientConfig;
import com.p1nero.fast_tpa.FastTPAMod;
import com.p1nero.fast_tpa.network.packet.server.SOSPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT)
public class KeyMappings {
    public static final KeyMapping SEND = new KeyMapping("key.fast_tpa.send", GLFW.GLFW_KEY_RIGHT_ALT, "key.fast_tpa.category");
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
                    Component message = Component.Serializer.fromJson(ClientConfig.SOS_MESSAGE.get(), Minecraft.getInstance().level.registryAccess());
                    PacketDistributor.sendToServer(new SOSPacket(message));
                }
            }
        }

    }
}
