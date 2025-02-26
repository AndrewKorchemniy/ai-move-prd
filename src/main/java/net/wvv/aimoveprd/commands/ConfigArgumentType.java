package net.wvv.aimoveprd.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

public class ConfigArgumentType implements ArgumentType<ConfigArgument> {

    @Override
    public ConfigArgument parse(StringReader stringReader) throws CommandSyntaxException {
        String s = stringReader.readUnquotedString();
        var result = new ConfigArgument();

        try {
            result.type = ConfigType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Invalid config type: " + s);
        }

        return result;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var suggestions = ConfigType.values();

        for (var suggestion : suggestions) {
            if (suggestion.name().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                builder.suggest(suggestion.name().toLowerCase());
            }
        }

        return builder.buildFuture();
    }
}
