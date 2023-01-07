package de.stealwonders.epicjobs.command.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.command.caption.EpicJobsCaptionKeys;
import de.stealwonders.epicjobs.model.job.Job;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public final class JobParser<C> implements ArgumentParser<C, Job> {

	@Override
	public @NonNull ArgumentParseResult<@NonNull Job> parse(
			final @NonNull CommandContext<@NonNull C> commandContext,
			final @NonNull Queue<@NonNull String> inputQueue
	) {
		final String input = inputQueue.peek();
		if (input == null) {
			return ArgumentParseResult.failure(new NoInputProvidedException(
					JobParser.class,
					commandContext
			));
		}

		try {
			final int id = Integer.parseInt(input); // throws NumberFormatException
			final Job job = EpicJobs.get().getStorage().getJobs().stream()
					.filter(j -> j.getId() == id)
					.findFirst()
					.orElse(null);

			if (job == null) {
				return ArgumentParseResult.failure(new JobParserException(input, commandContext));
			}
			inputQueue.remove();
			return ArgumentParseResult.success(job);
		} catch (final NumberFormatException e) {
			return ArgumentParseResult.failure(new JobParserException(input, commandContext));
		}
	}

	@Override
	public @NonNull List<@NonNull String> suggestions(
			final @NonNull CommandContext<C> commandContext,
			final @NonNull String input
	) {
		final List<String> completions = new ArrayList<>();
		EpicJobs.get().getStorage().getJobs().stream()
				.filter(job -> String.valueOf(job.getId()).startsWith(input))
				.forEach(job -> completions.add(String.valueOf(job.getId())));
		return completions;
	}

	private static final class JobParserException extends ParserException {

		private static final long serialVersionUID = -4640914893272718981L;
		private final String input;

		/**
		 * Construct a new ProjectParserException
		 *
		 * @param input   Input
		 * @param context Command context
		 */
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

		/**
		 * Get the input
		 *
		 * @return Input
		 */
		public @NonNull String getInput() {
			return this.input;
		}

	}

}