package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.gamerduck.essentials.commands.suggestions.GameModeSuggestionProvider;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.*;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.argument.EntityArgumentType.getPlayers;
import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class GameModeCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node =  dispatcher.register(literal("gamemode")
                .requires(source -> hasPermission(source, "essentials.gamemode", source.hasPermissionLevel(2)))
                .then(argument("gamemode", word()).suggests(new GameModeSuggestionProvider())
                .executes(ctx -> gamemode(ctx, Collections.singleton(ctx.getSource().getPlayerOrThrow()), GameMode.valueOf(getString(ctx, "gamemode").toUpperCase())))
                .then(argument("target", players())
                        .executes(ctx -> gamemode(ctx, getPlayers(ctx, "target"), GameMode.valueOf(getString(ctx, "gamemode").toUpperCase()))))));
        dispatcher.register(literal("gm").redirect(node));

        dispatcher.register(literal("gmc")
                .requires(source -> hasPermission(source, "essentials.gamemode", source.hasPermissionLevel(2)))
                    .executes(ctx -> gamemode(ctx, Collections.singleton(ctx.getSource().getPlayerOrThrow()), GameMode.CREATIVE))
                        .then(argument("targets", players())
                            .executes(ctx -> gamemode(ctx, getPlayers(ctx, "targets"), GameMode.CREATIVE))));

        dispatcher.register(literal("gma")
                .requires(source -> hasPermission(source, "essentials.gamemode", source.hasPermissionLevel(2)))
                    .executes(ctx -> gamemode(ctx, Collections.singleton(ctx.getSource().getPlayerOrThrow()), GameMode.ADVENTURE))
                        .then(argument("targets", players())
                           .executes(ctx -> gamemode(ctx, getPlayers(ctx, "targets"), GameMode.ADVENTURE))));

        dispatcher.register(literal("gms")
                .requires(source -> hasPermission(source, "essentials.gamemode", source.hasPermissionLevel(2)))
                    .executes(ctx -> gamemode(ctx, Collections.singleton(ctx.getSource().getPlayerOrThrow()), GameMode.SURVIVAL))
                        .then(argument("targets", players())
                            .executes(ctx -> gamemode(ctx, getPlayers(ctx, "targets"), GameMode.SURVIVAL))));

        dispatcher.register(literal("gmsp")
                .requires(source -> hasPermission(source, "essentials.gamemode", source.hasPermissionLevel(2)))
                .executes(ctx -> gamemode(ctx, Collections.singleton(ctx.getSource().getPlayerOrThrow()), GameMode.SPECTATOR))
                .then(argument("targets", players())
                        .executes(ctx -> gamemode(ctx, getPlayers(ctx, "targets"), GameMode.SPECTATOR))));
    }

    public static int gamemode(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> targets, GameMode gamemode) {
        targets.forEach(t -> t.changeGameMode(gamemode));
        return Command.SINGLE_SUCCESS;
    }
}