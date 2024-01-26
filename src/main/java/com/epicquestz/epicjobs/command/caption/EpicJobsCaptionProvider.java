package com.epicquestz.epicjobs.command.caption;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.caption.DelegatingCaptionProvider;

public final class EpicJobsCaptionProvider<C> extends DelegatingCaptionProvider<C> {

	/**
	 * Default caption for {@link EpicJobsCaptionKeys#ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND}.
	 */
	public static final String ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND = "Could not find project '<input>'";

	/**
	 * Default caption for {@link EpicJobsCaptionKeys#ARGUMENT_PARSE_FAILURE_JOB_NOT_FOUND}.
	 */
	public static final String ARGUMENT_PARSE_FAILURE_JOB_NOT_FOUND = "Could not find job '<input>'";

	private static final CaptionProvider<?> PROVIDER = CaptionProvider.constantProvider()
		.putCaption(
			EpicJobsCaptionKeys.ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND,
			ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND
		).putCaption(
			EpicJobsCaptionKeys.ARGUMENT_PARSE_FAILURE_JOB_NOT_FOUND,
			ARGUMENT_PARSE_FAILURE_JOB_NOT_FOUND
		).build();

	@Override
	public @NonNull CaptionProvider<C> delegate() {
		return (CaptionProvider<C>) PROVIDER;
	}

}
