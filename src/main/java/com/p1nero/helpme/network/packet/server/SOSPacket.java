package com.p1nero.helpme.network.packet.server;

import com.p1nero.helpme.network.packet.BasePacket;
import dev.ftb.mods.ftbessentials.command.TPACommands;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 根据客户端的需求来播动画
 */
public record SOSPacket(Component component) implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(component);
    }

    public static SOSPacket decode(FriendlyByteBuf buf){
        return new SOSPacket(buf.readComponent());
    }

    @Override
    public void execute(Player player) {
        if(player instanceof ServerPlayer serverPlayer){
            for(ServerPlayer target: serverPlayer.getLevel().players()){
                if(!serverPlayer.equals(target)){
                    TPACommands.tpa(serverPlayer, target, true);
                }
                target.displayClientMessage(serverPlayer.getDisplayName().copy().append(component), false);
            }
            ServerLevel level = serverPlayer.getLevel();
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(level, serverPlayer, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.FIREWORK_ROCKET));
            level.addFreshEntity(fireworkrocketentity);
        }
    }
}
