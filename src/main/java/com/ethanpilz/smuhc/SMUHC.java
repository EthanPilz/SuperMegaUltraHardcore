package com.ethanpilz.smuhc;

import com.ethanpilz.smuhc.command.AdminCommand;
import com.ethanpilz.smuhc.command.SuperMegaDeathRocketCommand;
import com.ethanpilz.smuhc.components.rocket.SuperMegaDeathRocketComponent;
import com.ethanpilz.smuhc.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SMUHC extends JavaPlugin {

    public static final String smuhcPrefix = ChatColor.DARK_GRAY + "[" + ChatColor.RESET + ChatColor.RED + "SMUHC" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;
    public static final Logger log = Logger.getLogger("Minecraft");
    public static Plugin plugin;
    public static final String consolePrefix = "[SuperMegaUltraHardcore]";

    @Override
    public void onEnable(){

        long startTimeInMilliseconds = System.currentTimeMillis();

        //Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //Listener
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        //Register Plugin for tasks
        plugin = this;

        //Commands
        getCommand("supermegadeathrocket").setExecutor(new SuperMegaDeathRocketCommand());
        getCommand("smuhc").setExecutor(new AdminCommand());

        //Recipe
        Bukkit.addRecipe(SuperMegaDeathRocketComponent.Recipe());

        //Startup complete
        Bukkit.getLogger().log(Level.INFO, consolePrefix + " Startup complete - took " + (System.currentTimeMillis() - startTimeInMilliseconds) + " ms");
    }
    public void onDisable(){

        //Logger
        Bukkit.getLogger().log(Level.INFO, consolePrefix + "SuperMegaUltraHardcore disabled.");

        plugin = null;

    }
}
