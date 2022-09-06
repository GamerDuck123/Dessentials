package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class RepairCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("repair")
                .requires(source -> hasPermission(source, "essentials.repair", source.hasPermissionLevel(2)))
                .executes(ctx -> repair(ctx))
                .then(argument("all", word())
                        .requires(source -> hasPermission(source, "essentials.repair.all", source.hasPermissionLevel(2)))
                        .executes(ctx -> repairAll(ctx))));
        dispatcher.register(literal("fix").redirect(node));

        final LiteralCommandNode<ServerCommandSource> nodeAll = dispatcher.register(literal("repairall")
                .requires(source -> hasPermission(source, "essentials.repair.all", source.hasPermissionLevel(2)))
                .executes(ctx -> repairAll(ctx)));
        dispatcher.register(literal("fixall").redirect(nodeAll));

    }
    public static int repair(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();
        playerEntity.getHandItems().forEach(i -> {
            if (i.isDamageable()) i.setDamage(0);
        });
        return Command.SINGLE_SUCCESS;
    }
    public static int repairAll(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity playerEntity = ctx.getSource().getPlayer();
        for (int i = 0; i < playerEntity.getInventory().size(); i++) {
            if (playerEntity.getInventory().getStack(i).isDamageable())
                playerEntity.getInventory().getStack(i).setDamage(0);
        }
        return Command.SINGLE_SUCCESS;
    }
}
