package com.gamerduck.essentials.commands.admin;

import com.gamerduck.essentials.commands.handlers.ICommand;
import com.gamerduck.essentials.commands.handlers.RegisterCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
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

import static net.minecraft.server.command.CommandManager.literal;

@RegisterCommand
public final class ModsCommand implements ICommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("mods")
                .requires(source -> hasPermission(source, "essentials.mods", source.hasPermissionLevel(4)))
                .executes(ctx -> mods(ctx)));
    }

    public static int mods(CommandContext<ServerCommandSource> ctx) {
        Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods();
        StringBuilder builder = new StringBuilder("Mods (%%%modsamount%%%): ");
        AtomicInteger modNumber = new AtomicInteger(0);
        mods.forEach(m -> {
            AtomicBoolean shouldSend = new AtomicBoolean(true);
            m.getMetadata().getAuthors().forEach(p -> shouldSend.set(p.getName().equalsIgnoreCase("FabricMC")));
            if (shouldSend.get()) return;
            else {
                builder.append("§a" + m.getMetadata().getName() + "§f, ");
                modNumber.getAndIncrement();
            }
        });
        ctx.getSource().sendMessage(Text.of(builder.toString().replaceAll("%%%modsamount%%%", String.valueOf(modNumber.get()))));
        return Command.SINGLE_SUCCESS;
    }
}
