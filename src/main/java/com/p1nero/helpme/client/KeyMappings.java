package com.p1nero.helpme.client;

import com.p1nero.helpme.Config;
import com.p1nero.helpme.HelpMe;
import com.p1nero.helpme.network.PacketHandler;
import com.p1nero.helpme.network.PacketRelay;
import com.p1nero.helpme.network.packet.server.SOSPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = {Dist.CLIENT},bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyMappings {
    public static final MyKeyMapping SEND = new MyKeyMapping("key.help_me.send", GLFW.GLFW_KEY_RIGHT_ALT, "key.help_me.category");
    @SubscribeEvent
    public static void registerKeys(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(SEND);
    }

    @Mod.EventBusSubscriber(modid = HelpMe.MOD_ID)
    public static class HandleClientTick{

        /**
         * 按键切换棍势，确保学过才可以用按键切换。
         */
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if(SEND.isRelease()){
                PacketRelay.sendToServer(PacketHandler.INSTANCE, new SOSPacket(Component.Serializer.fromJson(Config.SOS_MESSAGE.get())));
            }
        }

    }
}
