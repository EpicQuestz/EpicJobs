package de.stealwonders.epicjobs.command.caption;

import cloud.commandframework.bukkit.BukkitCaptionRegistry;

/**
 * Caption registry that uses bi-functions to produce messages
 *
 * @param <C> Command sender type
 */
public class EpicJobsCaptionRegistry<C> extends BukkitCaptionRegistry<C> {

    /**
     * Default caption for {@link EpicJobsCaptionKeys#ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND}.
     */
    public static final String ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND = "Could not find project '{input}'";

    protected EpicJobsCaptionRegistry() {
        super();
        this.registerMessageFactory(
                EpicJobsCaptionKeys.ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND,
                (caption, sender) -> ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND
        );
    }
}
