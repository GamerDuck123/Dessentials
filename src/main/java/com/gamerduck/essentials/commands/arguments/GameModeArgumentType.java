package com.gamerduck.essentials.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class GameModeArgumentType implements ArgumentType<GameMode> {
    private static final Collection<String> EXAMPLES =
            Arrays.asList("s", "c", "sp", "a", "survival", "creative", "spectator", "adventure");
    private GameModeArgumentType() {
    }

    public static GameModeArgumentType gamemodes() {
        return new GameModeArgumentType();
    }

    public static GameMode getGameMode(CommandContext<?> context, String name) {
        return ((GameMode)context.getArgument(name, GameMode.class));
    }

    @Override
    public GameMode parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();
        if (!reader.canRead()) reader.skip();

        while (reader.canRead() && reader.peek() != ' ') reader.skip();

        String gamemodeString = reader.getString().substring(argBeginning, reader.getCursor());
        if (gamemodeString.equalsIgnoreCase("c")) gamemodeString = "creative";
        if (gamemodeString.equalsIgnoreCase("s")) gamemodeString = "survival";
        if (gamemodeString.equalsIgnoreCase("sp")) gamemodeString = "spectator";
        if (gamemodeString.equalsIgnoreCase("a")) gamemodeString = "adventure";
        try {
            GameMode gamemode = GameMode.valueOf(gamemodeString.toUpperCase());
            return gamemode;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SimpleCommandExceptionType(Text.literal(ex.getMessage())).createWithContext(reader);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(EXAMPLES, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
