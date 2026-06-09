package com.epicquestz.epicjobs.command.parser;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.caption.EpicJobsCaptionKeys;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import java.util.Locale;
import java.util.UUID;

/**
 * Resolves an {@link OfflinePlayer} by name using the plugin's
 * {@link com.epicquestz.epicjobs.user.PlayerCache}, so commands such as {@code /job assign}
 * work for players who are offline but have joined the server before.
 */
public final class OfflinePlayerParser<C> implements ArgumentParser<C, OfflinePlayer>, BlockingSuggestionProvider.Strings<C> {

	@Override
	public @NonNull ArgumentParseResult<@NonNull OfflinePlayer> parse(
			final @NonNull CommandContext<@NonNull C> commandContext,
			final @NonNull CommandInput commandInput
	) {
		final String input = commandInput.peekString();
		final UUID uuid = EpicJobs.get().getPlayerCache().getUuid(input);

		if (uuid == null) {
			return ArgumentParseResult.failure(new OfflinePlayerParseException(input, commandContext));
		}

		commandInput.readString();
		return ArgumentParseResult.success(Bukkit.getOfflinePlayer(uuid));
	}

	@Override
	public @NonNull Iterable<@NonNull String> stringSuggestions(final @NonNull CommandContext<C> commandContext, final @NonNull CommandInput input) {
		final String prefix = input.peekString().toLowerCase(Locale.ROOT);
		return EpicJobs.get().getPlayerCache().getNames().stream()
				.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(prefix))
				::iterator;
	}

	private static final class OfflinePlayerParseException extends ParserException {

		private static final long serialVersionUID = -1126724197123987456L;

		public OfflinePlayerParseException(final @NonNull String input, final @NonNull CommandContext<?> context) {
			super(
				OfflinePlayerParser.class,
				context,
				EpicJobsCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND,
				CaptionVariable.of("input", input)
			);
		}

	}

}
