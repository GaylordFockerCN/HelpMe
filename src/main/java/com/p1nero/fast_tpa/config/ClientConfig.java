package com.p1nero.fast_tpa.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.p1nero.fast_tpa.FastTPAMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FastTPAMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<String> SOS_MESSAGE;
    public static final ForgeConfigSpec.ConfigValue<String> WHEN_JOIN;
    public static final ForgeConfigSpec.ConfigValue<String> SOUND_ID;
    public static final ForgeConfigSpec SPEC;

    static {
        SOS_MESSAGE = BUILDER
                .comment("默认发送的求救文本")
                .comment("https://minecraft.tools/en/json_text.php")
                .define("sos_message", "{" + "  \"text\": \"§l§o§6【发出救难信号】\"" + "}");

        WHEN_JOIN = BUILDER
                .comment("默认发送的支援文本")
                .comment("https://minecraft.tools/en/json_text.php")
                .define("when_join", "[\"\",{\"text\":\"孩子别怕，我来助你！\""+"}]");

        SOUND_ID = BUILDER
                .comment("传送至对方身边时要播放的音频的id，例如：minecraft:block.end_portal.spawn")
                .define("sound_id", "");

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("fast_tpa_client")
                .then(Commands.literal("sos_message")
                    .then(Commands.argument("value", ComponentArgument.textComponent())
                            .executes((context) -> setData(SOS_MESSAGE, Component.Serializer.toJson(ComponentArgument.getComponent(context, "value")), context))
                    )
                )
                .then(Commands.literal("when_join")
                        .then(Commands.argument("value", ComponentArgument.textComponent())
                                .executes((context) -> setData(WHEN_JOIN, Component.Serializer.toJson(ComponentArgument.getComponent(context, "value")), context))
                        )
                )
                .then(Commands.literal("sound_id")
                        .then(Commands.argument("value", StringArgumentType.greedyString())
                                .executes((context) -> setData(SOUND_ID, StringArgumentType.getString(context, "value"), context))
                        )
                )
        );
    }

    private static <T extends ForgeConfigSpec.ConfigValue<E>, E> int setData(T key, E value, CommandContext<CommandSourceStack> context) {
        CommandSourceStack stack = context.getSource();
        key.set(value);
        if(stack.getPlayer() != null){
            stack.getPlayer().displayClientMessage(Component.literal( context.getInput() + " : SUCCESS"), false);
        }
        return 0;
    }

}
