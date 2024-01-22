package com.epicquestz.epicjobs.command;

public class CommandPermissions {

	// Node separator
	private static final String SEPARATOR = ".";

	// Plugin node
	private static final String PLUGIN = "epicjobs";

	// Command node
	private static final String COMMAND = PLUGIN + SEPARATOR + "command";

	// Command nodes
	private static final String PROJECT_COMMAND = COMMAND + SEPARATOR + "project";
	private static final String JOB_COMMAND = COMMAND + SEPARATOR + "job";

	// Project commands
	public static final String CREATE_PROJECT = PROJECT_COMMAND + SEPARATOR + "create";
	public static final String MODIFY_PROJECT = PROJECT_COMMAND + SEPARATOR + "modify";
	public static final String LIST_ALL_PROJECTS = PROJECT_COMMAND + SEPARATOR + "listall";

	// Job commands
	public static final String CREATE_JOB = JOB_COMMAND + SEPARATOR + "create";
	public static final String MODIFY_JOB = JOB_COMMAND + SEPARATOR + "modify";
	public static final String LIST_ALL_JOBS = JOB_COMMAND + SEPARATOR + "listall";

}
