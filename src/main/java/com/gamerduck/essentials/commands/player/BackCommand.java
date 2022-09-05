package com.gamerduck.essentials.commands.player;


import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class BackCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("back")
                .requires(source -> hasPermission(source, "essentials.back", source.hasPermissionLevel(0)))
                .executes(ctx -> back(ctx)));
    }

    public static int back(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();
        EssentialsMain.playerRegistry.getPlayer(playerEntity).gotoBackLocation();
        return Command.SINGLE_SUCCESS;
    }
}
