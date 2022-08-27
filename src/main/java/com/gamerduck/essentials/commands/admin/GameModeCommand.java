package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.*;

import static com.gamerduck.essentials.commands.arguments.GameModeArgumentType.gamemodes;
import static com.gamerduck.essentials.commands.arguments.GameModeArgumentType.getGameMode;
import static net.minecraft.command.argument.EntityArgumentType.getPlayers;
import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class GameModeCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node =  dispatcher.register(literal("gamemode")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("gamemode", gamemodes())
                .executes(ctx -> gamemode(ctx, Collections.singleton(ctx.getSource().getPlayerOrThrow()), getGameMode(ctx, "gamemode")))
                .then(argument("target", players())
                        .executes(ctx -> gamemode(ctx, getPlayers(ctx, "target"), getGameMode(ctx, "gamemode"))))));
        dispatcher.register(literal("gm").redirect(node));

        dispatcher.register(literal("gmc")
                .requires(source -> source.hasPermissionLevel(2))
                    .executes(ctx -> gamemode(ctx, Collections.singleton(ctx.getSource().getPlayerOrThrow()), GameMode.CREATIVE))
                        .then(argument("targets", players())
                            .executes(ctx -> gamemode(ctx, getPlayers(ctx, "targets"), GameMode.CREATIVE))));

        dispatcher.register(literal("gma")
                .requires(source -> source.hasPermissionLevel(2))
                    .executes(ctx -> gamemode(ctx, Collections.singleton(ctx.getSource().getPlayerOrThrow()), GameMode.ADVENTURE))
                        .then(argument("targets", players())
                           .executes(ctx -> gamemode(ctx, getPlayers(ctx, "targets"), GameMode.ADVENTURE))));

        dispatcher.register(literal("gms")
                .requires(source -> source.hasPermissionLevel(2))
                    .executes(ctx -> gamemode(ctx, Collections.singleton(ctx.getSource().getPlayerOrThrow()), GameMode.SURVIVAL))
                        .then(argument("targets", players())
                            .executes(ctx -> gamemode(ctx, getPlayers(ctx, "targets"), GameMode.SURVIVAL))));

        dispatcher.register(literal("gmsp")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ctx -> gamemode(ctx, Collections.singleton(ctx.getSource().getPlayerOrThrow()), GameMode.SPECTATOR))
                .then(argument("targets", players())
                        .executes(ctx -> gamemode(ctx, getPlayers(ctx, "targets"), GameMode.SPECTATOR))));
    }

    public static int gamemode(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> targets, GameMode gamemode) {
        final ServerCommandSource source = ctx.getSource();
        final PlayerEntity self = source.getPlayer();
        targets.forEach(t -> t.changeGameMode(gamemode));
        return Command.SINGLE_SUCCESS;
    }
}