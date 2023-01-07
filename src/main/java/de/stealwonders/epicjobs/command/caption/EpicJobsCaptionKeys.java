package de.stealwonders.epicjobs.command.caption;

import cloud.commandframework.captions.Caption;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * {@link Caption} instances for messages in EpicJobs
 */
public class EpicJobsCaptionKeys {

	private static final Collection<Caption> RECOGNIZED_CAPTIONS = new LinkedList<>();

//    /**
//     * Variables: None
//     */

//     /**
//     * Variables: {input}, {min}, {max}
//     */

	/**
	 * Variables: {input}
	 */
	public static final Caption ARGUMENT_PARSE_FAILURE_PROJECT_NOT_FOUND = of("argument.parse.failure.project_not_found");
//    /**
//     * Variables: {input}, {map}
//     */
//    public static final Caption ARGUMENT_PARSE_FAILURE_TEAM_NOT_FOUND = of("argument.parse.failure.team_not_found");
//    public static final Caption ARGUMENT_PARSE_FAILURE_FLAG_NOT_FOUND = of("argument.parse.failure.flag_not_found");
//    public static final Caption ARGUMENT_PARSE_FAILURE_SPAWN_NOT_FOUND = of("argument.parse.failure.spawn_not_found");
//    public static final Caption ARGUMENT_PARSE_FAILURE_GATE_NOT_FOUND = of("argument.parse.failure.gate_not_found");

	private EpicJobsCaptionKeys() { }

	private static @NonNull Caption of(final @NonNull String key) {
		final Caption caption = Caption.of(key);
		RECOGNIZED_CAPTIONS.add(caption);
		return caption;
	}

	/**
	 * Get an immutable collection containing all standard caption keys
	 *
	 * @return Immutable collection of keys
	 */
	public static @NonNull Collection<@NonNull Caption> getStandardCaptionKeys() {
		return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
	}

}