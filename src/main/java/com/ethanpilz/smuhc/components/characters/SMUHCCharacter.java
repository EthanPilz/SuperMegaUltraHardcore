package com.ethanpilz.smuhc.components.characters;

import com.ethanpilz.smuhc.components.SMUHCPlayer;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.experience.XPManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SMUHCCharacter {

    private SMUHCPlayer smuhcPlayer;
    protected Arena arena;
    private XPManager xpManager;

    //Restore values
    private float originalWalkSpeed;
    private float originalFlySpeed;
    private boolean originalAllowFly;

    SMUHCCharacter(SMUHCPlayer player, Arena arena) {

        this.smuhcPlayer = player;
        this.arena = arena;
        this.xpManager = new XPManager(smuhcPlayer, arena);

        //Restore Values
        originalWalkSpeed = getSMUHCPlayer().getBukkitPlayer().getWalkSpeed();
        originalFlySpeed = getSMUHCPlayer().getBukkitPlayer().getFlySpeed();
        originalAllowFly = getSMUHCPlayer().getBukkitPlayer().getAllowFlight();
    }

    /**
     * @return F13Player object
     */
    public SMUHCPlayer getSMUHCPlayer() {
        return smuhcPlayer;
    }

    /**
     * @return Bukkit player
     */
    public Player getPlayer() {
        return getSMUHCPlayer().getBukkitPlayer();
    }

    /**
     * @return The character's arena
     */
    public Arena getArena() {
        return arena;
    }

    /**
     * @return The character's XP manager
     */
    public XPManager getXpManager() {
        return xpManager;
    }

    /**
     * @return The player's original walking speed
     */
    private float getOriginalWalkSpeed() {
        return originalWalkSpeed;
    }

    /**
     * @return The player's original fly speed
     */
    private float getOriginalFlySpeed() {
        return originalFlySpeed;
    }

    /**
     * @return The player's original permission to fly
     */
    private boolean isOriginallyAllowedFly() {
        return originalAllowFly;
    }

    /**
     * Restores the players original speed values
     */
    protected void restoreOriginalSpeeds() {
        if (getSMUHCPlayer().isOnline()) {
            getSMUHCPlayer().getBukkitPlayer().setFlySpeed(getOriginalFlySpeed());
            getSMUHCPlayer().getBukkitPlayer().setWalkSpeed(getOriginalWalkSpeed());
            getSMUHCPlayer().getBukkitPlayer().setAllowFlight(isOriginallyAllowedFly());
        }
    }

    /**
     * Prepares the player for the waiting room and teleports them in
     */
    public void enterWaitingRoom() {
        teleportToWaitingRoom();
        setDefaultSurvivalTraits();
        //giveWaitingRoomItems();
        getSMUHCPlayer().getWaitingPlayerStatsDisplayManager().displayStatsScoreboard();
        //makePlayerVisibleToEveryone(true); //In case of invisibility bug

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

                //Clear the action bar and hide spectator displays
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

                Fighter fighter = arena.getGameManager().getPlayerManager().getFighter(getSMUHCPlayer());

                //Hide the stats bars
                //smuhcPlayer.getCounselorStatsDisplayManager().hideStats();

                if (getSMUHCPlayer().isOnline()) {
                    //arena.getGameManager().getPlayerManager().getFighter(fighter).removeAllPotionEffects();
                    arena.getGameManager().getGameScoreboardManager().hideFromPlayer(getSMUHCPlayer().getBukkitPlayer());

                    makePlayerVisibleToEveryone(true);

                    //Player stats
                    getSMUHCPlayer().getBukkitPlayer().getInventory().clear();
                    getSMUHCPlayer().getBukkitPlayer().setHealth(20);
                    getSMUHCPlayer().getBukkitPlayer().setFoodLevel(20);
                    restoreOriginalSpeeds();

                }
            }
        }
    }

    /**
     * Performs all necessary tasks when the game begins
     */
    public void prepareForGameplay() {
        //Clear their inventory of any waiting room goodies
        getSMUHCPlayer().getBukkitPlayer().getInventory().clear();

        //Remove flying
        getSMUHCPlayer().getBukkitPlayer().setFlying(false);
        getSMUHCPlayer().getBukkitPlayer().setAllowFlight(false);

        //Display Status
       // getCounselorStatsDisplayManager().displayStats();

        //Display game-wide scoreboard
        getSMUHCPlayer().getWaitingPlayerStatsDisplayManager().removeStatsScoreboard();
        arena.getGameManager().getGameCountdownManager().hideFromPlayer(getSMUHCPlayer().getBukkitPlayer()); //In case they're coming from spectators
        arena.getGameManager().getGameScoreboardManager().displayForPlayer(getSMUHCPlayer().getBukkitPlayer());

        //Start All Counselor Tasks
        //scheduleTasks();

        //Perks
        //addGameStartPerks();

        //Make them visible, in case that invisibility bug is hitting usfile

        makePlayerVisibleToEveryone(true);
    }


    /**
         * Puts the player into classic survival mode for the game
         */
        private void setDefaultSurvivalTraits () {
            //Change game mode & clear inventory
            getSMUHCPlayer().getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
            getSMUHCPlayer().getBukkitPlayer().setHealth(20);
            getSMUHCPlayer().getBukkitPlayer().setFoodLevel(10);
            getSMUHCPlayer().getBukkitPlayer().getInventory().clear();
        }

        /**
         * Gives the player the waiting room usable items
         */
        private void giveWaitingRoomItems () {
            //Give them waiting room items
            //SpawnPreferenceMenu.addMenuOpenItem(getSMUHCPlayer().getBukkitPlayer());
            //Profiles_MainMenu.addMenuOpenItem(getSMUHCPlayer().getBukkitPlayer());
            //Shop_MainMenu.addMenuOpenItem(getSMUHCPlayer().getBukkitPlayer());
            //Shop_PurchasedPerksMenu.addMenuOpenItem(getSMUHCPlayer().getBukkitPlayer());
        }

        /**
         * Teleports the player to the arena's waiting room
         */
        private void teleportToWaitingRoom () {
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
        private void removeAllPotionEffects () {
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
        public void makePlayerVisibleToEveryone ( boolean show){
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