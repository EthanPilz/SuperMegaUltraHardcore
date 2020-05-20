package com.ethanpilz.smuhc.session;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.exceptions.SaveToDatabaseException;
import com.ethanpilz.smuhc.exceptions.arena.ArenaAlreadyExistsException;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ArenaSetupSession {
    private int state;
    private String arenaName;
    private String worldName;
    private String playerUUID;
    private Player player;
    private Location waitingLocation;
    private Location returnLocation;

    public ArenaSetupSession(String playerUUID, String arenaName) {
        this.state = 0;
        this.playerUUID = playerUUID;
        this.arenaName = arenaName;
        this.worldName = "SMUHC_" + this.arenaName;
        this.player = Bukkit.getPlayer(UUID.fromString(this.playerUUID));
        this.selectionMade();

    }

    public void selectionMade() {
        switch (this.state) {
            case 0:
                beginArenaCreation();
                break;
            case 1:
                waitingLocationSelected();
                break;
            case 2:
                returnLocationSelected();
                break;
        }
    }

    private void beginArenaCreation() {
        if (Bukkit.getWorld("SMUCH_" + this.arenaName) != null) {
            SMUHC.arenaCreationManager.removePlayerSetupSession(this.playerUUID);
            this.player.sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "A world with the name of your arena already exists, so you cannot setup this arena.");
        }
        this.player.sendMessage(ChatColor.RED + "-------SuperMegaUltraHardcore-------");
        this.player.sendMessage(ChatColor.WHITE + "To begin the setup process for arena " + ChatColor.GREEN + this.arenaName);
        this.player.sendMessage("");
        this.player.sendMessage(ChatColor.WHITE +"Go to where players will wait before the game starts and execute " + ChatColor.AQUA + "/smuhc here" + ChatColor.WHITE);
        this.player.sendMessage(ChatColor.RED + "--------------------------------------");
        this.state++;

    }

    private void waitingLocationSelected() {
        this.player.sendMessage(ChatColor.RED + "-------SuperMegaUltraHardcore-------");
        this.player.sendMessage(ChatColor.WHITE + "Game " + ChatColor.GREEN + this.arenaName);
        this.player.sendMessage("");
        this.player.sendMessage(ChatColor.WHITE + "Waiting location selected. Go to where players will be sent after the game ends and execute ");
        this.player.sendMessage(ChatColor.AQUA + "/smuhc here" + ChatColor.WHITE + " to use your current location.");
        this.player.sendMessage(ChatColor.RED + "--------------------------------------");
        this.state++;
        this.waitingLocation = player.getLocation();
    }

    private void returnLocationSelected() {
        this.player.sendMessage(ChatColor.RED + "-------SuperMegaUltraHardcore-------");
        this.player.sendMessage(ChatColor.WHITE + "Game " + ChatColor.RED + this.arenaName + ChatColor.WHITE + ":");
        this.player.sendMessage("");
        this.player.sendMessage(ChatColor.WHITE + "Return location selected. This game is finished being set up.");
        this.player.sendMessage(ChatColor.RED + "--------------------------------------");
        this.state++;
        this.returnLocation = player.getLocation();

        //Create Game
        Arena arena = new Arena(arenaName, worldName, waitingLocation, returnLocation, 60);

        //Attempt to store arena in database
        try {
            SMUHC.inputOutput.storeArena(arena);
            SMUHC.arenaController.addArena(arena);
            arena.prepareWorld();

        } catch (SaveToDatabaseException exception) {
            player.sendMessage(SMUHC.smuhcPrefix + "Game setup FAILED due to a database error.");
        } catch (ArenaAlreadyExistsException exception) {
            player.sendMessage(SMUHC.smuhcPrefix + "Game setup FAILED due to there already being an arena with that name in the controller memory.");
        } finally {
            //Terminate setup session
            SMUHC.arenaCreationManager.removePlayerSetupSession(this.playerUUID);
        }
    }
}
