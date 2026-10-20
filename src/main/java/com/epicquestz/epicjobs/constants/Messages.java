package com.epicquestz.epicjobs.constants;

import com.epicquestz.epicjobs.constants.Palette.Role;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;

/**
 * Central registry of user-facing messages. Templates are written in
 * <a href="https://docs.advntr.dev/minimessage/format.html">MiniMessage</a> and rendered to
 * Adventure {@link Component}s. Positional arguments are referenced as {@code <arg1>},
 * {@code <arg2>}, ... and are inserted as plain (unparsed) text.
 *
 * <p>Each message declares a semantic {@link Role} from the shared {@link Palette}; the role's
 * body colour is applied to the whole message, and {@code <em>} marks the values that should
 * stand out in the role's accent tone (see {@link Palette} for the tonal-pair rules).
 */
public enum Messages {

    // Jobs List & General
    JOB_DOESNT_EXIST(Role.ERROR, "This job does not exist. Did you enter the correct job ID?"),
    NO_JOBS_AVAILABLE(Role.WARNING, "There are no jobs available! Check back later."),
    PLAYER_HAS_NO_JOBS(Role.WARNING, "You have no claimed jobs!\n<muted>Use <em>/job check</em> to see if there are any available to claim.</muted>"),
    PLAYER_HAS_MULITPLE_JOBS(Role.WARNING, "You have more than one job.\n<muted>Please specify a job to continue.</muted>"),
    PLAYER_HASNT_CLAIMED_JOB(Role.ERROR, "You have not claimed this job."),
    TARGET_HAS_NO_JOBS(Role.WARNING, "<em><arg1></em> has not claimed any jobs."),

    // Jobs Claim
    ANNOUNCE_JOB_TAKEN(Role.SUCCESS, "<em><arg1></em> has claimed job <em>#<arg2></em>."),
    JOB_NOT_OPEN(Role.WARNING, "This job is not available to be claimed."),

    // Jobs Abandon
    ANNOUNCE_JOB_ABANDONMENT(Role.WARNING, "<em><arg1></em> has abandoned job <em>#<arg2></em>.\nIt is available to be claimed again."),
    JOB_CANT_BE_ABANDONED(Role.ERROR, "You can only abandon jobs that are yours and incomplete."),

    // Jobs Teleport
    PLAYER_JOB_TELEPORT(Role.INFO, "Teleporting to job site <em>#<arg1></em>"),

    // Jobs Done
    ANNOUNCE_JOB_DONE(Role.SUCCESS, "<em><arg1></em> has marked job <em>#<arg2></em> as done."),
    ANNOUNCE_JOB_DONE_OVERRIDE(Role.SUCCESS, "<em><arg1></em> has marked <em><arg2></em>'s job <em>#<arg3></em> as done."),
    CONFIRM_DONE_OVERRIDE(Role.WARNING, "Job <em>#<arg1></em> is claimed by <em><arg2></em>.\n<u>Click to mark it done anyway</u>."),
    JOB_HAS_TO_BE_ACTIVE(Role.WARNING, "The job has to be active to be marked done."),
    PLAYER_HAS_NO_ACTIVE_JOBS(Role.WARNING, "You have no active jobs."),

    // Jobs Complete
    JOB_COMPLETED(Role.SUCCESS, "Job <em>#<arg1></em> has been marked as complete."),
    JOB_CANT_BE_COMPLETE(Role.WARNING, "A job has to be marked done to complete."),
    JOB_COMPLETE_NOT_ALLOWED(Role.ERROR, "You can only complete jobs for projects you lead or are a deputy of."),
    CONFIRM_COMPLETE_OVERRIDE(Role.WARNING, "Job <em>#<arg1></em> belongs to project <em><arg2></em>, which you do not lead.\n<u>Click to mark it complete anyway</u>."),

    // Jobs Reopen
    ANNOUNCE_JOB_REOPEN(Role.WARNING, "<em><arg1></em> has reopened job <em>#<arg2></em>."),
    JOB_REOPEN(Role.SUCCESS, "Re-opened job <em>#<arg1></em> for player <em><arg2></em> to make edits."),
    JOB_NOT_DONE(Role.WARNING, "This job is not marked as done or complete."),

    // Jobs Un-assign
    HAS_UNASSIGNED_JOB(Role.SUCCESS, "You have un-assigned the player from job <em>#<arg1></em>."),
    JOB_CANT_BE_UNASSIGNED(Role.ERROR, "You can only un-assign incomplete jobs taken by a player."),

    // Jobs Assign
    HAS_ASSIGNED_JOB(Role.SUCCESS, "You have assigned <em><arg1></em> to job <em>#<arg2></em>."),
    JOB_CANT_BE_ASSIGNED(Role.ERROR, "You can only assign available jobs to a player."),
    HAS_BEEN_ASSIGNED_JOB(Role.INFO, "You have been assigned job <em>#<arg1></em>."),

    // Jobs Create
    SUCCESSFULLY_CREATED_JOB(Role.SUCCESS, "Successfully created job with id <em>#<arg1></em>."),
    ERROR_CREATING_JOB(Role.ERROR, "Error while creating job. Please contact an administrator."),

    // Jobs Remove
    SUCCESSFULLY_REMOVED_JOB(Role.SUCCESS, "Successfully deleted job."),

