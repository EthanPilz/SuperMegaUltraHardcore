package com.ethanpilz.smuhc;

import com.ethanpilz.smuhc.command.SuperMegaDeathRocket;
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

    @Override
    public void onEnable(){

        //Logger
        Bukkit.getLogger().log(Level.INFO, smuhcPrefix + "SuperMegaUltraHardcore enabled.");

        //Listener
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        //Register Plugin for tasks
        plugin = this;

        //Commands
        getCommand("supermegadeathrocket").setExecutor(new SuperMegaDeathRocket());



    }
    public void onDisable(){

        //Logger
        Bukkit.getLogger().log(Level.INFO, smuhcPrefix + "SuperMegaUltraHardcore disabled.");

    }
}
