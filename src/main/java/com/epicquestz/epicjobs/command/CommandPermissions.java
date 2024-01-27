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
	public static final String LIST_ALL_PROJECTS = PROJECT_COMMAND + SEPARATOR + "listall";

	// Dunno if this is good!?!?
	private static final String MODIFY_PROJECT = PROJECT_COMMAND + SEPARATOR + "modify";
//	public static final String MODIFY_PROJECT_ALL = MODIFY_PROJECT + SEPARATOR + "all";
	public static final String MODIFY_PROJECT_NAME = MODIFY_PROJECT + SEPARATOR + "name";
	public static final String MODIFY_PROJECT_LOCATION = MODIFY_PROJECT + SEPARATOR + "location";
	public static final String MODIFY_PROJECT_LEADER = MODIFY_PROJECT + SEPARATOR + "leader";
	public static final String MODIFY_PROJECT_DESCRIPTION = MODIFY_PROJECT + SEPARATOR + "description";
	public static final String MODIFY_PROJECT_STATUS = MODIFY_PROJECT + SEPARATOR + "status";
	public static final String MODIFY_PROJECT_PAUSE = MODIFY_PROJECT + SEPARATOR + "pause";
	public static final String MODIFY_PROJECT_RESUME = MODIFY_PROJECT + SEPARATOR + "resume";
	public static final String MODIFY_PROJECT_COMPLETE = MODIFY_PROJECT + SEPARATOR + "complete";


	// Job commands
	public static final String CREATE_JOB = JOB_COMMAND + SEPARATOR + "create";
	public static final String DELETE_JOB = JOB_COMMAND + SEPARATOR + "delete";

	public static final String LIST_JOBS = JOB_COMMAND + SEPARATOR + "list";
	public static final String LIST_ALL_JOBS = LIST_JOBS + SEPARATOR + "all";
	public static final String LIST_DONE_JOBS = LIST_JOBS + SEPARATOR + "done";

	public static final String CLAIM_JOB = JOB_COMMAND + SEPARATOR + "claim";
	public static final String COMPLETE_JOB = JOB_COMMAND + SEPARATOR + "complete";
	public static final String REOPEN_JOB = JOB_COMMAND + SEPARATOR + "reopen";
	public static final String ASSIGN_JOB = JOB_COMMAND + SEPARATOR + "assign";
	public static final String UNASSIGN_JOB = JOB_COMMAND + SEPARATOR + "unassign";


	private static final String MODIFY_JOB = JOB_COMMAND + SEPARATOR + "modify";
	public static final String MODIFY_JOB_DESCRIPTION = MODIFY_JOB + SEPARATOR + "description";
	public static final String MODIFY_JOB_LOCATION = MODIFY_JOB + SEPARATOR + "location";


	public static final String SHOW_STATISTICS = COMMAND + SEPARATOR + "statistics";

}
