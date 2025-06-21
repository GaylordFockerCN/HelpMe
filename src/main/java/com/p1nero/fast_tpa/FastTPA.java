package com.p1nero.fast_tpa;

import com.p1nero.fast_tpa.config.ClientConfig;
import com.p1nero.fast_tpa.config.ServerConfig;
import com.p1nero.fast_tpa.network.PacketHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(FastTPA.MOD_ID)
public class FastTPA {
    public static final String MOD_ID = "fast_tpa";
    public static final String ACCEPTED = "accepted";
    public static final String LAST_SEND_TIME = "last_send_time";
    public FastTPA(){
        PacketHandler.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ServerConfig.SPEC);
    }
}
