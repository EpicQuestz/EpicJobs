package com.epicquestz.epicjobs.command;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.caption.EpicJobsCaptionProvider;
import com.epicquestz.epicjobs.command.commands.job.JobCommand;
import com.epicquestz.epicjobs.command.commands.job.JobListAllCommand;
import com.epicquestz.epicjobs.command.commands.job.JobListDoneCommand;
import com.epicquestz.epicjobs.command.commands.project.ProjectCommand;
import com.epicquestz.epicjobs.command.commands.job.TeleportJobCommand;
import com.epicquestz.epicjobs.command.commands.project.ProjectEditCommand;
import com.epicquestz.epicjobs.command.parser.JobParser;
import com.epicquestz.epicjobs.command.parser.ProjectParser;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.project.Project;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

public class Commands {

	public Commands(EpicJobs plugin) {

		final PaperCommandManager<CommandSender> manager = new PaperCommandManager<>(
			plugin,
			ExecutionCoordinator.simpleCoordinator(),
			SenderMapper.identity()
		);

		// Register Brigadier mappings
		if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
			manager.registerBrigadier();
		}

		// Register asynchronous completions
		if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
			manager.registerAsynchronousCompletions();
		}

		// Register our custom caption provider, so we can define exception messages for parsers
		manager.captionRegistry().registerProvider(new EpicJobsCaptionProvider<>());

		// Register custom EpicJobs parsers
		manager.parserRegistry().registerParserSupplier(TypeToken.get(Project.class), parserParameters -> new ProjectParser<>());
		manager.parserRegistry().registerParserSupplier(TypeToken.get(Job.class), parserParameters -> new JobParser<>());

		// Register Commands
		final AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(manager, CommandSender.class);
		annotationParser.parse(new JobCommand(plugin));
		annotationParser.parse(new JobListAllCommand(plugin));
		annotationParser.parse(new JobListDoneCommand(plugin));
		annotationParser.parse(new TeleportJobCommand(plugin));

		annotationParser.parse(new ProjectCommand(plugin));
		annotationParser.parse(new ProjectEditCommand(plugin));

	}
}
