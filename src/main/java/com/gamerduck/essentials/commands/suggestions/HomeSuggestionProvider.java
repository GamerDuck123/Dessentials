package com.gamerduck.essentials.commands.suggestions;

import com.gamerduck.essentials.EssentialsMain;
import com.gamerduck.essentials.storage.records.Home;
import com.gamerduck.essentials.storage.records.Kit;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;

public class HomeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        for (Home home : EssentialsMain.playerRegistry.getPlayer(player).homes) {
            if (home != null) {
                builder.suggest(home.name());
            }
        }
        return builder.buildFuture();
    }
}