package com.gamerduck.essentials.commands.admin;


import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.gamerduck.essentials.commands.suggestions.WarpSuggestionProvider;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.block.Material;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class WarpCommands implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> warpsNode = dispatcher.register(literal("warps")
                .requires(source -> hasPermission(source, "essentials.warps", source.hasPermissionLevel(0)))
                .executes(ctx -> warps(ctx)));
        final LiteralCommandNode<ServerCommandSource> setNode = dispatcher.register(literal("setwarp")
                .requires(source -> hasPermission(source, "essentials.setwarp", source.hasPermissionLevel(4)))
                        .then(argument("name", word()).suggests(new WarpSuggestionProvider())
                        .executes(ctx -> setWarp(ctx, getString(ctx, "name")))));
        final LiteralCommandNode<ServerCommandSource> delNode = dispatcher.register(literal("delwarp")
                .requires(source -> hasPermission(source, "essentials.delwarp", source.hasPermissionLevel(4)))
                .then(argument("name", word()).suggests(new WarpSuggestionProvider())
                .executes(ctx -> delWarp(ctx, getString(ctx, "name")))));
        final LiteralCommandNode<ServerCommandSource> warpNode = dispatcher.register(literal("warp")
                .requires(source -> hasPermission(source, "essentials.warp", source.hasPermissionLevel(0)))
                .then(argument("name", word()).suggests(new WarpSuggestionProvider())
                .executes(ctx -> warp(ctx, getString(ctx, "name")))));
    }

    public static int setWarp(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        if (EssentialsMain.essServer.isWarp(name)) {
            player.sendMessage(Text.of("Warp with name already exists"));
        } else {
            EssentialsMain.essServer.setWarp(name, player.getPos());
            player.sendMessage(Text.of("Warp set"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int delWarp(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        if (!EssentialsMain.essServer.isWarp(name)) {
            player.sendMessage(Text.of("Warp with name doesn't exist"));
        } else {
            EssentialsMain.essServer.delWarp(name);
            player.sendMessage(Text.of("Warp removed"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int warps(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        player.sendMessage(Text.of(EssentialsMain.essServer.warps.toString()));
        return Command.SINGLE_SUCCESS;
    }

    public static int warp(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        if (!EssentialsMain.essServer.isWarp(name)) {
            player.sendMessage(Text.of("Warp with name doesnt exist"));
        } else {
            EssentialsMain.playerRegistry.getPlayer(player).teleport(EssentialsMain.essServer.getWarp(name).location(), 5);
            player.sendMessage(Text.of("Teleporting to warp"));
        }
        return Command.SINGLE_SUCCESS;
    }


}