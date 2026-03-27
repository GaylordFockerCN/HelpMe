package com.p1nero.fast_tpa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.p1nero.fast_tpa.FastTPAMod;
import com.p1nero.fast_tpa.network.packet.client.GetJoinMessagePacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;

@EventBusSubscriber(modid = FastTPAMod.MOD_ID)
public class TeleportCommand {
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.teleport.invalidPosition"));
   @SubscribeEvent
   public static void register(RegisterCommandsEvent event) {
      CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
      dispatcher.register(Commands.literal("fast_tpa_tp")
              .requires((commandSourceStack) -> {
                 ServerPlayer serverPlayer = commandSourceStack.getPlayer();
                 return serverPlayer != null && serverPlayer.getPersistentData().getBooleanOr(FastTPAMod.CAN_ACCEPT, true);
              })
              .then(Commands.argument("other", EntityArgument.player())
                      .then(Commands.argument("self", EntityArgument.player())
                              .executes((commandContext -> tp(EntityArgument.getPlayer(commandContext, "other"), EntityArgument.getPlayer(commandContext, "self")))))));
   }

   public static int tp(ServerPlayer other, ServerPlayer self) throws CommandSyntaxException {
      if(other == null || self == null) {
         throw INVALID_POSITION.create();
      }
      if(other.getPersistentData().getBooleanOr(FastTPAMod.ACCEPTED, false)) {
         other.sendSystemMessage(Component.translatable("info.fast_tpa.already"));
         return 0;
      }
      other.teleportTo(self.level(), self.getX(), self.getY(), self.getZ(), Set.of(), other.getYRot(), other.getXRot(), false);
      other.getPersistentData().putBoolean(FastTPAMod.ACCEPTED, true);
      other.getPersistentData().putBoolean(FastTPAMod.CAN_ACCEPT, false);
      PacketDistributor.sendToPlayer(other, new GetJoinMessagePacket(self.getUUID().toString()));
      return 1;
   }

}
