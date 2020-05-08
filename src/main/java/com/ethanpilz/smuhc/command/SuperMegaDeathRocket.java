package com.ethanpilz.smuhc.command;

import com.ethanpilz.smuhc.SMUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SuperMegaDeathRocket implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender.hasPermission("smuhc.smdr")) {

            Bukkit.getServer().broadcastMessage(SMUHC.smuhcPrefix + ChatColor.AQUA + sender.getName() + ChatColor.DARK_GRAY + ":" + ChatColor.GREEN + " Wanna see something" + ChatColor.ITALIC + " REALLY " + ChatColor.RESET + ChatColor.GREEN + "cool?");

        }
        return true;
    }
}
