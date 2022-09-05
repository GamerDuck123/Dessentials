package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class TopCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("top")
                .requires(source -> hasPermission(source, "essentials.top", source.hasPermissionLevel(2)))
                .executes(ctx -> top(ctx)));
    }
    public static int top(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();
        BlockPos pos = playerEntity.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, playerEntity.getBlockPos());
        playerEntity.teleport(pos.getX(), pos.getY(), pos.getZ());
        return Command.SINGLE_SUCCESS;
    }
}
