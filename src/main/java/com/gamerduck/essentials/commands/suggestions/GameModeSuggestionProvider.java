package com.gamerduck.essentials.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameMode;

import java.util.concurrent.CompletableFuture;

public class GameModeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (GameMode mode : GameMode.values()) {
            if (mode != null) {
                builder.suggest(mode.toString().toLowerCase());
            }
        }
        return builder.buildFuture();
    }
}
