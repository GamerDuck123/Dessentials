package com.gamerduck.essentials.commands.handlers;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.text.Text.literal;

public interface ICommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher);

}
