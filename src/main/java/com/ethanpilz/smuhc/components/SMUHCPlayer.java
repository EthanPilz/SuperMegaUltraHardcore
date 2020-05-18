package com.ethanpilz.smuhc.components;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.components.level.SMUHCPlayerLevel;
import com.ethanpilz.smuhc.manager.display.WaitingPlayerStatsDisplayManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class SMUHCPlayer {

    private SMUHCPlayer smuhcPlayer;
    protected Arena arena;
    //private XPManager xpManager;


    //UUID
    private String playerUUID;

    //Statistics
    private SMUHCPlayerLevel level;
    private int experiencePoints;

    //Waiting Scoreboard
    private WaitingPlayerStatsDisplayManager waitingPlayerStatsDisplayManager;

    public SMUHCPlayer(String uuid, int xp) {
        this.playerUUID = uuid;
        experiencePoints = xp;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(UUID.fromString(getPlayerUUID()));

    }

    /**
     * @return Bukkit player
     */
    public Player getPlayer() {
        return getSMUHCPlayer().getBukkitPlayer();
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
     * Prepares the player for the waiting room and teleports them in
     */
    public void enterWaitingRoom() {
        teleportToWaitingRoom();
        //getF13Player().getWaitingPlayerStatsDisplayManager().displayStatsScoreboard();
        makePlayerVisibleToEveryone(true); //In case of invisibility bug

    }

    /**
     * Returns the manager for the waiting player stats scoreboard
     *
     * @return
     */
    public WaitingPlayerStatsDisplayManager getWaitingPlayerStatsDisplayManager() {
        return waitingPlayerStatsDisplayManager;
    }




    /**
     * Removes the character from the game and restores them to pre-game status
     */

    public void leaveGame() {
        teleportToReturnPoint();

        if (arena.getGameManager().isGameWaiting() || arena.getGameManager().isGameEmpty()) {
            if (getSMUHCPlayer().isOnline()) {
                getSMUHCPlayer().getWaitingPlayerStatsDisplayManager().removeStatsScoreboard();
                arena.getGameManager().getWaitingCountdownDisplayManager().hideForPlayer(getSMUHCPlayer().getBukkitPlayer());
                getSMUHCPlayer().getBukkitPlayer().getInventory().clear();

            }
        } else if (arena.getGameManager().isGameInProgress()) {
            if (arena.getGameManager().getPlayerManager().isSpectator(getSMUHCPlayer())) {
                arena.getGameManager().getPlayerManager().leaveSpectator(getSMUHCPlayer());

                //Hide spectator displays
                arena.getGameManager().getGameCountdownManager().hideFromPlayer(getSMUHCPlayer().getBukkitPlayer());
                arena.getGameManager().getGameScoreboardManager().hideFromPlayer(getSMUHCPlayer().getBukkitPlayer());

                //Make them visible to everyone again
                if (getSMUHCPlayer().getBukkitPlayer().isOnline()) {
                    //Make visible to all players
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.showPlayer(getSMUHCPlayer().getBukkitPlayer());
                    }
                }
            }

            if (arena.getGameManager().getPlayerManager().isPlaying(getSMUHCPlayer())) {
                SMUHCPlayer player = arena.getGameManager().getPlayerManager().getPlayer(getSMUHCPlayer());

                //Hide the stats bars

                if (getSMUHCPlayer().isOnline()) {

                    arena.getGameManager().getGameScoreboardManager().hideFromPlayer(getSMUHCPlayer().getBukkitPlayer());

                    makePlayerVisibleToEveryone(true);

                    //Player stats
                    getSMUHCPlayer().getBukkitPlayer().getInventory().clear();
                    getSMUHCPlayer().getBukkitPlayer().setHealth(20);
                    getSMUHCPlayer().getBukkitPlayer().setFoodLevel(20);

                }
            }
        }
    }

        /**
         * Teleports the player to the arena's waiting room
         */
        private void teleportToWaitingRoom() {
            teleport(arena.getWaitingLocation());
        }

        /**
         * Teleports the player to the arena's return point
         */
        private void teleportToReturnPoint () {
            teleport(arena.getReturnLocation());
        }

        /**
         * Teleports player to location
         *
         * @param location Teleport to location
         */
        public void teleport (Location location){
            if (getSMUHCPlayer().isOnline()) {
                getSMUHCPlayer().getBukkitPlayer().teleport(location);
            }
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

        getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
    }

}
