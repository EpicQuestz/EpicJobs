package com.epicquestz.epicjobs.constants;

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
 */
public enum Messages {

    // Jobs List & General
    JOB_DOESNT_EXIST("<red>This job does not exist. Did you enter the correct job ID?</red>"),
    NO_JOBS_AVAILABLE("<red>There are no jobs available! Check back later.</red>"),
    PLAYER_HAS_NO_JOBS("<red>You have no claimed jobs!</red>\n<gold>Use /job check to see if there are any available to claim.</gold>"),
    PLAYER_HAS_MULITPLE_JOBS("<red>You have more than one job.</red>\n<gold>Please specify a job to continue.</gold>"),
    PLAYER_HASNT_CLAIMED_JOB("<red>You have not claimed this job.</red>"),

    // Jobs Claim
    ANNOUNCE_JOB_TAKEN("<green><arg1> has claimed job <aqua>#<arg2></aqua>.</green>"),
    JOB_NOT_OPEN("<red>This job is not available to be claimed.</red>"),

    // Jobs Abandon
    ANNOUNCE_JOB_ABANDONMENT("<red><arg1> has abandoned job <aqua>#<arg2></aqua>.</red>\n<green>It is available to be claimed again.</green>"),
    JOB_CANT_BE_ABANDONED("<red>You can only abandon jobs that are yours and incomplete.</red>"),

    // Jobs Teleport
    PLAYER_JOB_TELEPORT("<yellow>Teleporting to job site #<arg1></yellow>"),

    // Jobs Done
    ANNOUNCE_JOB_DONE("<green><arg1> has marked job <aqua>#<arg2></aqua> as done.</green>"),
    JOB_HAS_TO_BE_ACTIVE("<red>The job has to be active to be marked done.</red>"),
    PLAYER_HAS_NO_ACTIVE_JOBS("<red>You have no active jobs.</red>"),

    // Jobs Complete
    JOB_COMPLETED("<green>Job <aqua>#<arg1></aqua> has been marked as complete.</green>"),
    JOB_CANT_BE_COMPLETE("<red>A job has to be marked done to complete.</red>"),

    // Jobs Reopen
    ANNOUNCE_JOB_REOPEN("<gold><arg1> has reopened job <aqua>#<arg2></aqua>.</gold>"),
    JOB_REOPEN("<green>Re-opened job <aqua>#<arg1></aqua> for player <arg2> to make edits.</green>"),
    JOB_NOT_DONE("<red>This job is not marked as done or complete.</red>"),

    // Jobs Un-assign
    HAS_UNASSIGNED_JOB("<gold>You have un-assigned the player from job <aqua>#<arg1></aqua>.</gold>"),
    JOB_CANT_BE_UNASSIGNED("<red>You can only un-assign incomplete jobs taken by a player.</red>"),

    // Jobs Assign
    HAS_ASSIGNED_JOB("<gold>You have assigned <arg1> to job <aqua>#<arg2></aqua>.</gold>"),
    JOB_CANT_BE_ASSIGNED("<red>You can only assign available jobs to a player.</red>"),
    HAS_BEEN_ASSIGNED_JOB("<gold>You have been assigned job <aqua>#<arg1></aqua>.</gold>"),

    // Jobs Create
    CREATING_JOB("<gold>Creating job...</gold>"), // actionbar
    SUCCESSFULLY_CREATED_JOB("<green>Successfully created job with id <aqua>#<arg1></aqua>.</green>"),

    // Jobs Remove
    REMOVING_JOB("<gold>Removing job #<arg1>...</gold>"), // actionbar
    SUCCESSFULLY_REMOVED_JOB("<green>Successfully deleted job.</green>"),

    // Project List & General
    PROJECT_DOESNT_EXIST("<red>This project does not exist. Did you enter the correct project name?</red>"),
    NO_PROJECTS_AVAILABLE("<red>There are no active projects to participate in.</red>"),
    PROJECT_ALREADY_COMPLETE("<red>This project is already marked as complete.</red>"),

    // Create Project
    CREATING_PROJECT("<gold>Creating project <arg1>...</gold>"), // actionbar
    SUCCESSFULLY_CREATED_PROJECT("<green>Successfully created project with id #<arg1>.</green>"),
    CANT_CREATE_PROJECT("<red>Cannot create a project with duplicate name.</red>"),

    // Project Edit
    PLAYER_NOT_FOUND("<red>Player <arg1> could not be found.</red>"),

    // Project Teleport
    PLAYER_PROJECT_TELEPORT("<yellow>Teleporting to project site <arg1></yellow>"),

    // Project Complete
    ANNOUNCE_PROJECT_COMPLETION("<green>Project <arg1> has been completed!</green>"),

    // Profile
    MISSING_PROFILE("<red>Your EpicJobs profile could not be found. Please contact an administrator.</red>");

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final String message;

    Messages(final String message) {
        this.message = message;
    }

    /**
     * @return the raw MiniMessage template (not the rendered component).
     */
    public String template() {
        return message;
    }

    public Component component(final Object... args) {
        if (args.length == 0) {
            return MINI_MESSAGE.deserialize(message);
        }
        final TagResolver[] resolvers = new TagResolver[args.length];
        for (int i = 0; i < args.length; i++) {
            resolvers[i] = Placeholder.unparsed("arg" + (i + 1), String.valueOf(args[i]));
        }
        return MINI_MESSAGE.deserialize(message, resolvers);
    }

    public void send(final Audience audience, final Object... args) {
        audience.sendMessage(component(args));
    }

    public void sendActionbar(final Audience audience, final Object... args) {
        audience.sendActionBar(component(args));
    }

    public void broadcast(final Object... args) {
        Bukkit.getServer().broadcast(component(args));
    }

}
