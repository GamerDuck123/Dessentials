package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.*;

@RegisterCommand
public final class BroadcastCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("broadcast")
                .requires(source -> source.hasPermissionLevel(2))
                    .then(argument("message", greedyString())
                            .executes(ctx -> broadcast(ctx.getSource(), getString(ctx, "message")))));
        dispatcher.register(literal("bc").redirect(node));
    }

    public static int broadcast(ServerCommandSource source, String message) {
        final Text text = Text.literal(message.replaceAll("&", "§")).formatted();
        source.getServer().getPlayerManager().broadcast(text, false);
        return Command.SINGLE_SUCCESS;
    }


}
