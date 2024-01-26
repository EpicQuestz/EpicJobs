package com.epicquestz.epicjobs.command.parser;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.caption.EpicJobsCaptionKeys;
import com.epicquestz.epicjobs.job.Job;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

public final class JobParser<C> implements ArgumentParser<C, Job>, BlockingSuggestionProvider.Strings<C> {

	@Override
	public @NonNull ArgumentParseResult<@NonNull Job> parse(
			final @NonNull CommandContext<@NonNull C> commandContext,
			final @NonNull CommandInput commandInput
	) {
		final String input = commandInput.peekString();
		try {
			final int id = Integer.parseInt(input); // throws NumberFormatException
			final Job job = EpicJobs.get().getJobManager().getJobById(id);

			if (job == null) {
				return ArgumentParseResult.failure(new JobParserException(input, commandContext));
			}

			commandInput.readString();
			return ArgumentParseResult.success(job);
		} catch (final NumberFormatException e) {
			return ArgumentParseResult.failure(new JobParserException(input, commandContext));
		}

	}

	@Override
	public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
		return EpicJobs.get().getJobManager().getJobs().stream().map(Job::getId).map(String::valueOf)::iterator; // todo: filter by input
	}

	private static final class JobParserException extends ParserException {

		private static final long serialVersionUID = 5066274206463292993L;
		private final String input;

		public JobParserException(
			final @NonNull String input,
			final @NonNull CommandContext<?> context
		) {
			super(
				JobParser.class,
				context,
				EpicJobsCaptionKeys.ARGUMENT_PARSE_FAILURE_JOB_NOT_FOUND,
				CaptionVariable.of("input", input)
			);
			this.input = input;
		}

		public @NonNull String getInput() {
			return this.input;
		}

	}

}