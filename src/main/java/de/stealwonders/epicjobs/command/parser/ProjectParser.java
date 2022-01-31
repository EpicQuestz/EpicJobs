package de.stealwonders.epicjobs.command.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.command.caption.EpicJobsCaptionKeys;
import de.stealwonders.epicjobs.model.project.Project;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public final class ProjectParser<C> implements ArgumentParser<C, Project> {

    @Override
    public @NonNull ArgumentParseResult<@NonNull Project> parse(
            final @NonNull CommandContext<@NonNull C> commandContext,
            final @NonNull Queue<@NonNull String> inputQueue
    ) {
        final String input = inputQueue.peek();
        if (input == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(
                    ProjectParser.class,
                    commandContext
            ));
        }

        final EpicJobs plugin = JavaPlugin.getPlugin(EpicJobs.class);
        final Project project = plugin.getProjectManager().getProjectByName(input);
        if (project == null) {
            return ArgumentParseResult.failure(new ProjectParserException(input, commandContext));
        }
        inputQueue.remove();
        return ArgumentParseResult.success(project);
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(
            final @NonNull CommandContext<C> commandContext,
            final @NonNull String input
    ) {
        final List<String> completions = new ArrayList<>();
        final EpicJobs plugin = JavaPlugin.getPlugin(EpicJobs.class);
        for (Project value : plugin.getProjectManager().getProjects()) {
            completions.add(value.getName());
        }
        return completions;
    }

    private static final class ProjectParserException extends ParserException {

        private static final long serialVersionUID = 8167729917402542614L;
        private final String input;

        /**
         * Construct a new ProjectParserException
         *
         * @param input   Input
         * @param context Command context
         */
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
