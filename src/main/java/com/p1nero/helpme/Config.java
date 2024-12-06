package com.p1nero.helpme;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HelpMe.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<String> SOS_MESSAGE;
    public static final ForgeConfigSpec.ConfigValue<String> WHEN_JOIN;
    public static final ForgeConfigSpec SPEC;

    static {
        SOS_MESSAGE = BUILDER
                .comment("默认发送的求救文本")
                .comment("https://minecraft.tools/en/json_text.php")
                .define("sos_message", "{" + "  \"text\": \" : §l§o§6【发出救难信号】\"" + "}");

        WHEN_JOIN = BUILDER
                .comment("默认发送的支援文本")
                .comment("https://minecraft.tools/en/json_text.php")
                .define("when_join", "[\"\",{\"text\":\" :\"},{\"text\":\" \\u8fd9\",\"color\":\"dark_blue\"},{\"text\":\"\\u662f\",\"color\":\"dark_red\"},{\"text\":\"\\u4e00\\u6761\",\"color\":\"green\"},{\"text\":\"\\u975e\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"kill\"}},{\"text\":\"\\u5e38\",\"bold\":true},{\"text\":\"\\u88c5\\u903c\",\"underlined\":true},{\"text\":\"\\u7684\",\"strikethrough\":true},{\"text\":\"\\u6837\",\"strikethrough\":true,\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"This is a sample text\"}},{\"text\":\"\\u4f8b\\u6587\\u672c\",\"bold\":true,\"color\":\"#7D387D\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"This is an example text\"}},{\"text\":\"\\u231a\",\"bold\":true},{\"text\":\"https://minecraft.tools/en/json_text.php\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://minecraft.tools/en/json_text.php\"}}]");
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("help_me").requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
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
