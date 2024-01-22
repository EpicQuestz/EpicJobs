package com.epicquestz.epicjobs.command.caption;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class EpicJobsCaptionRegistryFactory<C> {

	/**
	 * Create a new EpicJobs caption registry instance
	 *
	 * @return Created instance
	 */
	public @NonNull EpicJobsCaptionRegistry<C> create() {
		return new EpicJobsCaptionRegistry<>();
	}

}