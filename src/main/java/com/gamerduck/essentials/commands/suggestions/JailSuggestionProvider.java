package com.gamerduck.essentials.commands.suggestions;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.storage.records.Jail;
import com.gamerduck.essentials.storage.records.Warp;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class JailSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (Jail jail : EssentialsMain.essServer.jails) {
            if (jail != null)
                builder.suggest(jail.name());
        }
        return builder.buildFuture();
    }
}