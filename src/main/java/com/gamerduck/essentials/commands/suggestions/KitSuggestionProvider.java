package com.gamerduck.essentials.commands.suggestions;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.storage.records.Kit;
import com.gamerduck.essentials.storage.records.Warp;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class KitSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (Kit kit : EssentialsMain.essServer.kits) {
            if (kit != null) {
                builder.suggest(kit.name());
            }
        }
        return builder.buildFuture();
    }
}