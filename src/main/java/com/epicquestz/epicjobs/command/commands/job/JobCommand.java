package com.epicquestz.epicjobs.command.commands.job;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.constants.Palette;
import com.epicquestz.epicjobs.constants.SkullHeads;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobCategory;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.project.ProjectStatus;
import com.epicquestz.epicjobs.user.User;
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
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import java.util.UUID;
import java.util.stream.Collectors;

import static com.epicquestz.epicjobs.constants.Messages.ANNOUNCE_JOB_ABANDONMENT;
import static com.epicquestz.epicjobs.constants.Messages.ANNOUNCE_JOB_DONE;
import static com.epicquestz.epicjobs.constants.Messages.ANNOUNCE_JOB_REOPEN;
import static com.epicquestz.epicjobs.constants.Messages.ANNOUNCE_JOB_TAKEN;
import static com.epicquestz.epicjobs.constants.Messages.COMPLETED_JOBS_COUNT;
import static com.epicquestz.epicjobs.constants.Messages.ERROR_CREATING_JOB;
import static com.epicquestz.epicjobs.constants.Messages.HAS_ASSIGNED_JOB;
import static com.epicquestz.epicjobs.constants.Messages.HAS_BEEN_ASSIGNED_JOB;
import static com.epicquestz.epicjobs.constants.Messages.HAS_UNASSIGNED_JOB;
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
import static com.epicquestz.epicjobs.constants.Messages.MISSING_PROFILE;
import static com.epicquestz.epicjobs.constants.Messages.MUST_BE_PLAYER;
import static com.epicquestz.epicjobs.constants.Messages.PLAYER_HASNT_CLAIMED_JOB;
import static com.epicquestz.epicjobs.constants.Messages.PLAYER_HAS_MULITPLE_JOBS;
import static com.epicquestz.epicjobs.constants.Messages.PLAYER_HAS_NO_ACTIVE_JOBS;
import static com.epicquestz.epicjobs.constants.Messages.PLAYER_HAS_NO_JOBS;
import static com.epicquestz.epicjobs.constants.Messages.PROJECT_ALREADY_COMPLETE;
import static com.epicquestz.epicjobs.constants.Messages.SUCCESSFULLY_CREATED_JOB;
import static com.epicquestz.epicjobs.constants.Messages.SUCCESSFULLY_REMOVED_JOB;

@Command("job|jobs")
public class JobCommand {

	private static final ItemStack BACK_BUTTON = Utils.getSkull(SkullHeads.OAK_WOOD_ARROW_LEFT.getBase64(), "<em>Back");

	private final EpicJobs plugin;

