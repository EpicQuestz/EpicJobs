package com.epicquestz.epicjobs.command.parser;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.caption.EpicJobsCaptionKeys;
import com.epicquestz.epicjobs.project.Project;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

public final class ProjectParser<C> implements ArgumentParser<C, Project>, BlockingSuggestionProvider.Strings<C> {

	@Override
	public @NonNull ArgumentParseResult<@NonNull Project> parse(
			final @NonNull CommandContext<@NonNull C> commandContext,
			final @NonNull CommandInput commandInput
	) {
		final String input = commandInput.peekString();
		try {
			final Project project = EpicJobs.get().getProjectManager().getProjectByName(input);

			if (project == null) {
				return ArgumentParseResult.failure(new ProjectParserException(input, commandContext));
			}

			commandInput.readString();
			return ArgumentParseResult.success(project);
		} catch (final NumberFormatException e) {
			return ArgumentParseResult.failure(new ProjectParserException(input, commandContext));
		}

	}

	@Override
	public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
		return EpicJobs.get().getProjectManager().getProjects().stream().map(Project::getName)::iterator;
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