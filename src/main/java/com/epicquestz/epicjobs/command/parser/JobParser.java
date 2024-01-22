package com.epicquestz.epicjobs.command.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.context.CommandInput;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.caption.EpicJobsCaptionKeys;
import com.epicquestz.epicjobs.job.Job;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class JobParser<C> implements ArgumentParser<C, Job> {

	@Override
	public @NonNull ArgumentParseResult<@NonNull Job> parse(
			final @NonNull CommandContext<@NonNull C> commandContext,
			final @NonNull CommandInput commandInput
	) {
		final String input = commandInput.peekString();
		try {
			final int id = Integer.parseInt(input); // throws NumberFormatException
			System.out.println("id: " + id);
			final Job job = EpicJobs.get().getJobManager().getJobById(id);
			System.out.println("job: " + job);

			if (job == null) {
				return ArgumentParseResult.failure(new JobParserException(input, commandContext));
			}

			return ArgumentParseResult.success(job);
		} catch (final NumberFormatException e) {
			return ArgumentParseResult.failure(new JobParserException(input, commandContext));
		}

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