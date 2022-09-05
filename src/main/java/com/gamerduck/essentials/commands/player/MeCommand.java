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
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.Formatting.ITALIC;
import static net.minecraft.util.Formatting.DARK_PURPLE;

@RegisterCommand
public final class MeCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("me")
                .requires(source -> hasPermission(source, "essentials.me", true))
                .then(argument("message", greedyString())
                        .executes(ctx -> broadcast(ctx, getString(ctx, "message")))));
    }

    public static int broadcast(CommandContext<ServerCommandSource> ctx, String message) {
        ServerPlayerEntity p = ctx.getSource().getPlayer();
        final Text text = Text.literal("* " + p.getDisplayName().getString() + " " + message).formatted(ITALIC, DARK_PURPLE);
        ctx.getSource().getServer().getPlayerManager().broadcast(text, false);
        return Command.SINGLE_SUCCESS;
    }
}