	public JobCommand(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@CommandDescription("List jobs")
	@Permission(CommandPermissions.LIST_JOBS)
	@Command("list|ls")
	public void onList(final @NonNull Player player) {
		sendProjectMenu(player);
	}

	@CommandDescription("List jobs near you")
	@Permission(CommandPermissions.LIST_JOBS)
	@Command("list|ls near [radius]")
	public void onListNear(final @NonNull Player player,
						   @Argument(value = "radius", description = "Radius") @Default(value = "32") final int radius) {
		final int clampedRadius = Math.clamp(radius, 1, 512);
		final List<Job> jobs = plugin.getJobManager().getJobs().stream()
            .filter(job -> job.getJobStatus().equals(JobStatus.OPEN))
            .filter(job -> job.getLocation().getWorld().equals(player.getWorld()))
            .filter(job -> job.getLocation().distanceSquared(player.getLocation()) < (double) clampedRadius * clampedRadius)
            .collect(Collectors.toList());
        sendJobMenu(player, "Available jobs (range " + clampedRadius + ")", null, jobs);
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
                .withName(Component.text(project.getName(), Palette.Role.INFO.accent(), TextDecoration.BOLD))
                .withLore(Component.text("Leader: ", Palette.MUTED).append(Component.text(Utils.getPlayerHolderText(project.getLeader()), Palette.Role.INFO.body())))
                .withLore(Component.empty())
                .withLore("<muted>Shift-click to <em>teleport</em>")
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
        final ChestGui gui = MenuHelper.getPaginatedSelectionGui("Current Projects", guiItems, null);
        gui.show(player);
    }

	private void sendJobMenu(final Player player, final String title, final GuiItem mainMenuItem, final List<Job> jobs) {
		final List<GuiItem> guiItems = new ArrayList<>();
		for (final Job job : jobs) {
			final ItemStack itemStack = JobItemHelper.getJobItem(job, "<muted>Shift-click to <em>claim</em>", JobItemHelper.InfoType.PROJECT, JobItemHelper.InfoType.CATEGORY, JobItemHelper.InfoType.STATUS, JobItemHelper.InfoType.DESCRIPTION, JobItemHelper.InfoType.CREATOR);
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
						player.sendMessage(Component.text("\"" + job.getDescription() + "\"", Palette.Role.INFO.body()).decoration(TextDecoration.ITALIC, true));
						break;
				}
			});
			guiItems.add(guiItem);
		}

		final ItemStack infoBook = new ItemStackBuilder(Material.BOOK)
            .withName("<em>Information")
            .withLore("<muted><em>Claim</em> job by using shift-click")
            .withLore("<muted><em>Teleport</em> by using left-click")
            .withLore("<muted>To <em>view job info</em> right-click")
            .build();

        final ChestGui gui = MenuHelper.getPaginatedGui(title, guiItems, mainMenuItem, infoBook);
        gui.show(player);
	}

	@CommandDescription("List your jobs")
	@Permission(CommandPermissions.LIST_JOBS)
	@Command("mine")
	public void onMine(final @NonNull Player player) {
		onListMine(player);
	}

	@CommandDescription("List your jobs")
	@Permission(CommandPermissions.LIST_JOBS)
	@Command("list|ls mine")
	public void onListMine(final @NonNull Player player) {
		final Optional<User> epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
        epicJobsPlayer.ifPresent(jobsPlayer -> sendStatusSelectionMenu(player, jobsPlayer));
	}

	private void sendStatusSelectionMenu(final Player player, final User user) {
        final GuiItem mainMenuItem = new GuiItem(BACK_BUTTON, inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            sendStatusSelectionMenu(player, user);
        });

        final GuiItem projectItem = new GuiItem(new ItemStackBuilder(Material.WRITABLE_BOOK).withName("<em>Active Jobs").build(), inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            final List<Job> jobs = user.getJobs().stream().filter(job -> job.getJobStatus().equals(JobStatus.TAKEN) || job.getJobStatus().equals(JobStatus.DONE)).collect(Collectors.toList());
            sendMyJobMenu(player, "Your Jobs", mainMenuItem, jobs);
        });

        final GuiItem statusItem = new GuiItem(new ItemStackBuilder(Material.COMPOSTER).withName("<em>Completed Jobs").build(), inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            final List<Job> jobs = user.getJobs().stream().filter(job -> job.getJobStatus().equals(JobStatus.COMPLETE)).collect(Collectors.toList());
            sendMyJobMenu(player, "Your Jobs", mainMenuItem, jobs);
        });

        final ChestGui gui = MenuHelper.getStaticSelectionGui("Select Job Status", null, projectItem, statusItem);
        gui.show(player);
    }

	private void sendMyJobMenu(final Player player, final String title, final GuiItem mainMenuItem, final List<Job> jobs) {
        final List<GuiItem> guiItems = new ArrayList<>();
        for (final Job job : jobs) {
            GuiItem guiItem = null;
            switch (job.getJobStatus()) {
                case TAKEN: {
                    final ItemStack itemStack = JobItemHelper.getJobItem(job, "<muted>Shift left-click to mark <em>done</em>\n<muted>Shift right-click to mark <em>abandon</em>", JobItemHelper.InfoType.PROJECT, JobItemHelper.InfoType.CATEGORY, JobItemHelper.InfoType.STATUS, JobItemHelper.InfoType.DESCRIPTION, JobItemHelper.InfoType.CREATOR);
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
                                player.sendMessage(Component.text("\"" + job.getDescription() + "\"", Palette.Role.INFO.body()).decoration(TextDecoration.ITALIC, true));
                                break;
                        }
                    });
                } break;
                case DONE: {
                    final ItemStack itemStack = JobItemHelper.getJobItem(job, "<muted>Shift right-click to mark <em>abandon</em>", JobItemHelper.InfoType.PROJECT, JobItemHelper.InfoType.CATEGORY, JobItemHelper.InfoType.STATUS, JobItemHelper.InfoType.DESCRIPTION, JobItemHelper.InfoType.CREATOR);
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
                                player.sendMessage(Component.text("\"" + job.getDescription() + "\"", Palette.Role.INFO.body()).decoration(TextDecoration.ITALIC, true));
                                break;
                        }
                    });
                } break;
                case COMPLETE: {
                    final ItemStack itemStack = JobItemHelper.getJobItem(job, "<muted>Shift-click to <em>teleport</em>", JobItemHelper.InfoType.PROJECT, JobItemHelper.InfoType.CATEGORY, JobItemHelper.InfoType.STATUS, JobItemHelper.InfoType.DESCRIPTION, JobItemHelper.InfoType.CREATOR);
                    guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
                        inventoryClickEvent.setResult(Event.Result.DENY);
                        switch (inventoryClickEvent.getClick()) {
                            case SHIFT_LEFT:
                            case SHIFT_RIGHT:
                                job.teleport(player);
                                break;
                            case LEFT:
                            case RIGHT:
                                player.sendMessage(Component.text("\"" + job.getDescription() + "\"", Palette.Role.INFO.body()).decoration(TextDecoration.ITALIC, true));
                                break;
                        }
                    });
                } break;
            }
            guiItems.add(guiItem);
        }

        final ItemStack infoBook = new ItemStackBuilder(Material.BOOK)
            .withName("<em>Information")
            .withLore("<muted>None :-)")
            .build();

        final ChestGui gui = MenuHelper.getPaginatedGui(title, guiItems, mainMenuItem, infoBook);
        gui.show(player);
    }

	@CommandDescription("Show job info")
	@Permission(CommandPermissions.JOB_INFO)
	@Command("info <job>")
	public void onInfo(final @NonNull CommandSender sender,
					   @Argument(value = "job", description = "Job") final @NonNull Job job) {
		final TextComponent text = Component.text()
			.content("Job #" + job.getId() + " @ ").color(Palette.Role.INFO.accent()).decoration(TextDecoration.BOLD, true)
			.append(Component.text(
				String.format("[%s x:%s y:%s z:%s]\n\n",
					job.getLocation().getWorld().getName(),
					job.getLocation().getBlockX(),
					job.getLocation().getBlockY(),
					job.getLocation().getBlockZ()
				), Palette.Role.INFO.accent()).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.UNDERLINED, true).hoverEvent(HoverEvent.showText(Component.text("Click to teleport!", Palette.MUTED))).clickEvent(ClickEvent.runCommand("/job teleport " + job.getId())))
			.append(Component.text()
				.decoration(TextDecoration.BOLD, false)
				.append(Component.text("Project: ", Palette.MUTED)).append(Component.text(job.getProject().getName(), Palette.Role.INFO.body()))
				.append(Component.text(" Category: ", Palette.MUTED)).append(Component.text(job.getJobCategory().toString(), Palette.Role.INFO.body()))
				.append(Component.text(" Status: ", Palette.MUTED)).append(Component.text(job.getJobStatus().toString() + "\n", Palette.Role.INFO.body()))
				.append(Component.text("Leader: ", Palette.MUTED)).append(Component.text(Utils.getPlayerHolderText(job.getCreator()), Palette.Role.INFO.body()))
				.append(Component.text(" Claimant: ", Palette.MUTED)).append(Component.text(Utils.getPlayerHolderText(job.getClaimant()) + "\n\n", Palette.Role.INFO.body()))
				.append(Component.text("\"" + job.getDescription() + "\"", Palette.Role.INFO.body()).decoration(TextDecoration.ITALIC, true)))
			.build();
		sender.sendMessage(Component.empty());
		sender.sendMessage(text);
		sender.sendMessage(Component.empty());
	}

	@CommandDescription("Claim a job")
	@Permission(CommandPermissions.CLAIM_JOB)
	@Command("claim|c [job]")
	public void onClaim(final @NonNull Player player,
						@Argument(value = "job", description = "Job", suggestions = "open-job") final @Nullable Job job) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                final Optional<User> epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
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
            .async(() -> plugin.getStorage().updateJob(job))
            .execute();
	}

	@CommandDescription("Abandon a job")
	@Permission(CommandPermissions.ABANDON_JOB)
	@Command("abandon [job]")
	public void onAbandon(final @NonNull Player player,
						  @Argument(value = "job", description = "Job", suggestions = "player-job") final @Nullable Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                final Optional<User> optional = plugin.getEpicJobsPlayer(player.getUniqueId());
                if (optional.isEmpty()) return null;
				final User epicJobsPlayer = optional.get();
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
						jobEdited = playerJobs.getFirst();
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
            .asyncLast((jobEdited) -> plugin.getStorage().updateJob(jobEdited))
            .execute();
	}

	@CommandDescription("Mark a job as done")
	@Permission(CommandPermissions.DONE_JOB)
	@Command("done|d [job]")
	public void onDone(final @NonNull Player player,
					   @Argument(value = "job", description = "Job", suggestions = "player-job") final @Nullable Job job) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                final Optional<User> optional = plugin.getEpicJobsPlayer(player.getUniqueId());
				if (optional.isEmpty()) {
					MISSING_PROFILE.send(player);
					return null;
				}
                final User user = optional.get();
                final List<Job> jobs = user.getJobs().stream().filter(j -> j.getJobStatus().equals(JobStatus.TAKEN)).toList();
                Job jobEdited = null;

                if (job == null) {
                    if (jobs.size() == 1) {
                            jobEdited = jobs.getFirst();
                        if (jobEdited.getJobStatus().equals(JobStatus.TAKEN)) {
                            jobEdited.setJobStatus(JobStatus.DONE);
                            ANNOUNCE_JOB_DONE.broadcast(player.getName(), jobs.getFirst().getId());
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
            .asyncLast((jobedited) -> plugin.getStorage().updateJob(jobedited))
            .execute();
	}

	@CommandDescription("Mark a job as complete")
	@Permission(CommandPermissions.COMPLETE_JOB)
	@Command("complete <job>")
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
            .async(() -> plugin.getStorage().updateJob(job))
            .execute();
	}

	@CommandDescription("Reopen a job")
	@Permission(CommandPermissions.REOPEN_JOB)
	@Command("reopen <job>")
	public void onReopen(final @NonNull Player player,
						 @Argument(value = "job", description = "Job") final @NonNull Job job) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                switch (job.getJobStatus()) {
                    case COMPLETE:
                    case TAKEN:
                        final UUID previousClaimant = job.getClaimant();
                        job.setJobStatus(JobStatus.OPEN);
                        job.setClaimant(null);
                        if (previousClaimant != null) {
                            plugin.getEpicJobsPlayer(previousClaimant).ifPresent(jobsPlayer -> jobsPlayer.removeJob(job));
                        }
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
            .async(() -> plugin.getStorage().updateJob(job))
            .execute();
	}

	@CommandDescription("Unassign a job")
	@Permission(CommandPermissions.UNASSIGN_JOB)
	@Command("unassign <job>")
	public void onUnassign(final @NonNull Player player,
						   @Argument(value = "job", description = "Job") final @NonNull Job job) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (job.getJobStatus().equals(JobStatus.TAKEN)) {
                    final UUID claimant = job.getClaimant();
                    job.setClaimant(null);
                    job.setJobStatus(JobStatus.OPEN);
                    if (claimant != null) {
                        plugin.getEpicJobsPlayer(claimant).ifPresent(epicJobsPlayer -> epicJobsPlayer.removeJob(job));
                    }
					HAS_UNASSIGNED_JOB.send(player, job.getId());
                    return true;
                } else {
                    JOB_CANT_BE_UNASSIGNED.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorage().updateJob(job))
            .execute();
	}

	@CommandDescription("Assign a job")
	@Permission(CommandPermissions.ASSIGN_JOB)
	@Command("assign <job> <player>")
	public void onAssign(final @NonNull Player player,
						 @Argument(value = "job", description = "Job", suggestions = "open-job") final @NonNull Job job,
						 @Argument(value = "player", description = "Player") final @NonNull OfflinePlayer target
	) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (job.getJobStatus().equals(JobStatus.OPEN)) {
                    job.setClaimant(target.getUniqueId());
                    job.setJobStatus(JobStatus.TAKEN);
                    // keep the in-memory profile in sync if the target is currently online
                    plugin.getEpicJobsPlayer(target.getUniqueId()).ifPresent(epicJobsPlayer -> epicJobsPlayer.addJob(job));
                    HAS_ASSIGNED_JOB.send(player, Utils.getPlayerHolderText(target.getUniqueId()), job.getId());
                    final Player onlineTarget = target.getPlayer();
                    if (onlineTarget != null) {
                        HAS_BEEN_ASSIGNED_JOB.send(onlineTarget, job.getId());
                    }
                    return true;
                } else {
                    JOB_CANT_BE_ASSIGNED.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorage().updateJob(job))
            .execute();
	}

	@CommandDescription("Create a job")
	@Permission(CommandPermissions.CREATE_JOB)
	@Command("create <project> <category> <description>")
	public void onCreate(final @NonNull Player player,
						 @Argument(value = "project", description = "Project", suggestions = "active-project") final @NonNull Project project,
						 @Argument(value = "category", description = "Category") final @NonNull JobCategory jobCategory,
						 @Argument(value = "description", description = "Description") final @NonNull @Greedy String description
	) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (project.getProjectStatus().equals(ProjectStatus.ACTIVE)) {
                    return true;
                } else {
                    PROJECT_ALREADY_COMPLETE.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .asyncFirst(() -> plugin.getStorage().createAndLoadJob(player.getUniqueId(), description, project, player.getLocation(), JobStatus.OPEN, jobCategory))
            .syncLast((job) -> {
                if (job == null) {
                    ERROR_CREATING_JOB.send(player);
                    return;
                }
                plugin.getJobManager().addJob(job); // register on the main thread
                job.getProject().addJob(job);
                SUCCESSFULLY_CREATED_JOB.send(player, job.getId());
            })
            .execute();
	}

	@CommandDescription("Remove a job")
	@Permission(CommandPermissions.DELETE_JOB)
	@Command("remove|delete <job>")
	public void onRemove(final @NonNull Player player,
						 @Argument(value = "job", description = "Job") final @NonNull Job job) {
		EpicJobs.newSharedChain("EpicJobs")
            .sync(() -> {
                plugin.getJobManager().removeJob(job);
                job.getProject().removeJob(job);
                plugin.getEpicJobsPlayer(job.getClaimant()).ifPresent(epicJobsPlayer -> epicJobsPlayer.removeJob(job));
            })
            .async(() -> plugin.getStorage().deleteJob(job))
            .sync(() -> SUCCESSFULLY_REMOVED_JOB.send(player)
        ).execute();
	}

	@CommandDescription("Job statistics")
	@Permission(CommandPermissions.SHOW_STATISTICS)
	@Command("stats [player]")
	public void onStats(final @NonNull CommandSender sender,
						@Argument(value = "player", description = "Player") final @Nullable Player target) {
		final Player player;
		if (target != null) {
			player = target;
		} else if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			MUST_BE_PLAYER.send(sender);
			return;
		}

		final Optional<User> optional = plugin.getEpicJobsPlayer(player.getUniqueId());
		if (optional.isEmpty()) {
			MISSING_PROFILE.send(player);
			return;
		}
        COMPLETED_JOBS_COUNT.send(player, optional.get().getJobs().stream().filter(j -> j.getJobStatus().equals(JobStatus.COMPLETE)).toList().size());
	}

}
