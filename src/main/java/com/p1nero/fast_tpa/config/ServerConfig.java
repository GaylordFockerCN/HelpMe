package com.p1nero.fast_tpa.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.p1nero.fast_tpa.FastTPA;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = FastTPA.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ServerConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.ConfigValue<Integer> COOLDOWN;
    public static final ModConfigSpec.ConfigValue<Boolean> BROADCAST;
    public static final ModConfigSpec SPEC;

    static {
        COOLDOWN = BUILDER
                .comment("发送冷却(tick)")
                .comment("Cooldown(tick)")
                .defineInRange("cooldown", 600, 0, Integer.MAX_VALUE);
        BROADCAST = BUILDER
                .comment("是否全局广播救援文本 为true全局可见 为false则只有求救者可见")
                .comment("broadcast to all when tp. If true, all players can see, or only sender can see.")
                .define("broadcast", true);
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("fast_tpa").requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
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
        );
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
