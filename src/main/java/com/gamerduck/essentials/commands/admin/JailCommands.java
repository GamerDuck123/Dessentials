package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.gamerduck.essentials.commands.suggestions.JailSuggestionProvider;
import com.gamerduck.essentials.commands.suggestions.WarpSuggestionProvider;
import com.gamerduck.essentials.storage.objects.EssServer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.argument.EntityArgumentType.getPlayers;
import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class JailCommands implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> warpsNode = dispatcher.register(literal("jails")
                .requires(source -> hasPermission(source, "essentials.jails", source.hasPermissionLevel(4)))
                .executes(ctx -> jails(ctx)));
        final LiteralCommandNode<ServerCommandSource> setNode = dispatcher.register(literal("setjail")
                .requires(source -> hasPermission(source, "essentials.setjail", source.hasPermissionLevel(4)))
                .then(argument("name", word())
                        .executes(ctx -> setJail(ctx, getString(ctx, "name")))));
        final LiteralCommandNode<ServerCommandSource> delNode = dispatcher.register(literal("deljail")
                .requires(source -> hasPermission(source, "essentials.deljail", source.hasPermissionLevel(4)))
                .then(argument("name", word()).suggests(new JailSuggestionProvider())
                        .executes(ctx -> delJail(ctx, getString(ctx, "name")))));
        final LiteralCommandNode<ServerCommandSource> jailNode = dispatcher.register(literal("jail")
                .requires(source -> hasPermission(source, "essentials.jail", source.hasPermissionLevel(4)))
                .then(argument("targets", players())
                .then(argument("name", word()).suggests(new JailSuggestionProvider())
                        .executes(ctx -> jail(ctx, getString(ctx, "name"), getPlayers(ctx, "targets"))))));
    }

    public static int setJail(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        if (EssentialsMain.essServer.isJail(name)) {
            player.sendMessage(Text.of("Jail with name already exists"));
        } else {
            EssentialsMain.essServer.addJail(name, player.getPos());
            player.sendMessage(Text.of("Jail set"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int delJail(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        if (!EssentialsMain.essServer.isJail(name)) {
            player.sendMessage(Text.of("Jail with name doesn't exist"));
        } else {
            EssentialsMain.essServer.delJail(name);
            player.sendMessage(Text.of("Jail removed"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int jails(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        player.sendMessage(Text.of(EssentialsMain.essServer.jails.toString()));
        return Command.SINGLE_SUCCESS;
    }

    public static int jail(CommandContext<ServerCommandSource> ctx, String name, Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        if (!EssentialsMain.essServer.isJail(name)) {
            player.sendMessage(Text.of("Jail with name doesnt exist"));
        } else {
            Vec3d loc = EssentialsMain.essServer.getJail(name).location();
            players.forEach(p -> {
                EssentialsMain.essServer.inJail.add(p);
                p.teleport(loc.x, loc.y, loc.z);
            });
            player.sendMessage(Text.of("Teleporting all players to jail.."));
        }
        return Command.SINGLE_SUCCESS;
    }


}
