package de.stealwonders.epicjobs.commands;

import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.constants.Messages;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobCategory;
import de.stealwonders.epicjobs.job.JobStatus;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.project.ProjectStatus;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    private EpicJobs plugin;

    public CommandManager(EpicJobs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sendCommands(sender);
            return true;
        }

        Player player = (Player) sender;
        EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());

        switch (args[0].toLowerCase()) {

            case "help":
                sendCommands(sender);
                break;
            case "list":
                //todo list all jobs
                //todo job filter should be added

                List<Job> jobs = plugin.getJobManager().getJobs().stream()
                        .filter(job -> job.getJobStatus() == JobStatus.OPEN)
                        .collect(Collectors.toList());

                System.out.println(jobs);
                System.out.println(jobs.size());

                if (jobs.size() > 1) {
                    for (Job job : jobs) {
                        player.sendMessage("#" + job.getId() + " | " + job.getDescription());
                    }
                } else {
                    player.sendMessage("There are no jobs to be claimed.");
                }

                break;
            case "claim":

                if (args.length < 2) {
                    Messages.SPECIFY_JOB_ID.send(player);
                } else {

                    try {
                        int jobId = Integer.parseInt(args[1]);
                        Job job = plugin.getJobManager().getJobById(jobId);

                        if (job == null) {
                            Messages.JOB_DOESNT_EXIST.send(player);
                            return true;
                        }

                        if (job.getJobStatus() == JobStatus.OPEN) {
                            job.setClaimant(player);
                            job.setJobStatus(JobStatus.TAKEN);
                            Messages.ANNOUNCE_JOB_TAKEN.broadcast(player.getName(), jobId);
                        } else {
                            Messages.JOB_NOT_OPEN.send(player);
                        }
                    } catch (NumberFormatException ignore) {
                        player.sendMessage("Please enter a valid number.");
                        return true;
                    }

                }

                break;
            case "abandon":

                if (args.length == 1) {
                    if (epicJobsPlayer.getJobs().size() == 1) {
                        Job job = epicJobsPlayer.getJobs().get(0);
                        job.setClaimant(null);
                        epicJobsPlayer.removeJob(job);
                    } else if (epicJobsPlayer.getJobs().size() == 0) {
                        //todo send message: you have no jobs
                        Messages.PLAYER_HAS_NO_JOBS.send(player);
                    } else {
                        //todo send message: you have more than 1 job
                        Messages.PLAYER_HAS_MULITPLE_JOBS.send(player);
                    }
                } else {

                    try {
                        int jobId = Integer.parseInt(args[1]);
                        Job job = plugin.getJobManager().getJobById(jobId);
                        job.setClaimant(null);
                        epicJobsPlayer.removeJob(job);
                    } catch (NumberFormatException ignore) {
                        player.sendMessage("Please enter a valid number.");
                        return true;
                    }

                }

                break;
            case "tp":

                if (args.length == 1) {
                    if (epicJobsPlayer.getJobs().size() == 1) {
                        Job job = epicJobsPlayer.getJobs().get(0);
                        player.teleport(job.getLocation());
                    } else if (epicJobsPlayer.getJobs().size() == 0) {
                        //todo send message: you have no job to teleport to
                        Messages.PLAYER_HAS_NO_JOBS.send(player);
                    } else {
                        //todo send meesage: you have more than 1 job
                        Messages.PLAYER_HAS_NO_JOBS.send(player);
                    }
                } else {

                    try {
                        int jobId = Integer.parseInt(args[1]);
                        Job job = plugin.getJobManager().getJobById(jobId);
                        player.teleport(job.getLocation());
                    } catch (NumberFormatException ignore) {
                        player.sendMessage("Please enter a valid number.");
                        return true;
                    }

                }

                break;
            case "done":

                if (args.length == 1) {
                    if (epicJobsPlayer.getJobs().size() == 1) {
                        Job job = epicJobsPlayer.getJobs().get(0);
                        job.setJobStatus(JobStatus.DONE);
                        //todo inform job creator
                    } else if (epicJobsPlayer.getJobs().size() == 0) {
                        //todo send message: you have no jobs
                        Messages.PLAYER_HAS_NO_JOBS.send(player);
                    } else {
                        //todo send meesage: you have more than 1 job
                        Messages.PLAYER_HAS_NO_JOBS.send(player);
                    }
                } else {

                    try {
                        int jobId = Integer.parseInt(args[1]);
                        Job job = plugin.getJobManager().getJobById(jobId);
                        job.setJobStatus(JobStatus.DONE);
                        //todo inform job creator
                    } catch (NumberFormatException ignore) {
                        player.sendMessage("Please enter a valid number.");
                        return true;
                    }

                }

                break;
            case "complete":

                if (args.length <= 2) {
                    //todo send meesage: specify a job
                    Messages.SPECIFY_JOB_ID.send(player);
                } else {

                    try {
                        int jobId = Integer.parseInt(args[1]);
                        Job job = plugin.getJobManager().getJobById(jobId);
                        job.setJobStatus(JobStatus.COMPLETE);
                        //todo inform job claimant
                    } catch (NumberFormatException ignore) {
                        player.sendMessage("Please enter a valid number.");
                        return true;
                    }

                }

                break;
            case "reopen":

                if (args.length <= 2) {
                    //todo send meesage: specify a job
                    Messages.SPECIFY_JOB_ID.send(player);
                } else {

                    try {
                        int jobId = Integer.parseInt(args[1]);
                        Job job = plugin.getJobManager().getJobById(jobId);
                        job.setJobStatus(JobStatus.REOPENED);
                        //todo inform job claimant
                    } catch (NumberFormatException ignore) {
                        player.sendMessage("Please enter a valid number.");
                        return true;
                    }

                }

                break;
            case "unassign":

                if (args.length <= 2) {
                    //todo send meesage: specify a job
                    Messages.SPECIFY_JOB_ID.send(player);
                } else {

                    try {
                        int jobId = Integer.parseInt(args[1]);
                        Job job = plugin.getJobManager().getJobById(jobId);
                        job.setClaimant(null);
                        //todo inform job claimant
                    } catch (NumberFormatException ignore) {
                        player.sendMessage("Please enter a valid number.");
                        return true;
                    }

                }

                break;
            case "assign":

                if (args.length <= 2) {
                    //todo send message: specify a job
                } else {

                    if (args.length == 3) {

                        try {
                            int jobId = Integer.parseInt(args[1]);
                            Job job = plugin.getJobManager().getJobById(jobId);
                            Player target = Bukkit.getPlayer(args[2]);
                            if (target != null) {
                                job.setClaimant(target);
                                //todo inform job claimant
                            } else {
                                //todo send message: player is not online
                                Messages.PLAYER_NOT_FOUND.send(player, args[2]);
                            }
                        } catch (NumberFormatException ignore) {
                            player.sendMessage("Please enter a valid number.");
                            return true;
                        }
                    }

                }

                break;
            case "create":

                if (args.length < 4) {
                    player.sendMessage("/job create <project> <category> <description>");
                } else {

                    Project project = plugin.getProjectManager().getProjectByName(args[1]);
                    JobCategory jobCategory = JobCategory.getJobCategoryByName(args[2]);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 3; i < args.length; i++) {
                        stringBuilder.append(args[i] + " ");
                    }

                    if (project == null) {
                        Messages.JOB_DOESNT_EXIST.send(player);
                        return true;
                    }

                    if (jobCategory == null) {
                        Messages.CATEGORY_DOESNT_EXIST.send(player);
                        return true;
                    }

                    if (stringBuilder.toString().isEmpty()) {
                        player.sendMessage("Please provide a job description.");
                    }

                    int id = plugin.getJobManager().getFreeId();
                    Job job = new Job(id, player, stringBuilder.toString(), jobCategory, project);
                    plugin.getJobManager().addJob(job);

                    player.sendMessage("Successfully created job with id #" + id);
                }

                break;
            case "remove":

                if (args.length < 2) {
                    player.sendMessage("Please specify a job.");
                } else {

                    if (args.length == 2) {

                        try {
                            int jobId = Integer.parseInt(args[1]);
                            Job job = plugin.getJobManager().getJobById(jobId);

                            if (job != null) {
                                job.getProject().removeJob(job);
                                plugin.getJobManager().removeJob(job);

                                EpicJobsPlayer target = plugin.getEpicJobsPlayer(job.getClaimant());
                                if (target != null) {
                                    target.removeJob(job);
                                }
                                player.sendMessage("Successfully deleted job.");
                            } else {
                                Messages.JOB_DOESNT_EXIST.send(player);
                            }

                        } catch (NumberFormatException ignore) {
                            player.sendMessage("Please enter a valid number.");
                            return true;
                        }
                    }

                }

                break;
            case "edit":

                if (args.length < 4) {
                    //todo: return how to use command
                } else {

                    try {
                        int jobId = Integer.parseInt(args[1]);
                        Job job = plugin.getJobManager().getJobById(jobId);

                        if (job != null) {
                            switch (args[2].toLowerCase()) {
                                case "description":

                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 3; i < args.length; i++) {
                                        stringBuilder.append(args[i] + " ");
                                    }
                                    job.setDescription(stringBuilder.toString());

                                    break;
                                case "project":

                                    Project project = plugin.getProjectManager().getProjectByName(args[2]);
                                    if (project != null) {
                                        job.setProject(project);
                                        //todo send message: success
                                    } else {
                                        //todo send message: project dont exist
                                        Messages.PROJECT_DOESNT_EXIST.send(player);
                                    }

                                    break;
                                case "category":

                                    JobCategory jobCategory = JobCategory.valueOf(args[2]);
                                    if (jobCategory != null) {
                                        job.setJobCategory(jobCategory);
                                        //todo send message: success
                                    } else {
                                        //todo send message: project dont exist
                                        Messages.PROJECT_DOESNT_EXIST.send(player);
                                    }

                                    break;
                                case "location":
                                    job.setLocation(player.getLocation());
                                    //todo send message: success
                                    break;
                                default:
                                    //todo send message: edit command usage
                            }
                        } else {
                            //todo send message: job dont exist
                            Messages.JOB_DOESNT_EXIST.send(player);
                        }

                    } catch (NumberFormatException ignore) {
                        player.sendMessage("Please enter a valid number.");
                        return true;
                    }


                }

                break;
            case "stats":
                break;

            case "createproject":

                if (args.length == 1) {
                    player.sendMessage("You must specify a name to create a project.");
                } else if (args.length == 2) {
                    int id = plugin.getProjectManager().getFreeId();
                    Project project = new Project(id, args[1], player);
                    plugin.getProjectManager().addProject(project);
                    player.sendMessage("Successfully created project " + args[1]);
                } else if (args.length == 3) {
                    Player target = Bukkit.getPlayer(args[2]);
                    if (target != null) {
                        int id = plugin.getProjectManager().getFreeId();
                        Project project = new Project(id, args[1], target);
                        plugin.getProjectManager().addProject(project);
                        player.sendMessage("Successfully created project " + args[1]);
                    } else {
                        Messages.PLAYER_NOT_FOUND.send(player, args[2]);
                    }
                } else {
                    //todo sendmessage: commandusage
                }
                break;

            case "project":

                if (args.length >=2) {

                    if (args[1].equalsIgnoreCase("list")) {
                        //todo: list projects
                    }

                    String projectName = args[1];
                    Project project = plugin.getProjectManager().getProjectByName(projectName);
                    if (project != null) {

                        if (args.length > 2) {

                            switch (args[2].toLowerCase()) {
                                case "edit":
                                    if (args.length > 3) {
                                        switch (args[3].toLowerCase()) {
                                            case "location":
                                                project.setLocation(player.getLocation());
                                                //todo sendmessage: success
                                                break;
                                            case "leader":
                                                Player target = Bukkit.getPlayer(args[4]);
                                                if (target != null) {
                                                    project.setLeader(player);

                                                    target.sendActionBar("hello");

                                                    //todo send message: success
                                                } else {
                                                    //todo send message: cant find player
                                                    Messages.PLAYER_NOT_FOUND.send(player, args[4]);
                                                }
                                                break;
                                        }
                                    } else {
                                        player.sendMessage("Please specify a valid subcommand.");
                                    }

                                    break;
                                case "tp":
                                    player.teleport(project.getLocation());
                                    break;
                                case "complete":
                                    project.setProjectStatus(ProjectStatus.COMPLETE);
                                    //todo inform builders or smth
                                    break;
                                default:
                                    //todo send command lsit?
                            }
                        } else {
                            player.sendMessage("Please specify a subcommand.");
                        }

                    } else {
                        player.sendMessage("A project of the name " + args[1] + " could not be found.");
                    }

                } else {
                    player.sendMessage("Please provide a project name.");
                }

                break;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {




        return null;
    }

    private void sendCommands(CommandSender sender) {
        sender.sendMessage("/job[s] - Lists all the job commands that a player has access to\n" +
                "/job[s] help - Lists all the job commands that a player has access to\n" +
                "\n" +
                "/job[s] list [<filter>] - Lists all the available jobs\n" +
                "The filter will be able to filter jobs depending on Category and Project (and Status for Admins/Leaders?)\n" +
                "\n" +
                "/job[s] claim <id> - Claim a job for yourself\n" +
                "/job[s] abandon <id> - Abandon a job if it’s too hard or want to free it up\n" +
                "\n" +
                "/job[s] tp [<id>] - Will teleport you to the jobsite\n" +
                "If the player only has one job claimed then he doesn’t have to provide the ID\n" +
                "/job[s] done [<id>] - Will mark a job as done so a manager / designer can review it\n" +
                "If the player only has one job he doesn’t need to specify the id \n" +
                "\n" +
                "\n" +
                "/job[s] complete <id> - Marks a job as complete\n" +
                "/job[s] reopen <id> - Tells a player to redo his job as the work he has done is bad\n" +
                "\n" +
                "/job[s] unassign <id> - Unassigns the current assignee from the job\n" +
                "/job[s] assign <id> <player> - Assigns the given player to the job\n" +
                "\n" +
                "/job[s] create <project> [<category>] <description>\n" +
                "/job[s] remove <id>\n" +
                "/job[s] edit <id> description / project / category / location <value>\n" +
                "\n" +
                "/job[s] stats <player> - Showcases the amount of totally completed jobs and more\n" +
                "\n" +
                "/job[s] createproject <name> [<leader>]\n" +
                "If no leader is defined then the project creator will be the leader\n" +
                "/job[s] project <name> edit name <new name>\n" +
                "/job[s] project <name> edit location\n" +
                "/job[s] project <name> edit leader <player>\n" +
                "/job[s] project <name> tp\n" +
                "/job[s] project <name> complete - Marks the project as completed\n");
    }
}
