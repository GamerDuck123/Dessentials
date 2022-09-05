package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.command.argument.EntityArgumentType.getPlayers;
import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class GodCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("god")
                .requires(source -> hasPermission(source, "essentials.god", source.hasPermissionLevel(2)))
                        .executes(ctx -> godModePersonal(ctx))
                .then(argument("targets", players())
                        .requires(source -> hasPermission(source, "essentials.god.other", source.hasPermissionLevel(2)))
                        .executes(ctx -> godModeOther(ctx, getPlayers(ctx, "targets")))));
    }

    public static int godModePersonal(CommandContext<ServerCommandSource> ctx) {
        EssentialsMain.playerRegistry.getPlayer(ctx.getSource().getPlayer()).changeGodMode();
        return Command.SINGLE_SUCCESS;
    }
    public static int godModeOther(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> players) {
        players.forEach(p -> EssentialsMain.playerRegistry.getPlayer(p).changeGodMode());
        return Command.SINGLE_SUCCESS;
    }


}
