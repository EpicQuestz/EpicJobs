package com.epicquestz.epicjobs.command.commands.job;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.constants.SkullHeads;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobCategory;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.project.ProjectStatus;
import com.epicquestz.epicjobs.user.EpicJobsPlayer;
import com.epicquestz.epicjobs.utils.ItemStackBuilder;
import com.epicquestz.epicjobs.utils.JobItemHelper;
import com.epicquestz.epicjobs.utils.MenuHelper;
import com.epicquestz.epicjobs.utils.Utils;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.epicquestz.epicjobs.constants.Messages.ANNOUNCE_JOB_ABANDONMENT;
import static com.epicquestz.epicjobs.constants.Messages.ANNOUNCE_JOB_DONE;
import static com.epicquestz.epicjobs.constants.Messages.ANNOUNCE_JOB_REOPEN;
import static com.epicquestz.epicjobs.constants.Messages.ANNOUNCE_JOB_TAKEN;
import static com.epicquestz.epicjobs.constants.Messages.CREATING_JOB;
import static com.epicquestz.epicjobs.constants.Messages.HAS_ASSIGNED_JOB;
import static com.epicquestz.epicjobs.constants.Messages.HAS_BEEN_ASSIGNED_JOB;
import static com.epicquestz.epicjobs.constants.Messages.JOB_CANT_BE_ABANDONED;
import static com.epicquestz.epicjobs.constants.Messages.JOB_CANT_BE_ASSIGNED;
import static com.epicquestz.epicjobs.constants.Messages.JOB_CANT_BE_COMPLETE;
import static com.epicquestz.epicjobs.constants.Messages.JOB_CANT_BE_UNASSIGNED;
import static com.epicquestz.epicjobs.constants.Messages.JOB_COMPLETED;
import static com.epicquestz.epicjobs.constants.Messages.JOB_DOESNT_EXIST;
import static com.epicquestz.epicjobs.constants.Messages.JOB_HAS_TO_BE_ACTIVE;
import static com.epicquestz.epicjobs.constants.Messages.JOB_NOT_DONE;
import static com.epicquestz.epicjobs.constants.Messages.JOB_NOT_OPEN;
import static com.epicquestz.epicjobs.constants.Messages.JOB_REOPEN;
import static com.epicquestz.epicjobs.constants.Messages.PLAYER_HASNT_CLAIMED_JOB;
import static com.epicquestz.epicjobs.constants.Messages.PLAYER_HAS_MULITPLE_JOBS;
import static com.epicquestz.epicjobs.constants.Messages.PLAYER_HAS_NO_ACTIVE_JOBS;
import static com.epicquestz.epicjobs.constants.Messages.PLAYER_HAS_NO_JOBS;
import static com.epicquestz.epicjobs.constants.Messages.PROJECT_ALREADY_COMPLETE;
import static com.epicquestz.epicjobs.constants.Messages.REMOVING_JOB;
import static com.epicquestz.epicjobs.constants.Messages.SUCCESSFULLY_CREATED_JOB;
import static com.epicquestz.epicjobs.constants.Messages.SUCCESSFULLY_REMOVED_JOB;

public class JobCommand {

	private static final ItemStack BACK_BUTTON = Utils.getSkull(SkullHeads.OAK_WOOD_ARROW_LEFT.getBase64(), "§f§lBack");

	private final EpicJobs plugin;

