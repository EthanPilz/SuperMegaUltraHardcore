package com.ethanpilz.smuhc.command;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.exceptions.arena.ArenaDoesNotExistException;
import com.ethanpilz.smuhc.exceptions.game.GameInProgressException;
import com.ethanpilz.smuhc.exceptions.player.PlayerNotPlayingException;
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
                        //All is good, begin the play process handled by the GameManager and PlayerManager
                        try {
                            SMUHC.arenaController.getArena(arenaName).getGameManager().getPlayerManager().playerJoinGame(SMUHC.playerController.getPlayer((Player) sender));
                        } catch (ArenaDoesNotExistException exception) {
                            sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Game " + ChatColor.AQUA + args[1] + ChatColor.RED + " does not exist.");
                        } catch (GameInProgressException exception) {
                            sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Game " + ChatColor.AQUA + args[1] + ChatColor.RED + " is already in progress.");
                        }

                    } else {
                        sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "You must specify a game to join.");
                    }
                } else if (args[0].equalsIgnoreCase("setup") || args[0].equalsIgnoreCase("create")) {
                    //Correct Syntax: /smuhc setup [arenaName]
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
                            sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Game " + ChatColor.AQUA + arenaName + ChatColor.RED + " already exists. Please choose another name and try again.");
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
                    }

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

                } else if (args[0].equalsIgnoreCase("arena")) {
                    if (args.length == 2) {
                        String arenaName = args[1];

                        try {
                            Arena arena = SMUHC.arenaController.getArena(arenaName);

                            sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.STRIKETHROUGH + "-----" + ChatColor.RESET + ChatColor.RED + arena.getName() + ChatColor.WHITE + ChatColor.STRIKETHROUGH + " -----");

                            if (arena.getGameManager().isGameEmpty()) {
                                sender.sendMessage("Game Status: " + ChatColor.RED + "Empty");
                            } else if (arena.getGameManager().isGameWaiting()) {
                                sender.sendMessage("Game Status: " + ChatColor.GOLD + "Waiting");
                            } else if (arena.getGameManager().isGameInProgress()) {
                                sender.sendMessage("Game Status: " + ChatColor.GREEN + "In Progress");

                                int rem = arena.getGameManager().getGameTimeLeft() % 3600;
                                int mn = rem / 60;
                                int sec = rem % 60;
                                sender.sendMessage("Time Left: " + mn + "m " + sec + "sec");
                                sender.sendMessage("# Players: " + arena.getGameManager().getPlayerManager().getNumberOfPlayers());

                                if (arena.getGameManager().getPlayerManager().getNumberOfSpectators() > 0) {
                                    sender.sendMessage("# Spectators: " + arena.getGameManager().getPlayerManager().getNumberOfSpectators());
                                }

                                sender.sendMessage(ChatColor.STRIKETHROUGH + "--------------");
                            }

                        } catch (ArenaDoesNotExistException exception) {
                            sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Game " + ChatColor.AQUA + arenaName + " does not exist.");
                        }
                    } else {
                        //Incorrect syntax
                        sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Incorrect arena syntax. Usage:" + ChatColor.AQUA + "/smuhc arena [arenaName]");
                    }

                } else if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("stop")) {
                    if (args.length == 2) {
                        String arenaName = args[1];

                        try {
                            Arena arena = SMUHC.arenaController.getArena(arenaName);

                            if (arena.getGameManager().isGameInProgress()) {
                                arena.getGameManager().gameTimeUp();
                                sender.sendMessage(SMUHC.smuhcPrefix + "Game in " + ChatColor.AQUA + arenaName + " ended.");
                            } else {
                                //The game is not in progress, thus we can't end it
                                sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "There is no in progress game " + ChatColor.AQUA + arenaName + ChatColor.RED + " to end.");
                            }
                        } catch (ArenaDoesNotExistException exception) {
                            sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Game " + ChatColor.AQUA + arenaName + ChatColor.RED + " does not exist.");
                        }
                    } else {
                        //Incorrect setup syntax
                        sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Incorrect arena syntax. Usage:" + ChatColor.AQUA + "/smuhc end [arenaName]");
                    }

                } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
                    //Correct Syntax: /f13 setup [arenaName]
                    if (args.length == 2) {
                        String arenaName = args[1];

                        //Check to see if the arena with that name already exists
                        if (SMUHC.arenaController.doesArenaExist(arenaName)) {
                            //End the game and remove the arena
                            try {
                                Arena arena = SMUHC.arenaController.getArena(arenaName);

                                if (!arena.getGameManager().isGameEmpty()) {
                                    arena.getGameManager().getPlayerManager().sendMessageToAllPlayers(SMUHC.smuhcPrefix + ChatColor.RED + "The game has ended because the arena is being deleted right now!");
                                    arena.getGameManager().gameTimeUp();
                                }

                                arena.delete();
                                sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.GREEN + "Game " + ChatColor.AQUA + arenaName + ChatColor.GREEN + " has been deleted successfully.");

                            } catch (ArenaDoesNotExistException exception) {
                                //yuh get it to it
                            }

                        } else {
                            //An arena with that name doesn't exist in the arena controller memory
                            sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.GREEN + "Game " + ChatColor.AQUA + arenaName + ChatColor.GREEN + " does not exist.");
                        }
                    } else {
                        //Incorrect setup syntax
                        sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Incorrect delete syntax. Usage: " + ChatColor.AQUA + "/smuhc delete [arenaName]");
                    }

                } else if (args[0].equalsIgnoreCase("quit") || args[0].equalsIgnoreCase("leave")) {
                    try {
                        Arena arena = SMUHC.arenaController.getPlayerArena(SMUHC.playerController.getPlayer((Player) sender));
                        arena.getGameManager().getPlayerManager().onPlayerQuit(SMUHC.playerController.getPlayer((Player) sender));
                    } catch (PlayerNotPlayingException exception) {
                        sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "You are not in a game, therefore you cannot leave one.");
                    }

                    } else {
                        sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Unknown command " + ChatColor.AQUA + args[0] + ChatColor.RED + ". These look like commands to me: \n" + ChatColor.YELLOW + "reload, version, join, quit, setup, cancel, end, arena, delete");

                    }
                 } else {
            sender.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "You don't have permissions to access this command.");
        }

        } return true;
    }
}


