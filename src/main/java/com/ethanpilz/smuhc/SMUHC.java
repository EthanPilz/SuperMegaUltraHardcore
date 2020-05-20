package com.ethanpilz.smuhc;

import com.ethanpilz.smuhc.command.CommandHandler;
import com.ethanpilz.smuhc.command.SuperMegaDeathRocketCommand;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.components.rocket.SuperMegaDeathRocketComponent;
import com.ethanpilz.smuhc.controller.ArenaController;
import com.ethanpilz.smuhc.controller.PlayerController;
import com.ethanpilz.smuhc.io.InputOutput;
import com.ethanpilz.smuhc.listener.BlockListener;
import com.ethanpilz.smuhc.listener.PlayerListener;
import com.ethanpilz.smuhc.manager.setup.ArenaCreationManager;
import com.ethanpilz.smuhc.runnable.PlayerMemoryClean;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SMUHC extends JavaPlugin {

    public static final String smuhcPrefix = ChatColor.DARK_GRAY + "[" + ChatColor.RESET + ChatColor.RED + "SMUHC" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static SMUHC instance;
    public static final String consolePrefix = "[SuperMegaUltraHardcore] ";
    public static final String smuhcPluginVersion = "1.0";
    public static final String signPrefix = ChatColor.BLACK + "[" + ChatColor.DARK_RED + "SMUHC" + ChatColor.BLACK + "]";

    //Game Components
    public static ArenaController arenaController;
    public static PlayerController playerController;

    //Global Managers
    public static InputOutput inputOutput;
    public static ArenaCreationManager arenaCreationManager;

    @Override
    public void onEnable(){

        long startTimeInMilliseconds = System.currentTimeMillis();

        // Register Plugin for tasks
        SMUHC.instance = this;

        // Config
        getConfig().addDefault("broadcast", true);
        this.saveDefaultConfig();

        // Initialize Game Components
        arenaController = new ArenaController();
        playerController = new PlayerController();
        arenaCreationManager = new ArenaCreationManager();

        // InputOutput
        inputOutput = new InputOutput();
        inputOutput.prepareDB();
        inputOutput.updateDB();
        inputOutput.loadArenas();
        inputOutput.loadSigns();

        // forEach loop for Arena hashmap to load every world for each arena or create a new one.
        arenaController.getArenas().forEach((s, arena) -> {
            getServer().createWorld(new WorldCreator(arena.getWorldName()));
                });

        // Listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);

        // Schedule tasks
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new PlayerMemoryClean(), 36000, 36000);

        if (!Bukkit.getPluginManager().isPluginEnabled("SidebarAPI")) {
            log.log(Level.SEVERE, consolePrefix + "Sidebar API not found - required for gameplay!!");
            this.setEnabled(false);
            return;
        }

        // Commands
        getCommand("supermegadeathrocket").setExecutor(new SuperMegaDeathRocketCommand());
        getCommand("smuhc").setExecutor(new CommandHandler());

        // Recipe
        Bukkit.addRecipe(SuperMegaDeathRocketComponent.Recipe());

        // Schedule tasks
        // Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new PlayerMemoryClean(), 36000, 36000);

        // Startup complete
        Bukkit.getLogger().log(Level.INFO, consolePrefix + " Startup complete. Took " + (System.currentTimeMillis() - startTimeInMilliseconds) + " ms");
    }

    public void onDisable(){

        // End every game, restore players, etc.
        for (Arena arena : arenaController.getArenas().values())
        {
            arena.getGameManager().clearArena();
        }

        // Logger
        Bukkit.getLogger().log(Level.INFO, consolePrefix + "SuperMegaUltraHardcore disabled.");
        InputOutput.freeConnection();

        instance = null;

    }

}
