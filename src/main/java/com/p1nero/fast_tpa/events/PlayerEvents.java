package com.p1nero.fast_tpa.events;

import com.p1nero.fast_tpa.FastTPA;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = FastTPA.MOD_ID)
public class PlayerEvents {
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        init(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        init(event.getEntity());
    }

    public static void init(Player serverPlayer) {
        serverPlayer.getPersistentData().putBoolean(FastTPA.ACCEPTED, false);
        serverPlayer.getPersistentData().putBoolean(FastTPA.CAN_ACCEPT, false);
        serverPlayer.getPersistentData().putInt(FastTPA.LAST_SEND_TIME, 0);
    }

}
