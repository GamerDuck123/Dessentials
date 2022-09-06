package com.gamerduck.essentials.commands.player;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class RulesCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> setNode = dispatcher.register(literal("rules")
                .requires(source -> hasPermission(source, "essentials.rules", source.hasPermissionLevel(0)))
                .executes(ctx -> rules(ctx, 1))
                .then(argument("page", integer())
                        .executes(ctx -> rules(ctx, getInteger(ctx, "page")))));
    }

    public static int rules(CommandContext<ServerCommandSource> ctx, Integer page) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        player.sendMessage(EssentialsMain.essServer.rules.get(page));
        return Command.SINGLE_SUCCESS;
    }

}