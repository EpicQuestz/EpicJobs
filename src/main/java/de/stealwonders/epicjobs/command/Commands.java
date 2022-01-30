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
import de.stealwonders.epicjobs.command.commands.CreateProjectCommand;
import org.bukkit.command.CommandSender;

import java.util.function.Function;

public class Commands {

    private EpicJobs plugin;

    private PaperCommandManager<CommandSender> manager;
    private AnnotationParser<CommandSender> annotationParser;

    public Commands(EpicJobs plugin) {
        this.plugin = plugin;

        /*
         * This is a function that will provide a command execution coordinator that parses
         * and executes commands synchronously
         */
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                CommandExecutionCoordinator.simpleCoordinator();

        // This function maps the command sender type of our choice to the bukkit command sender.
        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        try {
            manager = new PaperCommandManager<>(plugin, executionCoordinatorFunction, mapperFunction, mapperFunction);
        } catch (final Exception e) {
            plugin.getLogger().severe("Failed to initialize the command manager");
            /* Disable the plugin */
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

//        // Create the Minecraft help menu system
//        minecraftHelp = new MinecraftHelp<>(
//                "/example help",
//                player -> player,
//                manager
//        );

        // Register Brigadier mappings
        if (manager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            manager.registerBrigadier();
        }

        // Register asynchronous completions
        if (manager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
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
        annotationParser = new AnnotationParser<>(manager, CommandSender.class, commandMetaFunction);

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

//        // Register our custom caption registry so we can define exception messages for parsers
//        final BattlegroundCaptionRegistry<CommandSender> captionRegistry = new BattlegroundCaptionRegistryFactory<CommandSender>().create(); todo: add this?
//        manager.setCaptionRegistry(captionRegistry);

//        // Register custom Battleground parsers
//        manager.getParserRegistry().registerParserSupplier(TypeToken.get(Map.class), parserParameters ->
//                new MapArgument.MapParser<>());
//        manager.getParserRegistry().registerParserSupplier(TypeToken.get(Team.class), parserParameters ->
//                new TeamArgument.TeamParser<>());
//        manager.getParserRegistry().registerParserSupplier(TypeToken.get(Flag.class), parserParameters ->
//                new FlagArgument.FlagParser<>());
//        manager.getParserRegistry().registerParserSupplier(TypeToken.get(Spawn.class), parserParameters ->
//                new SpawnArgument.SpawnParser<>());
//        manager.getParserRegistry().registerParserSupplier(TypeToken.get(Gate.class), parserParameters ->
//                new GateArgument.GateParser<>());
//
//        manager.getParserRegistry().registerParserSupplier(TypeToken.get(Kit.class), parserParameters ->
//                new KitArgument.KitParser<>());
//        manager.getParserRegistry().registerParserSupplier(TypeToken.get(Warp.class), parserParameters ->
//                new WarpArgument.WarpParser<>());
//        manager.getParserRegistry().registerParserSupplier(TypeToken.get(Animation.class), parserParameters ->
//                new AnimationArgument.AnimationParser<>());

        // in-order of package structure with non parsed ones being further down

//        // Essential Commands
//        annotationParser.parse(new BroadcastCommand());
//        annotationParser.parse(new FlightCommand());
//        annotationParser.parse(new GameModeCommand());
//        annotationParser.parse(new HealthCommand());
//        annotationParser.parse(new LobbyCommand(plugin));
//        annotationParser.parse(new PingCommand());
//        annotationParser.parse(new SpeedCommand());
//        annotationParser.parse(new WarpCommand(plugin));
//        new TimeCommand(manager);
//
//        // Game Commands
//        annotationParser.parse(new EndGameCommand(plugin));
//        annotationParser.parse(new ResumeGamesCommand(plugin));
//        annotationParser.parse(new StartGameCommand(plugin));

        // Project Commands
        annotationParser.parse(new CreateProjectCommand(plugin));

    }
}
