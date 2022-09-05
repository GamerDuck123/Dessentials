package com.gamerduck.essentials.commands.player;


import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

import static net.minecraft.command.argument.EntityArgumentType.getPlayers;
import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class SpawnCommands implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("spawn")
                .requires(source -> hasPermission(source, "essentials.spawn", source.hasPermissionLevel(0)))
                .executes(ctx -> spawn(ctx))
                .then(argument("targets", players())
                        .requires(source -> hasPermission(source, "essentials.spawn.other", source.hasPermissionLevel(4)))
                        .executes(ctx -> spawnOther(ctx, getPlayers(ctx, "targets")))));
        final LiteralCommandNode<ServerCommandSource> setNode = dispatcher.register(literal("setspawn")
                .requires(source -> hasPermission(source, "essentials.setspawn", source.hasPermissionLevel(4)))
                .executes(ctx -> setSpawn(ctx)));
    }

    public static int spawnOther(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> players) {
        players.forEach((p) -> EssentialsMain.playerRegistry.getPlayer(p).teleport(EssentialsMain.essServer.getSpawn(), 0));
        return Command.SINGLE_SUCCESS;
    }
    public static int spawn(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity p = ctx.getSource().getPlayer();
        EssentialsMain.playerRegistry.getPlayer(p).teleport(EssentialsMain.essServer.getSpawn(), 5);
        return Command.SINGLE_SUCCESS;
    }
    public static int setSpawn(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity p = ctx.getSource().getPlayer();
        EssentialsMain.essServer.setSpawn(p.getPos());
        return Command.SINGLE_SUCCESS;
    }

}
