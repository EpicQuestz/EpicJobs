package com.epicquestz.epicjobs.command;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.caption.EpicJobsCaptionProvider;
import com.epicquestz.epicjobs.command.commands.SuggestionProvider;
import com.epicquestz.epicjobs.command.commands.job.JobCommand;
import com.epicquestz.epicjobs.command.commands.job.JobEditCommand;
import com.epicquestz.epicjobs.command.commands.job.JobListAllCommand;
import com.epicquestz.epicjobs.command.commands.job.JobListDoneCommand;
import com.epicquestz.epicjobs.command.commands.job.TeleportJobCommand;
import com.epicquestz.epicjobs.command.commands.project.ProjectCommand;
import com.epicquestz.epicjobs.command.commands.project.ProjectEditCommand;
import com.epicquestz.epicjobs.command.parser.JobParser;
import com.epicquestz.epicjobs.command.parser.ProjectParser;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.project.Project;
import io.leangen.geantyref.TypeToken;
import com.epicquestz.epicjobs.command.parser.OfflinePlayerParser;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

public class Commands {

	public Commands(final EpicJobs plugin) {

		// Native, Brigadier-backed command manager. Its sender type is the Paper
		// CommandSourceStack; we adapt it to CommandSender/Player below.
		final PaperCommandManager<CommandSourceStack> manager = PaperCommandManager.builder()
			.executionCoordinator(ExecutionCoordinator.simpleCoordinator())
			.buildOnEnable(plugin);

		// The manager's sender is CommandSourceStack, but our command and suggestion
		// methods declare CommandSender / Player. Register injectors so cloud can supply
		// those from the source stack without changing every command signature.
		manager.parameterInjectorRegistry().registerInjector(
			CommandSender.class,
			(context, annotations) -> context.sender().getSender()
		);
		manager.parameterInjectorRegistry().registerInjector(
			Player.class,
			(context, annotations) -> {
				final CommandSender sender = context.sender().getSender();
				if (sender instanceof Player player) {
					return player;
				}
				throw new IllegalStateException("This command can only be used by a player.");
			}
		);

		// Register our custom caption provider, so we can define exception messages for parsers
		manager.captionRegistry().registerProvider(new EpicJobsCaptionProvider<>());

		// Register custom EpicJobs parsers
		manager.parserRegistry().registerParserSupplier(TypeToken.get(Project.class), parserParameters -> new ProjectParser<>());
		manager.parserRegistry().registerParserSupplier(TypeToken.get(Job.class), parserParameters -> new JobParser<>());
		// Resolve OfflinePlayer from our own name cache (covers players who are offline but joined before)
		manager.parserRegistry().registerParserSupplier(TypeToken.get(OfflinePlayer.class), parserParameters -> new OfflinePlayerParser<>());

		// Register and parse commands and suggestions
		final AnnotationParser<CommandSourceStack> annotationParser = new AnnotationParser<>(manager, CommandSourceStack.class);

		annotationParser.parse(new SuggestionProvider(plugin)); // suggestions must be parsed first

		annotationParser.parse(new JobCommand(plugin));
		annotationParser.parse(new JobEditCommand(plugin));
		annotationParser.parse(new JobListAllCommand(plugin));
		annotationParser.parse(new JobListDoneCommand(plugin));
		annotationParser.parse(new TeleportJobCommand(plugin));

		annotationParser.parse(new ProjectCommand(plugin));
		annotationParser.parse(new ProjectEditCommand(plugin));

	}
}
