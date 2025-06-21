package com.p1nero.fast_tpa.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.p1nero.fast_tpa.FastTPA;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = FastTPA.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ClientConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.ConfigValue<String> SOS_MESSAGE;
    public static final ModConfigSpec.ConfigValue<String> WHEN_JOIN;
    public static final ModConfigSpec SPEC;

    static {
        SOS_MESSAGE = BUILDER
                .comment("默认发送的求救文本")
                .comment("https://minecraft.tools/en/json_text.php")
                .define("sos_message", "{" + "  \"text\": \"§l§o§6【发出救难信号】\"" + "}");

        WHEN_JOIN = BUILDER
                .comment("默认发送的支援文本")
                .comment("https://minecraft.tools/en/json_text.php")
                .define("when_join", "[\"\",{\"text\":\"\\u5b69\",\"color\":\"aqua\"},{\"text\":\"\\u5b50\",\"color\":\"red\"},{\"text\":\"\\u522b\"},{\"text\":\"\\u6015\",\"color\":\"dark_green\"},{\"text\":\"\\uff0c\",\"color\":\"dark_gray\"},{\"text\":\"\\u6211\",\"color\":\"gold\"},{\"text\":\"\\u6765\",\"color\":\"green\"},{\"text\":\"\\u52a9\",\"color\":\"dark_blue\"},{\"text\":\"\\u4f60\\uff01\",\"color\":\"yellow\"},{\"text\":\"[\\u70b9\\u51fb\\u67e5\\u770bjson\\u6587\\u672c\\u751f\\u6210\\u5668]\",\"color\":\"gray\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://minecraft.tools/en/json_text.php\"}},{\"text\":\" \\n\\u63d0\\u793a\\uff1a\\u7528fast_tpa_client\\u7cfb\\u5217\\u547d\\u4ee4\\u4e5f\\u53ef\\u4ee5\\u76f4\\u63a5\\u8fdb\\u884c\\u4fee\\u6539\\uff01\"}]");
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("fast_tpa_client")
                .then(Commands.literal("sos_message")
                    .then(Commands.argument("value", ComponentArgument.textComponent(event.getBuildContext()))
                            .executes((context) -> setData(SOS_MESSAGE, Component.Serializer.toJson(ComponentArgument.getComponent(context, "value"), context.getSource().registryAccess()), context))
                    )
                )
                .then(Commands.literal("when_join")
                        .then(Commands.argument("value", ComponentArgument.textComponent(event.getBuildContext()))
                                .executes((context) -> setData(WHEN_JOIN, Component.Serializer.toJson(ComponentArgument.getComponent(context, "value"), context.getSource().registryAccess()), context))
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
