package com.epicquestz.epicjobs.command.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.context.CommandInput;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.caption.EpicJobsCaptionKeys;
import com.epicquestz.epicjobs.project.Project;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ProjectParser<C> implements ArgumentParser<C, Project> {

	@Override
	public @NonNull ArgumentParseResult<@NonNull Project> parse(
			final @NonNull CommandContext<@NonNull C> commandContext,
			final @NonNull CommandInput commandInput
	) {
		final String input = commandInput.peekString();
		try {
			final int id = Integer.parseInt(input); // throws NumberFormatException
			final Project project = EpicJobs.get().getProjectManager().getProjectById(id);

			if (project == null) {
				return ArgumentParseResult.failure(new ProjectParserException(input, commandContext));
			}

			commandInput.readString();
			return ArgumentParseResult.success(project);
		} catch (final NumberFormatException e) {
			return ArgumentParseResult.failure(new ProjectParserException(input, commandContext));
		}

	}

	private static final class ProjectParserException extends ParserException {

		private static final long serialVersionUID = 6172092073821948183L;
		private final String input;

		public ProjectParserException(
			final @NonNull String input,
			final @NonNull CommandContext<?> context
		) {
			super(
				ProjectParser.class,
				context,
				EpicJobsCaptionKeys.ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND,
				CaptionVariable.of("input", input)
			);
			this.input = input;
		}

		public @NonNull String getInput() {
			return this.input;
		}

	}

}