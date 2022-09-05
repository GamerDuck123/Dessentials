package com.gamerduck.essentials.commands.player;

import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class SuicideCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("suicide")
                .requires(source -> hasPermission(source, "essentials.suicide", source.hasPermissionLevel(0)))
                .executes(ctx -> suicide(ctx)));
    }

    public static int suicide(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();
        playerEntity.kill();
        return Command.SINGLE_SUCCESS;
    }
}
