package com.ethanpilz.smuhc.command;

import com.ethanpilz.smuhc.SMUHC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (sender instanceof Player) {
                if (sender.hasPermission("smuhc.admin")) {
                    if (args.length < 1) {
                        sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.YELLOW + "No sub command provided. Options: help, version, reload");

                    } else if (args[0].equalsIgnoreCase("reload")) {
                        sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.GREEN + "Config file reloaded.");
                        SMUHC.plugin.reloadConfig();
                        SMUHC.plugin.saveConfig();
                        SMUHC.plugin.saveDefaultConfig();
                    }

                } else {
                    sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "You don't have permissions.");
                }

            } else {
                if (args.length < 1) {
                    sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.YELLOW + "No sub command provided. Options: help, version, reload");

                } else if (args[0].equalsIgnoreCase("reload")) {
                    SMUHC.plugin.reloadConfig();
                    SMUHC.plugin.saveConfig();
                    SMUHC.plugin.saveDefaultConfig();
                    sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.GREEN + "Config file reloaded.");

                } else if (args[0].equalsIgnoreCase("version")) {
                    sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.YELLOW + "Version" + ChatColor.DARK_GRAY + ": " + SMUHC.smuhcPluginVersion);

                }
            } return true;
        }
    }


