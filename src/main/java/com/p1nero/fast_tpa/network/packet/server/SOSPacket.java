package com.p1nero.fast_tpa.network.packet.server;

import com.p1nero.fast_tpa.FastTPAMod;
import com.p1nero.fast_tpa.config.ServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SOSPacket(Component message) implements CustomPacketPayload {
    public static final Type<@NotNull SOSPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(FastTPAMod.MOD_ID, "sos_packet"));
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull SOSPacket> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC,
            SOSPacket::message,
            SOSPacket::new);

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }

    public static void execute(SOSPacket packet, IPayloadContext context) {
        Player player = context.player();
        if (player instanceof ServerPlayer self) {
            int lastSendTime = self.getPersistentData().getIntOr(FastTPAMod.LAST_SEND_TIME, 0);
            int left = ServerConfig.COOLDOWN.get() - (self.tickCount - lastSendTime);
            if(lastSendTime != 0 && left > 0) {
                self.sendSystemMessage(Component.translatable("info.fast_tpa.cooldown", left / 20).withStyle(ChatFormatting.BOLD, ChatFormatting.RED));
                return;
            }
            Component component = packet.message;
            if(component == null) {
                self.sendSystemMessage(Component.literal("ERROR").withStyle(ChatFormatting.BOLD, ChatFormatting.RED));
                return;
            }
            MutableComponent formattedMessage = FastTPAMod.getFormattedName(self).append(component);
            for (ServerPlayer target : self.level().getServer().getPlayerList().getPlayers()) {
                if(target == self) {
                    continue;
                }
                target.sendSystemMessage(formattedMessage);
                MutableComponent accept = Component.translatable("info.fast_tpa.accept");
                accept.setStyle(Style.EMPTY
                        .applyFormat(ChatFormatting.GREEN)
                        .withBold(true)
                        .withClickEvent(new ClickEvent.RunCommand("/fast_tpa_tp " + target.getScoreboardName() + " " + self.getScoreboardName()))
                );
                target.sendSystemMessage(accept);
                target.getPersistentData().putBoolean(FastTPAMod.ACCEPTED, false);
                target.getPersistentData().putBoolean(FastTPAMod.CAN_ACCEPT, true);
            }
            self.sendSystemMessage(formattedMessage);
            ServerLevel level = self.level();
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(level, self, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.FIREWORK_ROCKET));
            level.addFreshEntity(fireworkrocketentity);

            self.getPersistentData().putInt(FastTPAMod.LAST_SEND_TIME, self.tickCount);
        }
    }
}
