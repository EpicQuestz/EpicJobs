package de.stealwonders.epicjobs;

import de.stealwonders.epicjobs.command.Commands;
import de.stealwonders.epicjobs.storage.Storage;
import org.bukkit.plugin.java.JavaPlugin;

public final class EpicJobs extends JavaPlugin {

	private static EpicJobs instance;

	private Storage storage;
	private Commands commands;

    @Override
    public void onEnable() {
        // Plugin startup logic

		instance = this;

		storage = new Storage();
		commands = new Commands(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

	public static EpicJobs get() {
		return instance;
	}

	public Storage getStorage() {
		return storage;
	}

	public Commands getCommands() {
		return commands;
	}

}