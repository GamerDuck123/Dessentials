package com.gamerduck.essentials.commands.player;


import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
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
public final class NickCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("nick")
                .requires(source -> hasPermission(source, "essentials.nick", source.hasPermissionLevel(2)))
                        .then(argument("nick", word())
                .executes(ctx -> nick(ctx, getString(ctx, "nick")))));
    }

    public static int nick(CommandContext<ServerCommandSource> ctx, String nick) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = ctx.getSource().getPlayerOrThrow();
        EssentialsMain.playerRegistry.getPlayer(playerEntity).setNickName(Text.of(nick));
        return Command.SINGLE_SUCCESS;
    }
}
