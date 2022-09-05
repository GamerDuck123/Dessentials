package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class SpeedCommands implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("speed")
                .requires(source -> hasPermission(source, "essentials.speed", source.hasPermissionLevel(4)))
                        .then(argument("amount", floatArg(0, 10))
                        .executes(ctx -> speed(ctx, getFloat(ctx,"amount")) )));
        final LiteralCommandNode<ServerCommandSource> flyNode = dispatcher.register(literal("flyspeed")
                .requires(source -> hasPermission(source, "essentials.speed", source.hasPermissionLevel(4)))
                .then(argument("amount", floatArg(0, 10))
                .executes(ctx -> flySpeed(ctx, getFloat(ctx,"amount")))));
        final LiteralCommandNode<ServerCommandSource> walkNode = dispatcher.register(literal("walkspeed")
                .requires(source -> hasPermission(source, "essentials.speed", source.hasPermissionLevel(4)))
                .then(argument("amount", floatArg(0, 10))
                .executes(ctx -> walkSpeed(ctx, getFloat(ctx,"amount")))));
    }

    public static int speed(CommandContext<ServerCommandSource> ctx, Float amount) {
        ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();
        EssentialsMain.playerRegistry.getPlayer(playerEntity).changeSpeed(amount);
        return Command.SINGLE_SUCCESS;
    }
    public static int flySpeed(CommandContext<ServerCommandSource> ctx, Float amount) {
        ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();
        EssentialsMain.playerRegistry.getPlayer(playerEntity).changeSpeed(amount, true);
        return Command.SINGLE_SUCCESS;
    }
    public static int walkSpeed(CommandContext<ServerCommandSource> ctx, Float amount) {
        ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();
        EssentialsMain.playerRegistry.getPlayer(playerEntity).changeSpeed(amount, false);
        return Command.SINGLE_SUCCESS;
    }
}