	public JobCommand(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@CommandDescription("List jobs")
	@Permission(CommandPermissions.LIST_JOBS)
	@Command("job|jobs list|ls")
	public void onList(final @NonNull Player player) {
		final List<Job> jobs = plugin.getJobManager().getJobs();
		sendProjectMenu(player);
	}

	@CommandDescription("List jobs near you")
	@Permission(CommandPermissions.LIST_JOBS)
	@Command("job|jobs list|ls near [radius]")
	public void onListNear(final @NonNull Player player,
						   @Argument(value = "radius", description = "Radius") @Default(value = "32") final @NonNull int radius)
	{
		final List<Job> jobs = plugin.getJobManager().getJobs().stream()
            .filter(job -> job.getJobStatus().equals(JobStatus.OPEN))
            .filter(job -> job.getLocation().getWorld().equals(player.getWorld()))
            .filter(job -> job.getLocation().distanceSquared(player.getLocation()) < radius * radius)
            .collect(Collectors.toList());
        sendJobMenu(player, "Available jobs (range " + radius + ")", null, jobs);
	}

	private void sendProjectMenu(final Player player) {
        final GuiItem mainMenuItem = new GuiItem(BACK_BUTTON, inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            sendProjectMenu(player);
        });
        final List<GuiItem> guiItems = new ArrayList<>();
        final List<Project> projects = plugin.getProjectManager().getProjects().stream().filter(project -> project.getProjectStatus().equals(ProjectStatus.ACTIVE)).toList();
        for (final Project project : projects) {
            final ItemStack itemStack = new ItemStackBuilder(Material.SCAFFOLDING)
                .withName("§f§l" + project.getName())
                .withLore("§7Shift-click to teleport")
                .withLore("§f§lLeader: §f" + Utils.getPlayerHolderText(project.getLeader()))
                .build();
            final GuiItem guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
                inventoryClickEvent.setResult(Event.Result.DENY);
                switch (inventoryClickEvent.getClick()) {
                    case SHIFT_LEFT:
                    case SHIFT_RIGHT:
                        project.teleport(player);
                        break;
                    case LEFT:
                    case RIGHT:
                        final List<Job> jobs = plugin.getJobManager().getJobs().stream()
                            .filter(job -> job.getProject().equals(project))
                            .filter(job -> job.getJobStatus().equals(JobStatus.OPEN))
                            .collect(Collectors.toList());
                        sendJobMenu(player, "Available Jobs", mainMenuItem, jobs);
                        break;
                }
            });
            guiItems.add(guiItem);
        }
        final ChestGui gui = MenuHelper.getPaginatedSelectionGui("Current Projects", guiItems);
        gui.show(player);
    }

	private void sendJobMenu(final Player player, final String title, final GuiItem mainMenuItem, final List<Job> jobs) {
		final List<GuiItem> guiItems = new ArrayList<>();
		for (final Job job : jobs) {
			final ItemStack itemStack = JobItemHelper.getJobItem(job, "§7Shift-click to §lclaim", JobItemHelper.InfoType.PROJECT, JobItemHelper.InfoType.CATEGORY, JobItemHelper.InfoType.STATUS, JobItemHelper.InfoType.DESCRIPTION, JobItemHelper.InfoType.CREATOR);
			final GuiItem guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
				inventoryClickEvent.setResult(Event.Result.DENY);
				switch (inventoryClickEvent.getClick()) {
					case SHIFT_LEFT:
					case SHIFT_RIGHT:
						player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
						player.getOpenInventory().close();
						Bukkit.dispatchCommand(player, "job claim " + job.getId());
						break;
					case LEFT:
						job.teleport(player);
						break;
					case RIGHT:
						player.sendMessage(job.getDescription());
						break;
				}
			});
			guiItems.add(guiItem);
		}

		final ItemStack infoBook = new ItemStackBuilder(Material.BOOK)
            .withName("§f§lInformation")
            .withLore("§7§lClaim §7job by using shift-click")
            .withLore("§7§lTeleport §7by using left-click")
            .withLore("§7To §lview job info §7right-click")
            .build();

        final ChestGui gui = MenuHelper.getPaginatedGui(title, guiItems, mainMenuItem, infoBook);
        gui.show(player);
	}

	@CommandDescription("List your jobs")
	@Permission(CommandPermissions.LIST_JOBS)
	@Command("job|jobs mine")
	public void onMine(final @NonNull Player player) {
		onListMine(player);
	}

	@CommandDescription("List your jobs")
	@Permission(CommandPermissions.LIST_JOBS)
	@Command("job|jobs list|ls mine")
	public void onListMine(final @NonNull Player player) {
		final Optional<EpicJobsPlayer> epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
        epicJobsPlayer.ifPresent(jobsPlayer -> sendStatusSelectionMenu(player, jobsPlayer));
	}

	private void sendStatusSelectionMenu(final Player player, final EpicJobsPlayer epicJobsPlayer) {
        final GuiItem mainMenuItem = new GuiItem(BACK_BUTTON, inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            sendStatusSelectionMenu(player, epicJobsPlayer);
        });

        final GuiItem projectItem = new GuiItem(new ItemStackBuilder(Material.WRITABLE_BOOK).withName("§f§lActive Jobs").build(), inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            final List<Job> jobs = epicJobsPlayer.getJobs().stream().filter(job -> job.getJobStatus().equals(JobStatus.TAKEN) || job.getJobStatus().equals(JobStatus.DONE)).collect(Collectors.toList());
            sendMyJobMenu(player, "Your Jobs", mainMenuItem, jobs);
        });

        final GuiItem statusItem = new GuiItem(new ItemStackBuilder(Material.COMPOSTER).withName("§f§lCompleted Jobs").build(), inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            final List<Job> jobs = epicJobsPlayer.getJobs().stream().filter(job -> job.getJobStatus().equals(JobStatus.COMPLETE)).collect(Collectors.toList());
            sendMyJobMenu(player, "Your Jobs", mainMenuItem, jobs);
        });

        final ChestGui gui = MenuHelper.getStaticSelectionGui("Select Job Status", projectItem, statusItem);
        gui.show(player);
    }

	private void sendMyJobMenu(final Player player, final String title, final GuiItem mainMenuItem, final List<Job> jobs) {
        final List<GuiItem> guiItems = new ArrayList<>();
        for (final Job job : jobs) {
            GuiItem guiItem = null;
            switch (job.getJobStatus()) {
                case TAKEN: {
                    final ItemStack itemStack = JobItemHelper.getJobItem(job, "§7Shift left-click to mark §ldone\n§7Shift right-click to mark §labandon", JobItemHelper.InfoType.PROJECT, JobItemHelper.InfoType.CATEGORY, JobItemHelper.InfoType.STATUS, JobItemHelper.InfoType.DESCRIPTION, JobItemHelper.InfoType.CREATOR);
                    guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
                        inventoryClickEvent.setResult(Event.Result.DENY);
                        switch (inventoryClickEvent.getClick()) {
                            case SHIFT_LEFT:
                                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                                player.getOpenInventory().close();
                                Bukkit.dispatchCommand(player, "job done " + job.getId());
                                break;
                            case SHIFT_RIGHT:
                                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                                player.getOpenInventory().close();
                                Bukkit.dispatchCommand(player, "job abandon " + job.getId());
                                break;
                            case LEFT:
                                job.teleport(player);
                                break;
                            case RIGHT:
                                player.sendMessage(job.getDescription());
                                break;
                        }
                    });
                } break;
                case DONE: {
                    final ItemStack itemStack = JobItemHelper.getJobItem(job, "§7Shift right-click to mark §labandon", JobItemHelper.InfoType.PROJECT, JobItemHelper.InfoType.CATEGORY, JobItemHelper.InfoType.STATUS, JobItemHelper.InfoType.DESCRIPTION, JobItemHelper.InfoType.CREATOR);
                    guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
                        inventoryClickEvent.setResult(Event.Result.DENY);
                        switch (inventoryClickEvent.getClick()) {
                            case SHIFT_LEFT:
                            case SHIFT_RIGHT:
                                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                                player.getOpenInventory().close();
                                Bukkit.dispatchCommand(player, "job abandon " + job.getId());
                                break;
                            case LEFT:
                                job.teleport(player);
                                break;
                            case RIGHT:
                                player.sendMessage(job.getDescription());
                                break;
                        }
                    });
                } break;
                case COMPLETE: {
                    final ItemStack itemStack = JobItemHelper.getJobItem(job, "§7Shift-click to §lteleport", JobItemHelper.InfoType.PROJECT, JobItemHelper.InfoType.CATEGORY, JobItemHelper.InfoType.STATUS, JobItemHelper.InfoType.DESCRIPTION, JobItemHelper.InfoType.CREATOR);
                    guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
                        inventoryClickEvent.setResult(Event.Result.DENY);
                        switch (inventoryClickEvent.getClick()) {
                            case SHIFT_LEFT:
                            case SHIFT_RIGHT:
                                job.teleport(player);
                                break;
                            case LEFT:
                            case RIGHT:
                                player.sendMessage(job.getDescription());
                                break;
                        }
                    });
                } break;
            }
            guiItems.add(guiItem);
        }

        final ItemStack infoBook = new ItemStackBuilder(Material.BOOK)
            .withName("§f§lInformation")
            .withLore("§7None :-)")
            .build();

        final ChestGui gui = MenuHelper.getPaginatedGui(title, guiItems, mainMenuItem, infoBook);
        gui.show(player);
    }

