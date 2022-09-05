package com.gamerduck.essentials.commands.player;

import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;

import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class PingCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("ping")
                .requires(source -> hasPermission(source, "essentials.ping", source.hasPermissionLevel(0)))
                .executes(ctx -> ping(ctx)));
    }

    public static int ping(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();
        playerEntity.sendMessage(Text.literal("Ping: " + playerEntity.pingMilliseconds + "ms"));
        return Command.SINGLE_SUCCESS;
    }

}
