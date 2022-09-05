package com.gamerduck.essentials.mixin.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.KickCommand;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KickCommand.class)
public class KickCommandMixin {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void register(CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo info) {
        info.cancel();
    }
}
