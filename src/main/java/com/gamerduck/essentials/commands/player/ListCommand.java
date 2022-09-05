package com.gamerduck.essentials.commands.player;

import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class ListCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("list")
                .requires(source -> hasPermission(source, "essentials.list", source.hasPermissionLevel(0)))
                        .executes(ctx -> list(ctx)));
    }

    public static int list(CommandContext<ServerCommandSource> ctx) {
        StringBuilder list = new StringBuilder("Online Players: ");
        Arrays.stream(ctx.getSource().getServer().getPlayerManager().getPlayerNames()).forEach(p -> list.append(p));
        ctx.getSource().sendMessage(Text.literal(list.toString()));
        return Command.SINGLE_SUCCESS;
    }

}
