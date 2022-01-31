//package de.stealwonders.epicjobs.commands;
//
//import co.aikar.commands.BaseCommand;
//import co.aikar.commands.CommandHelp;
//import co.aikar.commands.annotation.CommandAlias;
//import co.aikar.commands.annotation.CommandCompletion;
//import co.aikar.commands.annotation.CommandPermission;
//import co.aikar.commands.annotation.Default;
//import co.aikar.commands.annotation.HelpCommand;
//import co.aikar.commands.annotation.Optional;
//import co.aikar.commands.annotation.Single;
//import co.aikar.commands.annotation.Subcommand;
//import co.aikar.commands.bukkit.contexts.OnlinePlayer;
//import de.stealwonders.epicjobs.EpicJobs;
//import de.stealwonders.epicjobs.model.project.Project;
//import de.stealwonders.epicjobs.model.project.ProjectStatus;
//import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.TextComponent;
//import net.kyori.adventure.text.event.ClickEvent;
//import net.kyori.adventure.text.event.HoverEvent;
//import net.kyori.adventure.text.format.NamedTextColor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static de.stealwonders.epicjobs.constants.Messages.*;
//
//@CommandAlias("project|projects")
//@CommandPermission("epicjobs.command.project")
//public class ProjectCommand extends BaseCommand {
//
//    private final EpicJobs plugin;
//
//    public ProjectCommand(final EpicJobs plugin) {
//        this.plugin = plugin;
//    }
//
//    @Default
//    @HelpCommand
//    public void onHelp(final CommandSender commandSender, final CommandHelp commandHelp) {
//        commandHelp.showHelp();
//    }
//

//
//    @Subcommand("create")
//    @CommandPermission("epicjobs.command.project.create")
//    public void onCreate(final Player player, @Single final String name, @Optional final Player leader) {
//        EpicJobs.newSharedChain("EpicJobs")
//            .syncFirst(() -> {
//                if (plugin.getProjectManager().getProjectByName(name) == null) {
//                    CREATING_PROJECT.sendActionbar(player, name);
//                    return true;
//                } else {
//                    CANT_CREATE_PROJECT.send(player);
//                    return false;
//                }
//            })
//            .abortIf(false)
//            .asyncFirst(() -> {
//                final Project project = (leader == null) ? plugin.getStorage().createAndLoadProject(name, player.getUniqueId(), player.getLocation(), ProjectStatus.ACTIVE) : plugin.getStorage().createAndLoadProject(name, leader.getUniqueId(), leader.getLocation(), ProjectStatus.ACTIVE);
//                plugin.getProjectManager().addProject(project);
//                return project;
//            })
//            .syncLast((project) -> {
//                final String message = (project == null) ? "§cError while creating project. Please contact an administrator." : SUCCESSFULLY_CREATED_PROJECT.toString(project.getId());
//                player.sendMessage(message);
//            })
//            .execute();
//    }
//




//

//
//    @Subcommand("complete")
//    @CommandCompletion("@active-project")
//    @CommandPermission("epicjobs.command.project.complete")
//    public void onComplete(final Player player, final Project project) {
//        EpicJobs.newSharedChain("EpicJobs")
//            .syncFirst(() -> {
//                if (!project.getProjectStatus().equals(ProjectStatus.COMPLETE)) {
//                    project.setProjectStatus(ProjectStatus.COMPLETE);
//                    ANNOUNCE_PROJECT_COMPLETION.broadcast(project.getName());
//                    return true;
//                } else {
//                    PROJECT_ALREADY_COMPLETE.send(player);
//                    return false;
//                }
//            })
//            .abortIf(false)
//            .async(() -> plugin.getStorage().updateProject(project))
//            .execute();
//    }
//
//}
