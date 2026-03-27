package com.p1nero.fast_tpa.network.packet.server;

import com.p1nero.fast_tpa.FastTPAMod;
import com.p1nero.fast_tpa.config.ServerConfig;
import com.p1nero.fast_tpa.network.packet.BasePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record SOSPacket(Component component) implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(component);
    }

    public static SOSPacket decode(FriendlyByteBuf buf) {
        return new SOSPacket(buf.readComponent());
    }

    @Override
    public void execute(Player player) {
        if (player instanceof ServerPlayer self) {
            int lastSendTime = self.getPersistentData().getInt(FastTPAMod.LAST_SEND_TIME);
            int left = ServerConfig.COOLDOWN.get() - (self.tickCount - lastSendTime);
            if(lastSendTime != 0 && left > 0) {
                self.displayClientMessage(Component.translatable("info.fast_tpa.cooldown", left / 20).withStyle(ChatFormatting.BOLD, ChatFormatting.RED), false);
                return;
            }
            MutableComponent formattedMessage = FastTPAMod.getFormattedName(self).append(component);
            for (ServerPlayer target : self.serverLevel().getServer().getPlayerList().getPlayers()) {
                if(target == self) {
                    continue;
                }
                target.displayClientMessage(formattedMessage, false);
                MutableComponent accept = Component.translatable("info.fast_tpa.accept");
                accept.setStyle(Style.EMPTY
                        .applyFormat(ChatFormatting.GREEN)
                        .withBold(true)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fast_tpa_tp " + target.getScoreboardName() + " " + self.getScoreboardName()))
                );
                target.displayClientMessage(accept, false);
                target.getPersistentData().putBoolean(FastTPAMod.ACCEPTED, false);
                target.getPersistentData().putBoolean(FastTPAMod.CAN_ACCEPT, true);
            }
            self.displayClientMessage(formattedMessage, false);
            ServerLevel level = self.serverLevel();
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(level, self, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.FIREWORK_ROCKET));
            level.addFreshEntity(fireworkrocketentity);

            self.getPersistentData().putInt(FastTPAMod.LAST_SEND_TIME, self.tickCount);
        }
    }
}
