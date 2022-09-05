package com.gamerduck.essentials.commands.player;

import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class HatCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("hat")
                .requires(source -> hasPermission(source, "essentials.hat", source.hasPermissionLevel(2)))
                        .executes(ctx -> hat(ctx)));
    }

    public static int hat(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity p = ctx.getSource().getPlayer();
        ItemStack oldHelm = p.getInventory().armor.get(3);
        p.getInventory().armor.set(3, p.getInventory().getMainHandStack());
        p.getInventory().setStack(p.getInventory().selectedSlot, oldHelm);
        return Command.SINGLE_SUCCESS;
    }

}
