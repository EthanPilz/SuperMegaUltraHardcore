package com.ethanpilz.smuhc.command;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.exceptions.arena.ArenaDoesNotExistException;
import com.ethanpilz.smuhc.exceptions.game.GameInProgressException;
import com.ethanpilz.smuhc.experience.arena.ArenaSetupSessionAlreadyInProgress;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(SMUHC.consolePrefix + "Commands need to be used by in-game players");
        } else {

            if (sender.hasPermission("smuhc.admin")) {

                if (args.length < 1) {

                    sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.YELLOW + "No sub command provided. Options: help, version, reload");

                } else if (args[0].equalsIgnoreCase("reload")) {

                    sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.GREEN + "Config file reloaded.");
                    SMUHC.instance.reloadConfig();
                    SMUHC.instance.saveConfig();
                    SMUHC.instance.saveDefaultConfig();

                } else if (args[0].equalsIgnoreCase("version")) {

                    sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.YELLOW + "Version" + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE + SMUHC.smuhcPluginVersion);

                } else if (args[0].equalsIgnoreCase("join")) {
                    if (args.length == 2) {
                        String arenaName = args[1];
                        //All is good, begin the play process handled by the ArenaCreation manager
                        try {
                            SMUHC.arenaController.getArena(arenaName).getGameManager().getPlayerManager().playerJoinGame(SMUHC.playerController.getPlayer((Player) sender));
                        } catch (ArenaDoesNotExistException exception) {
                            sender.sendMessage(SMUHC.smuhcPrefix + "Game x does not exist.");
                        } catch (GameInProgressException exception) {
                            sender.sendMessage(SMUHC.smuhcPrefix + "The game in x is already in progress. You'll need to wait until it's over to join.");
                        }

                    }
                } else if (args[0].equalsIgnoreCase("setup") || args[0].equalsIgnoreCase("create")) {
                    //Correct Syntax: /f13 setup [arenaName]
                    if (args.length == 2) {
                        String arenaName = args[1];

                        //Check to see if the arena with that name already exists
                        if (!SMUHC.arenaController.doesArenaExist(arenaName)) {
                            //All is good, begin the setup process handled by the ArenaCreation manager
                            try {
                                SMUHC.arenaCreationManager.startSetupSession(((Player) sender).getUniqueId().toString(), arenaName);
                            } catch (ArenaSetupSessionAlreadyInProgress exception) {
                                //They already have a setup session in progress
                                sender.sendMessage(SMUHC.smuhcPrefix + "You already have an arena setup session in progress. You must finish that session before starting a new one.");
                            }
                        } else {
                            //An arena with that name already exists in the arena controller memory
                            sender.sendMessage(SMUHC.smuhcPrefix + "Game " + ChatColor.RED + arenaName + " already exists. Please choose another name and try again.");
                        }
                    } else {
                        //Incorrect setup syntax
                        sender.sendMessage(SMUHC.smuhcPrefix + "Incorrect setup syntax. Usage: " + ChatColor.AQUA + "/SMUHC setup [arenaName]");
                    }

                } else if (args[0].equalsIgnoreCase("here")) {

                    if (SMUHC.arenaCreationManager.doesUserHaveActiveSession(((Player) sender).getUniqueId().toString())) {
                        //Make the selection
                        SMUHC.arenaCreationManager.getPlayerSetupSession(((Player) sender).getUniqueId().toString()).selectionMade();
                    } else {
                        //There is no active setup session
                        sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "You currently have no setup session in progress.");
                    }

                } else if (args[0].equalsIgnoreCase("cancel")) {
                    if (SMUHC.arenaCreationManager.doesUserHaveActiveSession(((Player) sender).getUniqueId().toString())) {
                        SMUHC.arenaCreationManager.removePlayerSetupSession(((Player) sender).getUniqueId().toString());
                        sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.GREEN + "Setup session cancelled successfully");


                    } else if (args[0].equalsIgnoreCase("arenas")) {
                        if (SMUHC.arenaController.getNumberOfArenas() > 0) {
                            sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.STRIKETHROUGH + "---" + ChatColor.RESET + "Arenas" + ChatColor.STRIKETHROUGH + "---");

                            //Print all arenas
                            Iterator it = SMUHC.arenaController.getArenas().entrySet().iterator();
                            int count = 1;
                            while (it.hasNext()) {
                                Map.Entry entry = (Map.Entry) it.next();
                                Arena arena = (Arena) entry.getValue();

                                sender.sendMessage(count++ + ".) " + arena.getName());
                            }
                        } else {
                            //There are no arenas
                            sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "There are no arenas to display.");
                        }


                    } else {
                        sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Unknown command " + ChatColor.AQUA + args[0] + ChatColor.RED + ". These look like commands to me: " + ChatColor.YELLOW + "reload, version, join, setup.");

                    }
                 }
            } else {
                sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "You don't have permissions.");
            }
        } return true;
    }
}


