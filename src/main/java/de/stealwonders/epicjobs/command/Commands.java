package de.stealwonders.epicjobs.command;

import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.command.caption.EpicJobsCaptionRegistry;
import de.stealwonders.epicjobs.command.caption.EpicJobsCaptionRegistryFactory;
import de.stealwonders.epicjobs.command.commands.job.CreateJobCommand;
import de.stealwonders.epicjobs.command.commands.job.ListJobsCommand;
import de.stealwonders.epicjobs.command.commands.project.CreateProjectCommand;
import de.stealwonders.epicjobs.command.commands.project.EditProjectCommand;
import de.stealwonders.epicjobs.command.commands.project.ListProjectsCommand;
import de.stealwonders.epicjobs.command.commands.project.TeleportProjectCommand;
import de.stealwonders.epicjobs.command.parser.ProjectParser;
import de.stealwonders.epicjobs.model.project.Project;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;

import java.util.function.Function;

public class Commands {

	public Commands(EpicJobs plugin) {

		/*
		 * This is a function that will provide a command execution coordinator that parses
		 * and executes commands synchronously
		 */
		final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
				CommandExecutionCoordinator.simpleCoordinator();

		// This function maps the command sender type of our choice to the bukkit command sender.
		final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
		PaperCommandManager<CommandSender> manager;
		try {
			manager = new PaperCommandManager<>(plugin, executionCoordinatorFunction, mapperFunction, mapperFunction);
		} catch (final Exception e) {
			plugin.getLogger().severe("Failed to initialize the command manager");
			plugin.getServer().getPluginManager().disablePlugin(plugin); // disable the plugin on failure
			return;
		}

//        // Create the Minecraft help menu system
//        minecraftHelp = new MinecraftHelp<>(
//                "/example help",
//                player -> player,
//                manager
//        );

		// Register Brigadier mappings
		if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
			manager.registerBrigadier();
		}

		// Register asynchronous completions
		if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
			manager.registerAsynchronousCompletions();
		}

//        /*
//         * Create the confirmation manager. This allows us to require certain commands to be
//         * confirmed before they can be executed
//         */
//        confirmationManager = new CommandConfirmationManager<>(30L, TimeUnit.SECONDS,
//                /* Action when confirmation is required */ context -> context.getCommandContext().getSender().sendMessage(
//                ChatColor.RED + "Confirmation required. Confirm using /example confirm."),
//                /* Action when no confirmation is pending */ sender -> sender.sendMessage(
//                ChatColor.RED + "You don't have any pending commands.")
//        );
//
//        // Register the confirmation processor. This will enable confirmations for commands that require it
//        confirmationManager.registerConfirmationProcessor(manager);

		// This will allow you to decorate commands with descriptions
		final Function<ParserParameters, CommandMeta> commandMetaFunction = parserParameters ->
				CommandMeta.simple()
						.with(CommandMeta.DESCRIPTION, parserParameters.get(StandardParameters.DESCRIPTION, "No description"))
						.build();
		AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(manager, CommandSender.class, commandMetaFunction);

//        // Override the default exception handlers
//        new MinecraftExceptionHandler<CommandSender>()
//                .withInvalidSyntaxHandler()
//                .withInvalidSenderHandler()
//                .withNoPermissionHandler()
//                .withArgumentParsingHandler()
//                .withDecorator(
//                        component -> Component.text()
//                                .append(Component.text("[", NamedTextColor.DARK_AQUA))
//                                .append(Component.text("Empire War", NamedTextColor.AQUA).decoration(TextDecoration.BOLD, true))
//                                .append(Component.text("] ", NamedTextColor.DARK_AQUA))
//                                .append(component).build()
//                ).apply(manager, player -> player);

		// Register our custom caption registry, so we can define exception messages for parsers
		final EpicJobsCaptionRegistry<CommandSender> captionRegistry = new EpicJobsCaptionRegistryFactory<CommandSender>().create();
		manager.captionRegistry(captionRegistry);

		// Register custom EpicJobs parsers
		manager.parserRegistry().registerParserSupplier(TypeToken.get(Project.class), parserParameters -> new ProjectParser<>());

		// Job Commands
		annotationParser.parse(new CreateJobCommand(plugin));
		annotationParser.parse(new ListJobsCommand(plugin));

		// Project Commands
		annotationParser.parse(new CreateProjectCommand(plugin));
		annotationParser.parse(new EditProjectCommand(plugin));
		annotationParser.parse(new ListProjectsCommand(plugin));
		annotationParser.parse(new TeleportProjectCommand());
	}

}