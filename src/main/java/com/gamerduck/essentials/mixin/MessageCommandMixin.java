package com.gamerduck.essentials.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.GameModeCommand;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void register(CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo info) {
        info.cancel();
    }
}
