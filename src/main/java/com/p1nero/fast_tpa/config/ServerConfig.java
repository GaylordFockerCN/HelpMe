package com.p1nero.fast_tpa.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.p1nero.fast_tpa.FastTPAMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = FastTPAMod.MOD_ID)
public class ServerConfig {
    private static boolean broadcast, playSound;
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.ConfigValue<Integer> COOLDOWN = BUILDER
            .comment("发送冷却(tick)")
            .comment("Cooldown(tick)")
            .defineInRange("cooldown", 600, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.ConfigValue<Boolean>
            BROADCAST = BUILDER
            .comment("是否全局广播救援文本 为true全局可见 为false则只有求救者可见")
            .comment("Broadcast to all when tp. If true, all players can see, or only sender can see.")
            .define("broadcast", true);
    public static final ModConfigSpec.ConfigValue<String> FORMAT = BUILDER
            .comment("默认名字播报格式 如：<Steve>")
            .comment("Format of name. Default: <Steve>")
            .define("format", "[%s] : ");
    public static final ModConfigSpec.ConfigValue<Boolean> PLAY_SOUND = BUILDER
            .comment("是否播放救援音频")
            .comment("Enable playing sound when tp")
            .define("play_sound", true);
    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean shouldBroadcast() {
        return broadcast;
    }

    public static boolean shouldPlaySound() {
        return playSound;
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("fast_tpa_server").requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("cooldown")
                    .then(Commands.argument("value", IntegerArgumentType.integer())
                            .executes((context) -> setData(COOLDOWN, IntegerArgumentType.getInteger(context, "value"), context))
                    )
                )
                .then(Commands.literal("broadcast")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes((context) -> setData(BROADCAST, BoolArgumentType.getBool(context, "value"), context))
                        )
                )
                .then(Commands.literal("play_sound")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes((context) -> setData(PLAY_SOUND, BoolArgumentType.getBool(context, "value"), context))
                        )
                )
                .then(Commands.literal("format")
                        .then(Commands.argument("value", StringArgumentType.greedyString())
                                .executes((context) -> setData(FORMAT, StringArgumentType.getString(context, "value"), context))
                        )
                )
        );
    }

    @SubscribeEvent
    public static void onModConfig(final ModConfigEvent.Reloading event) {
        broadcast = BROADCAST.get();
        playSound = PLAY_SOUND.get();
    }

    private static <T extends ModConfigSpec.ConfigValue<E>, E> int setData(T key, E value, CommandContext<CommandSourceStack> context) {
        CommandSourceStack stack = context.getSource();
        key.set(value);
        if(stack.getPlayer() != null){
            stack.getPlayer().displayClientMessage(Component.literal( context.getInput() + " : SUCCESS"), false);
        }
        return 0;
    }

}
