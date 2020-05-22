package com.ethanpilz.smuhc.components.arena;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.manager.game.GameManager;
import com.ethanpilz.smuhc.manager.arena.SignManager;
import com.ethanpilz.smuhc.utils.FileUtils;
import org.bukkit.*;

import java.io.File;
import java.util.logging.Level;

public class Arena {

    private GameManager gameManager;
    public String arenaName;
    public String worldName;
    private Location returnLocation;
    private Location waitingLocation;
    private int secondsWaitingRoom;


    //Managers
    public GameManager manager;
    public SignManager signManager;

    public Arena(String arenaName, String worldName, Location waitingLocation, Location returnLocation, int secWaitingRoom){

        //Values
        this.arenaName = arenaName;
        this.worldName = worldName;
        this.waitingLocation = waitingLocation;
        this.returnLocation = returnLocation;
        this.secondsWaitingRoom = secWaitingRoom;

        //Initialize
        signManager = new SignManager(this);
        gameManager = new GameManager(this);
    }

    /**
     * Returns the arena's name
     * @return
     */

    public String getName() {
        return arenaName;
    }

    public String getWorldName() { return worldName; }

    public World getWorld() { return Bukkit.getWorld(getWorldName()); }

    public void prepareWorld() {
        WorldCreator worldCreator = new WorldCreator("SMUHC_" + getName());
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.type(WorldType.NORMAL);
        worldCreator.createWorld();
        Bukkit.getServer().createWorld(worldCreator);
    }

    public void deleteWorldFolder() {
        try {
            // Delete the entire folder that the arena world is in.
            File worldFile = Bukkit.getServer().getWorld(getName()).getWorldFolder();
            SMUHC.log.log(Level.FINER, "deleteWorld(): worldFile: " + worldFile.getAbsolutePath());
            if (FileUtils.deleteFolder(worldFile)) {
                SMUHC.log.info("World " + getWorldName() + " was DELETED.");

            } else {
                SMUHC.log.severe("World  was NOT deleted.");
                SMUHC.log.severe("Are you sure the folder exists?");
            }

        } catch (Throwable e) {
            SMUHC.log.severe("No idea what went wrong... your best bet is to delete the files by hand.");
            SMUHC.log.severe(e.getMessage());
        }
    }

    /**
     * Returns wait location
     * @return
     */

    public Location getWaitingLocation() {
        return waitingLocation;
    }

    /**
     * Returns the return location
     * @return
     */

    public Location getReturnLocation() { return returnLocation; }

    /**
     * Returns the arena's game manager
     * @return
     */

    public GameManager getGameManager() { return gameManager; }

    /**
     * Returns the arena's sign manager
     * @return
     */

    public SignManager getSignManager() { return signManager; }

    /**
     * Returns the number of minutes in the waiting room once the minimum number of players have joined
     * @return
     */

    public int getSecondsWaitingRoom() { return secondsWaitingRoom; }

    /**
     * Updates the number of seconds in the waiting room
     * @param seconds
     */

    public void setSecondsWaitingRoom(int seconds) {
        secondsWaitingRoom = seconds;
        updateInDB();
    }

    /**
     * Updates the arena values in the database
     */

    private void updateInDB() {
        SMUHC.inputOutput.updateArena(this);
    }

    /**
     * Deletes the arena
     */
    public void delete() {
        //Cancel all game tasks
        //TODO

        //Remove from DB
        SMUHC.inputOutput.deleteArena(getName());

        //Remove signs
        getSignManager().deleteSigns();

         //getSignManager().markDeleted();
        //                                    FridayThe13th.arenaController.removeArena(arena);
        //                                    FridayThe13th.inputOutput.deleteArena(arenaName);
    }
}


