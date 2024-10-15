package com.p1nero.helpme;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HelpMe.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<? extends String> SOS_MESSAGE;
    public static final ForgeConfigSpec.ConfigValue<? extends String> WHEN_JOIN;
    public static final ForgeConfigSpec SPEC;

    static {
        SOS_MESSAGE = BUILDER
                .comment("默认发送的文本")
                .define("sos_message", "{" +
                        "  \"text\": \" : 【发出救难信号】\"," +
                        "  \"color\": \"gold\"," +
                        "  \"bold\": true," +
                        "  \"italic\": true" +
                        "}");

        WHEN_JOIN = BUILDER
                .comment("默认发送的文本")
                .define("when_join", "{" +
                        "  \"text\": \"：孩子别怕，我带着魔刀千刃来救你了！\"," +
                        "  \"color\": \"blue\"," +
                        "  \"bold\": true," +
                        "  \"italic\": true" +
                        "}");
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("help_me").requires((commandSourceStack) -> commandSourceStack.hasPermission(2)));
    }

    private static int setConfig(ForgeConfigSpec.DoubleValue config, double value, CommandSourceStack stack){
        config.set(value);
        stack.sendSuccess(Component.nullToEmpty("Successfully set to : "+value), true);
        return 0;
    }

    private static int setConfig(ForgeConfigSpec.BooleanValue config, boolean value, CommandSourceStack stack){
        config.set(value);
        stack.sendSuccess(Component.nullToEmpty("Successfully set to : "+value), true);
        return 0;
    }

}
