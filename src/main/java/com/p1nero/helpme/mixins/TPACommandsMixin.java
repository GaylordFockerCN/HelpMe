package com.p1nero.helpme.mixins;

import com.p1nero.helpme.network.PacketHandler;
import com.p1nero.helpme.network.PacketRelay;
import com.p1nero.helpme.network.packet.client.GetJoinMessagePacket;
import dev.ftb.mods.ftbessentials.command.TPACommands;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.ftb.mods.ftbessentials.command.TPACommands.REQUESTS;

/**
 * 获取客户端自定义文本
 */
@Mixin(value = TPACommands.class, remap = false)
public class TPACommandsMixin {
    @Inject(method = "tpaccept", at = @At("HEAD"))
    private static void inject(ServerPlayer player, String id, CallbackInfoReturnable<Integer> cir){
        TPACommands.TPARequest request = REQUESTS.get(id);
            if (request != null && request.here()){
                PacketRelay.sendToPlayer(PacketHandler.INSTANCE, new GetJoinMessagePacket(), player);
            }
    }
}
