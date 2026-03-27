package com.p1nero.fast_tpa.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.p1nero.fast_tpa.FastTPAMod;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = FastTPAMod.MOD_ID)
public class ClientConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.ConfigValue<@NotNull String> SOS_MESSAGE = BUILDER
            .comment("默认发送的求救文本")
            .comment("https://minecraft.tools/en/json_text.php")
            .define("sos_message", "{" + "  \"text\": \"§l§o§6【发出救难信号】\"" + "}");
    public static final ModConfigSpec.ConfigValue<@NotNull String> WHEN_JOIN = BUILDER
            .comment("默认发送的支援文本")
            .comment("https://minecraft.tools/en/json_text.php")
            .define("when_join", "[\"\",{\"text\":\"孩子别怕，我来助你！\"}]");
    public static final ModConfigSpec.ConfigValue<@NotNull String> SOUND_ID = BUILDER
            .comment("传送至对方身边时要播放的音频的id，例如：minecraft:block.end_portal.spawn")
            .define("sound_id", "");
    public static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("fast_tpa_client")
                .then(Commands.literal("sos_message")
                    .then(Commands.argument("value", ComponentArgument.textComponent(event.getBuildContext()))
                            .executes((context) -> setData(SOS_MESSAGE, FastTPAMod.toJson(ComponentArgument.getRawComponent(context, "value")), context))
                    )
                )
                .then(Commands.literal("when_join")
                        .then(Commands.argument("value", ComponentArgument.textComponent(event.getBuildContext()))
                                .executes((context) -> setData(WHEN_JOIN, FastTPAMod.toJson(ComponentArgument.getRawComponent(context, "value")), context))
                        )
                )
                .then(Commands.literal("sound_id")
                        .then(Commands.argument("value", StringArgumentType.greedyString())
                                .executes((context) -> setData(SOUND_ID, StringArgumentType.getString(context, "value"), context))
                        )
                )
        );
    }

    private static <T extends ModConfigSpec.ConfigValue<E>, E> int setData(T key, E value, CommandContext<CommandSourceStack> context) {
        CommandSourceStack stack = context.getSource();
        key.set(value);
        if(stack.getPlayer() != null){
            stack.getPlayer().sendSystemMessage(Component.literal( context.getInput() + " : SUCCESS").withStyle(ChatFormatting.GREEN));
        }
        return 0;
    }

}
