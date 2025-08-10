package com.p1nero.fast_tpa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.p1nero.fast_tpa.FastTPA;
import com.p1nero.fast_tpa.network.PacketHandler;
import com.p1nero.fast_tpa.network.PacketRelay;
import com.p1nero.fast_tpa.network.packet.client.GetJoinMessagePacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FastTPA.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TeleportCommand {
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.teleport.invalidPosition"));
   @SubscribeEvent
   public static void register(RegisterCommandsEvent event) {
      CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
      dispatcher.register(Commands.literal("fast_tpa_tp")
              .requires((commandSourceStack) -> {
                 ServerPlayer serverPlayer = commandSourceStack.getPlayer();
                 return serverPlayer != null && serverPlayer.getPersistentData().getBoolean(FastTPA.CAN_ACCEPT);
              })
              .then(Commands.argument("other", EntityArgument.player())
                      .then(Commands.argument("self", EntityArgument.player())
                              .executes((commandContext -> tp(EntityArgument.getPlayer(commandContext, "other"), EntityArgument.getPlayer(commandContext, "self")))))));
   }

   public static int tp(ServerPlayer other, ServerPlayer self) throws CommandSyntaxException {
      if(other == null || self == null) {
         throw INVALID_POSITION.create();
      }
      if(other.getPersistentData().getBoolean(FastTPA.ACCEPTED)) {
         other.displayClientMessage(Component.translatable("info.fast_tpa.already"), false);
         return 0;
      }
      other.teleportTo(self.serverLevel(), self.getX(), self.getY(), self.getZ(), other.getYRot(), other.getXRot());
      other.getPersistentData().putBoolean(FastTPA.ACCEPTED, true);
      other.getPersistentData().putBoolean(FastTPA.CAN_ACCEPT, false);
      PacketRelay.sendToPlayer(PacketHandler.INSTANCE, new GetJoinMessagePacket(self.getUUID()), other);
      return 1;
   }

}
