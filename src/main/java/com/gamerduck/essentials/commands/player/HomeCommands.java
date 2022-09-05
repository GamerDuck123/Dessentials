package com.gamerduck.essentials.commands.player;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.gamerduck.essentials.commands.suggestions.HomeSuggestionProvider;
import com.gamerduck.essentials.commands.suggestions.WarpSuggestionProvider;
import com.gamerduck.essentials.storage.objects.EssPlayer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class HomeCommands implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> homesNode = dispatcher.register(literal("homes")
                .requires(source -> hasPermission(source, "essentials.homes", source.hasPermissionLevel(0)))
                .executes(ctx -> homes(ctx)));
        final LiteralCommandNode<ServerCommandSource> setNode = dispatcher.register(literal("sethome")
                .requires(source -> hasPermission(source, "essentials.sethome", source.hasPermissionLevel(0)))
                .then(argument("name", word()).suggests(new HomeSuggestionProvider())
                        .executes(ctx -> setHome(ctx, getString(ctx, "name")))));
        final LiteralCommandNode<ServerCommandSource> delNode = dispatcher.register(literal("delhome")
                .requires(source -> hasPermission(source, "essentials.delhome", source.hasPermissionLevel(0)))
                .then(argument("name", word()).suggests(new HomeSuggestionProvider())
                        .executes(ctx -> delHome(ctx, getString(ctx, "name")))));
        final LiteralCommandNode<ServerCommandSource> homeNode = dispatcher.register(literal("home")
                .requires(source -> hasPermission(source, "essentials.home", source.hasPermissionLevel(0)))
                .then(argument("name", word()).suggests(new HomeSuggestionProvider())
                        .executes(ctx -> home(ctx, getString(ctx, "name")))));
    }

    public static int setHome(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        EssPlayer p = EssentialsMain.playerRegistry.getPlayer(player);
        if (p.isHome(name)) {
            player.sendMessage(Text.of("Home with name already exists"));
        } else {
            p.setHome(name, player.getPos());
            player.sendMessage(Text.of("Home set"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int delHome(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        EssPlayer p = EssentialsMain.playerRegistry.getPlayer(player);
        if (!p.isHome(name)) {
            player.sendMessage(Text.of("Home with name doesn't exist"));
        } else {
            p.delHome(name);
            player.sendMessage(Text.of("Home removed"));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int homes(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        EssPlayer p = EssentialsMain.playerRegistry.getPlayer(player);
        player.sendMessage(Text.of(p.homes.toString()));
        return Command.SINGLE_SUCCESS;
    }

    public static int home(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        EssPlayer p = EssentialsMain.playerRegistry.getPlayer(player);
        if (!p.isHome(name)) {
            player.sendMessage(Text.of("Home with name doesnt exist"));
        } else {
            p.teleport(p.getHome(name).location(), 5);
            player.sendMessage(Text.of("Teleporting to home"));
        }
        return Command.SINGLE_SUCCESS;
    }

}
