package com.gamerduck.essentials.commands.suggestions;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.storage.records.Warp;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameMode;

import java.util.concurrent.CompletableFuture;

public class WarpSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (Warp warp : EssentialsMain.essServer.warps) {
            if (warp != null) {
                builder.suggest(warp.name());
            }
        }
        return builder.buildFuture();
    }
}