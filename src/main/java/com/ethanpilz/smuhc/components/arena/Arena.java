package com.ethanpilz.smuhc.components.arena;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.manager.game.GameManager;
import com.ethanpilz.smuhc.manager.arena.SignManager;
import org.bukkit.Location;

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

        // arena.getSignManager().markDeleted();
        //                                    FridayThe13th.arenaController.removeArena(arena);
        //                                    FridayThe13th.inputOutput.deleteArena(arenaName);
    }
}


