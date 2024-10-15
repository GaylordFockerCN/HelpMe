package com.p1nero.helpme;

import com.p1nero.helpme.network.PacketHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(HelpMe.MOD_ID)
public class HelpMe {
    public static final String MOD_ID = "help_me";

    public HelpMe(){
        PacketHandler.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }

}
