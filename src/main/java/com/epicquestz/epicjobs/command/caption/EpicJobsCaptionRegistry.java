package com.epicquestz.epicjobs.command.caption;

import cloud.commandframework.bukkit.BukkitCaptionRegistry;
import cloud.commandframework.captions.CaptionProvider;

/**
 * Caption registry that uses bi-functions to produce messages
 *
 * @param <C> Command sender type
 */
public class EpicJobsCaptionRegistry<C> extends BukkitCaptionRegistry<C> {

	/**
	 * Default caption for {@link EpicJobsCaptionKeys#ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND}.
	 */
	public static final String ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND = "Could not find project '<input>'";

	/**
	 * Default caption for {@link EpicJobsCaptionKeys#ARGUMENT_PARSE_FAILURE_JOB_NOT_FOUND}.
	 */
	public static final String ARGUMENT_PARSE_FAILURE_JOB_NOT_FOUND = "Could not find job '<input>'";

	protected EpicJobsCaptionRegistry() {
		super();
		this.registerProvider(CaptionProvider.constantProvider(
			EpicJobsCaptionKeys.ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND,
			ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND
		));
		this.registerProvider(CaptionProvider.constantProvider(
			EpicJobsCaptionKeys.ARGUMENT_PARSE_FAILURE_JOB_NOT_FOUND,
			ARGUMENT_PARSE_FAILURE_JOB_NOT_FOUND
		));
	}

}