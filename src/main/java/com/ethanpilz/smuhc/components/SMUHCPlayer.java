package com.ethanpilz.smuhc.components;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.components.level.SMUHCPlayerLevel;
import com.ethanpilz.smuhc.exceptions.SaveToDatabaseException;
import com.ethanpilz.smuhc.experience.XPManager;
import com.ethanpilz.smuhc.manager.display.WaitingPlayerStatsDisplayManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

public class SMUHCPlayer {

    private SMUHCPlayer smuhcPlayer;
    protected Arena arena;
    private XPManager xpManager;


    //UUID
    private String playerUUID;

    //Statistics
    private SMUHCPlayerLevel level;
    private int experiencePoints;

    //Waiting Scoreboard
    private WaitingPlayerStatsDisplayManager waitingPlayerStatsDisplayManager;

    public SMUHCPlayer(String uuid) {
        this.playerUUID = uuid;

        //Display
        waitingPlayerStatsDisplayManager = new WaitingPlayerStatsDisplayManager(this);
    }

    public SMUHCPlayer(String uuid, int xp) {
        this.playerUUID = uuid;

        //Values
        experiencePoints = xp;
        determineLevel();
        //FridayThe13th.inputOutput.loadPlayerPurchases(this);

        //Display
        waitingPlayerStatsDisplayManager = new WaitingPlayerStatsDisplayManager(this);
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(UUID.fromString(getPlayerUUID()));

    }

    /**
     * @return SMUHCPlayer object
     */
    public SMUHCPlayer getSMUHCPlayer() {
        return smuhcPlayer;
    }

    /**
     * Updates player in the database
     */

    public void updateDB() {
        SMUHC.inputOutput.updatePlayer(this);
    }

    /**
     * Stores the player in the database
     */
    public void storeToDB() {
        try {
            SMUHC.inputOutput.storePlayer(this);
        } catch (SaveToDatabaseException exception) {
            //Ruh-roh raggy. Couldn't save them, probably means they already exist
            SMUHC.log.log(Level.WARNING, "Encountered an unexpected error while attempting to save F13 player to database.");
        }
    }

    /**
     * Returns the player's experience points
     *
     * @return
     */

    public int getXP() {
        return experiencePoints;
    }

    /**
     * Adds XP to the player's XP balance
     *
     * @param value
     */

    public void addXP(int value) {
        experiencePoints += Math.min(Math.max(value, 0), Integer.MAX_VALUE);
        updateDB();

        SMUHCPlayerLevel prevLevel = getLevel();
        determineLevel();

        if (getLevel() != prevLevel && isOnline()) {
            //They leveled up
            getBukkitPlayer().sendMessage(SMUHC.smuhcPrefix + "Congratulations! You've leveled up to level " + getLevel().getLevelNumber());
        }
    }

    /**
     * Returns the players level
     *
     * @return
     */

    public SMUHCPlayerLevel getLevel() {
        return level;
    }

    /**
     * Determines the players level
     */

    private void determineLevel() {
        for (SMUHCPlayerLevel l : SMUHCPlayerLevel.values()) {
            if (getXP() >= l.getMinXP() && getXP() < l.getMaxXP()) {
                this.level = l;
            }
        }
    }

    /**
     * Returns the next level for the player
     *
     * @return
     */

    public SMUHCPlayerLevel getNextLevel() {
        SMUHCPlayerLevel level = getLevel();

        for (SMUHCPlayerLevel l : SMUHCPlayerLevel.values()) {
            if (l.getLevelNumber() == (getLevel().getLevelNumber() + 1)) {
                level = l;
                break;
            }
        }
        return level;
    }

    /**
     * @return If the Bukkit player is online
     */

    public boolean isOnline() {
        return Bukkit.getOnlinePlayers().contains(getBukkitPlayer());
    }



    /**
     * Removes all active potion effects for the player
     */
    void removeAllPotionEffects() {
        if (getSMUHCPlayer().isOnline()) {
            //Remove all potion effects
            for (PotionEffect effect : getSMUHCPlayer().getBukkitPlayer().getActivePotionEffects()) {
                getSMUHCPlayer().getBukkitPlayer().removePotionEffect(effect.getType());
            }
        }
    }


    /**
     * Makes the player visible to all other players on the server
     */

    public void makePlayerVisibleToEveryone(boolean show) {
        //Make them visible to everyone again
        if (getSMUHCPlayer().getBukkitPlayer().isOnline()) {
            //Make visible to all players
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (show) {
                    player.showPlayer(getSMUHCPlayer().getBukkitPlayer());
                } else {
                    player.hidePlayer(getSMUHCPlayer().getBukkitPlayer());
                }

            }
        }

    }

    /**
     * Returns the manager for the waiting player stats scoreboard
     *
     * @return
     */
    public WaitingPlayerStatsDisplayManager getWaitingPlayerStatsDisplayManager() {
        return waitingPlayerStatsDisplayManager;
    }

}
