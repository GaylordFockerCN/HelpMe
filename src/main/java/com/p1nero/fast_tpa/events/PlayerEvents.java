package com.p1nero.fast_tpa.events;

import com.p1nero.fast_tpa.FastTPA;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FastTPA.MOD_ID)
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
        serverPlayer.getPersistentData().putInt(FastTPA.LAST_SEND_TIME, 0);
    }

}
