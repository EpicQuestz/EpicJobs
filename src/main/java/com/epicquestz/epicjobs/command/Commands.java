package com.epicquestz.epicjobs.command;

import cloud.commandframework.SenderMapper;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.ExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.caption.EpicJobsCaptionRegistry;
import com.epicquestz.epicjobs.command.caption.EpicJobsCaptionRegistryFactory;
import com.epicquestz.epicjobs.command.commands.TeleportJobCommand;
import com.epicquestz.epicjobs.command.parser.JobParser;
import com.epicquestz.epicjobs.command.parser.ProjectParser;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.project.Project;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;

public class Commands {

	public Commands(EpicJobs plugin) {

		final PaperCommandManager<CommandSender> manager = new PaperCommandManager<>(
			plugin,
			ExecutionCoordinator.simpleCoordinator(),
			SenderMapper.identity()
		);

		if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
			manager.registerBrigadier();
		} else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
			// Use Paper async completions API (see Javadoc for why we don't use this with Brigadier)
			manager.registerAsynchronousCompletions();
		}

		// Register our custom caption registry, so we can define exception messages for parsers
		final EpicJobsCaptionRegistry<CommandSender> captionRegistry = new EpicJobsCaptionRegistryFactory<CommandSender>().create();
		manager.captionRegistry(captionRegistry);

		// Register custom EpicJobs parsers
		manager.parserRegistry().registerParserSupplier(TypeToken.get(Project.class), parserParameters -> new ProjectParser<>());
		manager.parserRegistry().registerParserSupplier(TypeToken.get(Job.class), parserParameters -> new JobParser<>());

		// Register Commands
		final AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(manager, CommandSender.class);
		annotationParser.parse(new TeleportJobCommand(plugin));

	}
}
