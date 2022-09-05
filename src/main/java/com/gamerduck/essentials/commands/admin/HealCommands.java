package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.block.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Collections;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.command.argument.EntityArgumentType.getPlayers;
import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class HealCommands implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> healNode = dispatcher.register(literal("heal")
                .requires(source -> hasPermission(source, "essentials.heal", source.hasPermissionLevel(2)))
                    .executes(ctx -> heal(ctx, Collections.singleton(ctx.getSource().getPlayerOrThrow())))
                .then(argument("targets", players())
                        .executes(ctx -> heal(ctx, getPlayers(ctx, "targets")))));

        final LiteralCommandNode<ServerCommandSource> feedNode = dispatcher.register(literal("feed")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ctx -> feed(ctx, Collections.singleton(ctx.getSource().getPlayerOrThrow())))
                .then(argument("targets", players())
                        .executes(ctx -> feed(ctx, getPlayers(ctx, "targets")))));
        dispatcher.register(literal("eat").redirect(feedNode));

    }

    public static int heal(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> ent) {
        ent.forEach(e -> e.setHealth(e.getMaxHealth()));
        feed(ctx, ent);
        return Command.SINGLE_SUCCESS;
    }

    public static int feed(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> ent) {
        ent.forEach(e -> {
            e.getHungerManager().setFoodLevel(20);
            e.getHungerManager().setSaturationLevel(20);
        });
        return Command.SINGLE_SUCCESS;
    }


}
