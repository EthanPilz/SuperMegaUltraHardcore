package com.ethanpilz.smuhc.components.characters;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.SMUHCPlayer;
import com.ethanpilz.smuhc.components.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;


public class Fighter extends SMUHCCharacter {

    //Statistics
    private boolean moving = false;

    //Managers
    //private CounselorStatsDisplayManager counselorStatsDisplayManager;

    //Tasks
    int statsUpdateTask = -1;


    /**
     * Creates new counselor object
     * @param player Minecraft player object
     */
    public Fighter(SMUHCPlayer player, Arena arena)
    {
        super(player, arena);

        //Initialize Manager
        //counselorStatsDisplayManager = new CounselorStatsDisplayManager(this);

    }

    /**
     * Returns counselor's stats display manager
     * @return
     */
    /*public CounselorStatsDisplayManager getCounselorStatsDisplayManager() {
        return counselorStatsDisplayManager;
    }*/


    /**
     * Performs all necessary tasks when the game begins
     */
    public void prepareForGameplay()
    {
        //Clear their inventory of any waiting room goodies
        getSMUHCPlayer().getBukkitPlayer().getInventory().clear();

        //Remove flying
        getSMUHCPlayer().getBukkitPlayer().setFlying(false);
        getSMUHCPlayer().getBukkitPlayer().setAllowFlight(false);

        //Display Status
        //getCounselorStatsDisplayManager().displayStats();

        //Display game-wide scoreboard
        //getSMUHCPlayer().getWaitingPlayerStatsDisplayManager().removeStatsScoreboard();
        arena.getGameManager().getGameCountdownManager().hideFromPlayer(getSMUHCPlayer().getBukkitPlayer()); //In case they're coming from spectators
        arena.getGameManager().getGameScoreboardManager().displayForPlayer(getSMUHCPlayer().getBukkitPlayer());

        //Start All Counselor Tasks
        scheduleTasks();

        //Make them visible, in case that invisibility bug is hitting usfile
        makePlayerVisibleToEveryone(true);
    }

    /**
     * Schedules all counselor specific tasks
     */
    public void scheduleTasks()
    {
        //Start the stats updater
        //getCounselorStatsDisplayManager().startUpdaterTask();

    }

    /**
     * Cancels all counselor specific tasks
     */
    public void cancelTasks()
    {
        //End the stats updater
        //getCounselorStatsDisplayManager().endUpdaterTask();

        //Cancel fearLevel check task
        Bukkit.getScheduler().cancelTask(statsUpdateTask);
    }

    public void transitionToSpectatingMode() {
        //Remove any potions from in game
        removePotionEffects();

        //Stop stats since they're dead
        //etCounselorStatsDisplayManager().hideStats();
        cancelTasks();
    }

    /**
     * Removes potion effects from counselor
     */
    public void removePotionEffects() {
        getSMUHCPlayer().getBukkitPlayer().removePotionEffect(PotionEffectType.BLINDNESS); //Scared
        getSMUHCPlayer().getBukkitPlayer().removePotionEffect(PotionEffectType.CONFUSION); //Out of breath
        getSMUHCPlayer().getBukkitPlayer().removePotionEffect(PotionEffectType.GLOWING);
        getSMUHCPlayer().getBukkitPlayer().closeInventory(); //Close the spectate inventory if they happen to have it open
    }

}