    // Jobs Stats
    MUST_BE_PLAYER(Role.ERROR, "You must be a player to use this command."),
    COMPLETED_JOBS_COUNT(Role.INFO, "Completed jobs: <em><arg1></em>"),
    TARGET_COMPLETED_JOBS_COUNT(Role.INFO, "<em><arg1></em>'s completed jobs: <em><arg2></em>"),

    // Jobs Edit
    JOB_CLAIMANT_SET(Role.SUCCESS, "Set claimant of job to: <em><arg1></em>"),
    JOB_DESCRIPTION_SET(Role.SUCCESS, "Set description of job to: <em><arg1></em>"),
    JOB_PROJECT_SET(Role.SUCCESS, "Set project of job to: <em><arg1></em>"),
    JOB_LOCATION_SET(Role.SUCCESS, "Set job location to your current position"),
    JOB_STATUS_SET(Role.SUCCESS, "Set job status to: <em><arg1></em>"),
    JOB_CATEGORY_SET(Role.SUCCESS, "Set job category to: <em><arg1></em>"),

    // Project List & General
    PROJECT_DOESNT_EXIST(Role.ERROR, "This project does not exist. Did you enter the correct project name?"),
    NO_PROJECTS_AVAILABLE(Role.WARNING, "There are no active projects to participate in."),
    PROJECT_ALREADY_COMPLETE(Role.WARNING, "This project is already marked as complete."),

    // Create Project
    SUCCESSFULLY_CREATED_PROJECT(Role.SUCCESS, "Successfully created project with id <em>#<arg1></em>."),
    CANT_CREATE_PROJECT(Role.ERROR, "Cannot create a project with duplicate name."),
    ERROR_CREATING_PROJECT(Role.ERROR, "Error while creating project. Please contact an administrator."),

    // Project Edit
    PLAYER_NOT_FOUND(Role.ERROR, "Player <em><arg1></em> could not be found."),
    PROJECT_NAME_SET(Role.SUCCESS, "Set name of project to <em><arg1></em>"),
    PROJECT_LEADER_SET(Role.SUCCESS, "Set project leader to <em><arg1></em>"),
    PROJECT_LOCATION_SET(Role.SUCCESS, "Updated project location to your current position"),
    PROJECT_STATUS_SET(Role.SUCCESS, "Set project status to <em><arg1></em>"),

    // Project Teleport
    PLAYER_PROJECT_TELEPORT(Role.INFO, "Teleporting to project site <em><arg1></em>"),

    // Project Pause
    PROJECT_PAUSED(Role.INFO, "Paused project <em><arg1></em>."),
    PROJECT_ALREADY_PAUSED(Role.WARNING, "Project is already paused."),

    // Project Resume
    PROJECT_RESUMED(Role.INFO, "Unpaused project <em><arg1></em>."),
    PROJECT_NOT_PAUSED(Role.WARNING, "Project is not paused."),

    // Project Complete
    ANNOUNCE_PROJECT_COMPLETION(Role.SUCCESS, "Project <em><arg1></em> has been completed!"),

    // Project Stats
    NO_STATS_AVAILABLE(Role.WARNING, "There are no completed jobs to show statistics for yet."),
    PROJECT_HAS_NO_COMPLETED_JOBS(Role.INFO, "No jobs have been completed in <em><arg1></em> yet."),

    // Project Deputies
    DEPUTY_ADDED(Role.SUCCESS, "Added <em><arg1></em> as a deputy of project <em><arg2></em>."),
    DEPUTY_REMOVED(Role.WARNING, "Removed <em><arg1></em> as a deputy of project <em><arg2></em>."),
    ALREADY_DEPUTY(Role.WARNING, "<em><arg1></em> is already a deputy of this project."),
    NOT_A_DEPUTY(Role.WARNING, "<em><arg1></em> is not a deputy of this project."),
    LEADER_CANT_BE_DEPUTY(Role.WARNING, "The project leader cannot be added as a deputy."),
    NO_DEPUTIES(Role.INFO, "This project has no deputies."),
    CANT_MANAGE_DEPUTIES(Role.ERROR, "Only the project leader can manage deputies."),

    // Project ownership
    NOT_PROJECT_MANAGER(Role.ERROR, "You can only manage jobs and projects you lead or are a deputy of."),
    NOT_PROJECT_LEADER(Role.ERROR, "Only the project leader can do this."),

    // Profile
    MISSING_PROFILE(Role.ERROR, "Your EpicJobs profile could not be found. Please contact an administrator.");

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final Role role;
    private final String message;

    Messages(final Role role, final String message) {
        this.role = role;
        this.message = message;
    }

    /**
     * @return the raw MiniMessage template (not the rendered component).
     */
    public String template() {
        return message;
    }

    public Component component(final Object... args) {
        final TagResolver[] resolvers = new TagResolver[args.length + 1];
        resolvers[0] = role.tags();
        for (int i = 0; i < args.length; i++) {
            resolvers[i + 1] = Placeholder.unparsed("arg" + (i + 1), String.valueOf(args[i]));
        }
        return MINI_MESSAGE.deserialize(message, resolvers).colorIfAbsent(role.body());
    }

    public void send(final Audience audience, final Object... args) {
        audience.sendMessage(component(args));
    }

    public void broadcast(final Object... args) {
        Bukkit.getServer().broadcast(component(args));
    }

}