//	@CommandDescription("List jobs by project")
//	@Command("job|jobs list|ls project <project> [status] [category]")
//	public void onListProject(final @NonNull CommandSender sender,
//							  @Argument(value = "project", description = "Project") final @NonNull Project project,
//							  @Argument(value = "status", description = "Status") @Default(value = "OPEN") final @Nullable JobStatus jobStatus,
//							  @Argument(value = "category", description = "Category") @Default(value = "ALL") final @Nullable JobCategory jobCategory)
//	{
//		Stream<Job> jobStream = plugin.getJobManager().getJobs().stream().filter(job -> job.getProject().equals(project));
//        if (jobStatus != null)
//            jobStream = jobStream.filter(job -> job.getJobStatus().equals(jobStatus));
//        if (jobCategory != null)
//            jobStream = jobStream.filter(job -> job.getJobCategory().equals(jobCategory));
//        final List<Job> jobs = jobStream.limit(20).toList();
////        sendJobList(commandSender, jobs);
//	}
//
//	@CommandDescription("List jobs by status")
//	@Command("job|jobs list|ls status <status>")
//	public void onListStatus(final @NonNull CommandSender sender,
//							 @Argument(value = "status", description = "Status") final @NonNull JobStatus jobStatus)
//	{
//		final List<Job> jobs = plugin.getJobManager().getJobs().stream()
//			.filter(job -> job.getJobStatus().equals(jobStatus))
//			.limit(20)
//			.toList();
////		sendJobList(commandSender, jobs);
//	}
//
//	@CommandDescription("List jobs by category")
//	@Command("job|jobs list|ls category <category>")
//	public void onListCategory(final @NonNull CommandSender sender,
//							   @Argument(value = "category", description = "Category") final @NonNull JobCategory jobCategory)
//	{
//		final List<Job> jobs = plugin.getJobManager().getJobs().stream()
//			.filter(job -> job.getJobCategory().equals(jobCategory))
//			.limit(20)
//			.toList();
////		sendJobList(commandSender, jobs);
//	}

	@CommandDescription("Show job info")
	@Permission(CommandPermissions.JOB_INFO)
	@Command("job|jobs info <job>")
	public void onInfo(final @NonNull CommandSender sender,
					   @Argument(value = "job", description = "Job") final @NonNull Job job) {
		final TextComponent text = Component.text()
			.content("Job #" + job.getId() + " @ ").color(NamedTextColor.GOLD)
			.append(Component.text(
				String.format("[%s x:%s y:%s z:%s]\n",
					job.getLocation().getWorld().getName(),
					job.getLocation().getBlockX(),
					job.getLocation().getBlockY(),
					job.getLocation().getBlockZ()
				), NamedTextColor.AQUA).hoverEvent(HoverEvent.showText(Component.text("Click to teleport!"))).clickEvent(ClickEvent.runCommand("/job teleport " + job.getId())))
			.append(Component.text("Project: ", NamedTextColor.GOLD)).append(Component.text(job.getProject().getName(), NamedTextColor.YELLOW))
			.append(Component.text(" Category: ", NamedTextColor.GOLD)).append(Component.text(job.getJobCategory().toString(), NamedTextColor.YELLOW))
			.append(Component.text(" Status: ", NamedTextColor.GOLD)).append(Component.text(job.getJobStatus().toString() + "\n", NamedTextColor.YELLOW))
			.append(Component.text("Leader: ", NamedTextColor.GOLD)).append(Component.text(Utils.getPlayerHolderText(job.getCreator()), NamedTextColor.YELLOW))
			.append(Component.text(" Claimant: ", NamedTextColor.GOLD)).append(Component.text(Utils.getPlayerHolderText(job.getClaimant()) + "\n", NamedTextColor.YELLOW))
			.append(Component.text("Description: ", NamedTextColor.GOLD)).append(Component.text(job.getDescription(), NamedTextColor.YELLOW))
			.build();
		sender.sendMessage("");
		sender.sendMessage(text);
		sender.sendMessage("");
	}

	@CommandDescription("Claim a job")
	@Permission(CommandPermissions.CLAIM_JOB)
	@Command("job|jobs claim|c [job]")
	public void onClaim(final @NonNull Player player,
						@Argument(value = "job", description = "Job", suggestions = "open-job") final @Nullable Job job) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                final Optional<EpicJobsPlayer> epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
                if (job == null) {
                    JOB_DOESNT_EXIST.send(player);
                    return false;
                } else {
                    if (job.getJobStatus().equals(JobStatus.OPEN)) {
                        if (epicJobsPlayer.isPresent()) {
                            job.claim(epicJobsPlayer.get());
                            ANNOUNCE_JOB_TAKEN.broadcast(player.getName(), job.getId());
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        JOB_NOT_OPEN.send(player);
                        return false;
                    }
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateJob(job))
            .execute();
	}

	@CommandDescription("Abandon a job")
	@Permission(CommandPermissions.ABANDON_JOB)
	@Command("job|jobs abandon [job]")
	public void onAbandon(final @NonNull Player player,
						  @Argument(value = "job", description = "Job", suggestions = "player-job") final @Nullable Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                final Optional<EpicJobsPlayer> optional = plugin.getEpicJobsPlayer(player.getUniqueId());
                if (optional.isEmpty()) return null;
				final EpicJobsPlayer epicJobsPlayer = optional.get();
				final List<Job> playerJobs = epicJobsPlayer.getJobs().stream().filter(j -> j.getJobStatus().equals(JobStatus.TAKEN) || j.getJobStatus().equals(JobStatus.DONE)).toList();

				Job jobEdited = null;
				if (job != null) {
					if (playerJobs.contains(job)) {
						jobEdited = job;
					} else {
						JOB_CANT_BE_ABANDONED.send(player);
					}
				}

				if (job == null) {
					if (playerJobs.size() == 1) {
						jobEdited = playerJobs.get(0);
					} else {
						if (playerJobs.isEmpty()) {
							PLAYER_HAS_NO_JOBS.send(player);
						} else {
							PLAYER_HAS_MULITPLE_JOBS.send(player);
						}
					}
				}

				if (jobEdited != null) {
					jobEdited.abandon(epicJobsPlayer);
					ANNOUNCE_JOB_ABANDONMENT.broadcast(player.getName(), jobEdited.getId());
				}
                return jobEdited;
            })
            .abortIfNull()
            .asyncLast((jobEdited) -> plugin.getStorageImplementation().updateJob(jobEdited))
            .execute();
	}

	@CommandDescription("Mark a job as done")
	@Permission(CommandPermissions.DONE_JOB)
	@Command("job|jobs done|d [job]")
	public void onDone(final @NonNull Player player,
					   @Argument(value = "job", description = "Job", suggestions = "player-job") final @Nullable Job job) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                final Optional<EpicJobsPlayer> optional = plugin.getEpicJobsPlayer(player.getUniqueId());
				if (optional.isEmpty()) {
					player.sendMessage("§cCould not find your profile. Please contact an administrator.");
					return null;
				}
                final EpicJobsPlayer epicJobsPlayer = optional.get();
                final List<Job> jobs = epicJobsPlayer.getActiveJobs();
                Job jobEdited = null;

                if (job == null) {
                    if (jobs.size() == 1) {
                            jobEdited = jobs.get(0);
                        if (jobEdited.getJobStatus().equals(JobStatus.TAKEN)) {
                            jobEdited.setJobStatus(JobStatus.DONE);
                            ANNOUNCE_JOB_DONE.broadcast(player.getName(), jobs.get(0).getId());
                        } else {
                            JOB_HAS_TO_BE_ACTIVE.send(player);
                        }
                    } else if (jobs.isEmpty()) {
                        PLAYER_HAS_NO_ACTIVE_JOBS.send(player);
                    } else {
                        PLAYER_HAS_MULITPLE_JOBS.send(player);
                    }
                } else {
                    if (jobs.contains(job)) {
                        if (job.getJobStatus().equals(JobStatus.TAKEN)) {
                            job.setJobStatus(JobStatus.DONE);
                            ANNOUNCE_JOB_DONE.broadcast(player.getName(), job.getId());
                            jobEdited = job;
                        } else {
                            JOB_HAS_TO_BE_ACTIVE.send(player);
                        }
                    } else {
                        PLAYER_HASNT_CLAIMED_JOB.send(player);
                    }
                }
                return jobEdited;
            })
            .abortIfNull()
            .asyncLast((jobedited) -> plugin.getStorageImplementation().updateJob(jobedited))
            .execute();
	}

	@CommandDescription("Mark a job as complete")
	@Permission(CommandPermissions.COMPLETE_JOB)
	@Command("job|jobs complete <job>")
	public void onComplete(final @NonNull Player player,
						   @Argument(value = "job", description = "Job", suggestions = "player-job") final @NonNull Job job) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (job.getJobStatus().equals(JobStatus.DONE)) {
                    job.setJobStatus(JobStatus.COMPLETE);
                    JOB_COMPLETED.send(player, job.getId());
                    return true;
                } else {
                    JOB_CANT_BE_COMPLETE.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateJob(job))
            .execute();
	}

	@CommandDescription("Reopen a job")
	@Permission(CommandPermissions.REOPEN_JOB)
	@Command("job|jobs reopen <job>")
	public void onReopen(final @NonNull Player player,
						 @Argument(value = "job", description = "Job") final @NonNull Job job) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                switch (job.getJobStatus()) {
                    case COMPLETE:
                    case TAKEN:
                        job.setJobStatus(JobStatus.OPEN);
                        job.setClaimant(null);
                        final Optional<EpicJobsPlayer> epicJobsPlayer = plugin.getEpicJobsPlayer(job.getClaimant());
                        epicJobsPlayer.ifPresent(jobsPlayer -> jobsPlayer.removeJob(job));
                        ANNOUNCE_JOB_REOPEN.send(player, player.getName(), job.getId());
                        return true;
                    case DONE:
                        job.setJobStatus(JobStatus.TAKEN);
                        final PlayerProfile profile = Bukkit.createProfile(job.getClaimant());
                        if (profile.completeFromCache()) {
                            JOB_REOPEN.send(player, job.getId(), profile.getName());
						} else {
                            JOB_REOPEN.send(player, job.getId(), "<unknown>");
                        }
						return true;
					default:
                        JOB_NOT_DONE.send(player);
                        return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateJob(job))
            .execute();
	}

	@CommandDescription("Unassign a job")
	@Permission(CommandPermissions.UNASSIGN_JOB)
	@Command("job|jobs unassign <job>")
	public void onUnassign(final @NonNull Player player,
						   @Argument(value = "job", description = "Job") final @NonNull Job job) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (job.getJobStatus().equals(JobStatus.TAKEN)) {
                    plugin.getEpicJobsPlayer(job.getClaimant()).ifPresent(epicJobsPlayer -> epicJobsPlayer.removeJob(job));
                    return true;
                } else {
                    JOB_CANT_BE_UNASSIGNED.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateJob(job))
            .execute();
	}

	@CommandDescription("Assign a job")
	@Permission(CommandPermissions.ASSIGN_JOB)
	@Command("job|jobs assign <job> <player>")
	public void onAssign(final @NonNull Player player,
						 @Argument(value = "job", description = "Job", suggestions = "open-job") final @NonNull Job job,
						 @Argument(value = "player", description = "Player") final @NonNull Player target
	) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (job.getJobStatus().equals(JobStatus.OPEN)) {
                    plugin.getEpicJobsPlayer(target.getUniqueId()).ifPresent(job::claim);
                    HAS_ASSIGNED_JOB.send(player, target.getName(), job.getId());
                    HAS_BEEN_ASSIGNED_JOB.send(target, job.getId());
                    return true;
                } else {
                    JOB_CANT_BE_ASSIGNED.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateJob(job))
            .execute();
	}

	@CommandDescription("Create a job")
	@Permission(CommandPermissions.CREATE_JOB)
	@Command("job|jobs create <project> <category> <description>")
	public void onCreate(final @NonNull Player player,
						 @Argument(value = "project", description = "Project", suggestions = "active-project") final @NonNull Project project,
						 @Argument(value = "category", description = "Category") final @NonNull JobCategory jobCategory,
						 @Argument(value = "description", description = "Description") final @NonNull @Greedy String description
	) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (project.getProjectStatus().equals(ProjectStatus.ACTIVE)) {
                    CREATING_JOB.sendActionbar(player);
                    return true;
                } else {
                    PROJECT_ALREADY_COMPLETE.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .asyncFirst(() -> {
                final Job job = plugin.getStorageImplementation().createAndLoadJob(player.getUniqueId(), description, project, player.getLocation(), JobStatus.OPEN, jobCategory);
                plugin.getJobManager().addJob(job);
                return job;
            })
            .syncLast((job) -> {
                final String message = (job == null) ? "§cError while creating job. Please contact an administrator." : SUCCESSFULLY_CREATED_JOB.toString(job.getId());
                player.sendMessage(message);
            })
            .execute();
	}

	@CommandDescription("Remove a job")
	@Permission(CommandPermissions.DELETE_JOB)
	@Command("job|jobs remove|delete <job>")
	public void onRemove(final @NonNull Player player,
						 @Argument(value = "job", description = "Job") final @NonNull Job job) {
		EpicJobs.newSharedChain("EpicJobs")
            .sync(() -> {
                plugin.getJobManager().removeJob(job);
                job.getProject().removeJob(job);
                plugin.getEpicJobsPlayer(job.getClaimant()).ifPresent(epicJobsPlayer -> epicJobsPlayer.removeJob(job));
                REMOVING_JOB.sendActionbar(player, job.getId());
            })
            .async(() -> plugin.getStorageImplementation().deleteJob(job))
            .sync(() -> SUCCESSFULLY_REMOVED_JOB.send(player)
        ).execute();
	}

	@CommandDescription("Job statistics")
	@Permission(CommandPermissions.SHOW_STATISTICS)
	@Command("job|jobs stats [player]")
	public void onStats(final @NonNull CommandSender sender,
						@Argument(value = "player", description = "Player") final @Nullable Player target) {
		final Player player;
		if (target != null) {
			player = target;
		} else if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			sender.sendMessage("§cYou must be a player to use this command.");
			return;
		}

		final Optional<EpicJobsPlayer> optional = plugin.getEpicJobsPlayer(player.getUniqueId());
		if (optional.isEmpty()) {
			player.sendMessage("§cError while loading player data. Please contact an administrator.");
			return;
		}
        player.sendMessage("Completed jobs: " + optional.get().getCompletedJobs().size());
	}

}
