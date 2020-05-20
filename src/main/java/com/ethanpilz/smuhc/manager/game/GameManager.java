package com.ethanpilz.smuhc.manager.game;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.SMUHCPlayer;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.components.arena.GameStatus;
import com.ethanpilz.smuhc.manager.display.GameCountdownManager;
import com.ethanpilz.smuhc.manager.display.GameScoreboardManager;
import com.ethanpilz.smuhc.manager.display.WaitingCountdownDisplayManager;
import com.ethanpilz.smuhc.runnable.GameCountdown;
import com.ethanpilz.smuhc.runnable.GameStatusCheck;
import com.ethanpilz.smuhc.runnable.GameWaitingCountdown;
import com.ethanpilz.smuhc.runnable.PlayerWaitingDisplayUpdate;
import org.bukkit.*;

import java.util.logging.Level;

public class GameManager {

    private Arena arena;

    //Game Variables
    private int gameTimeLeftInSeconds;
    private int gameTimeMax;
    private int waitingTimeLeftInSeconds;
    private GameStatus gameStatus;

    //Tasks
    //second countdown (only when in waiting and in progress)
    private int gameStatusCheckTask = -1;
    private int gameCountdownTask = -1;
    private int waitingCountdownTask = -1;
    private int waitingPlayerUpdateTask = -1;

    //Managers
    private PlayerManager playerManager;
    private GameCountdownManager gameCountdownManager;
    private WaitingCountdownDisplayManager waitingCountdownDisplayManager; //Game-wide waiting room countdown
    private GameScoreboardManager gameScoreboardManager;

    /**
     * @param arena Game object
     */
    public GameManager(Arena arena) {
        this.arena = arena;
        resetGameStatistics();

        //Get max times
        gameTimeMax = 3600;

        //Managers
        playerManager = new PlayerManager(arena);
        gameCountdownManager = new GameCountdownManager(arena);
        waitingCountdownDisplayManager = new WaitingCountdownDisplayManager(arena);
        gameScoreboardManager = new GameScoreboardManager(arena);

        //Change game status to empty
        gameStatus = GameStatus.Empty; //to avoid null pointer
        changeGameStatus(GameStatus.Empty);

        //Start Tasks
        gameStatusCheckTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(SMUHC.instance, new GameStatusCheck(arena), 60, 20);
    }

    /**
     * Returns the arena's player manager
     *
     * @return
     */
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * Returns the game countdown display manager
     *
     * @return
     */
    public GameCountdownManager getGameCountdownManager() {
        return gameCountdownManager;
    }

    /**
     * Returns the waiting countdown display manager
     */
    public WaitingCountdownDisplayManager getWaitingCountdownDisplayManager() {
        return waitingCountdownDisplayManager;
    }

    /**
     * Returns the game scoreboard display manager
     *
     * @return
     */
    public GameScoreboardManager getGameScoreboardManager() {
        return gameScoreboardManager;
    }

    /**
     * Returns the seconds left in the waiting countdown
     *
     * @return
     */
    public int getWaitingTimeLeft() {
        return waitingTimeLeftInSeconds;
    }

    /**
     * Sets the seconds left in the waiting countdown
     *
     * @param value
     */
    public void setWaitingTimeLeft(int value) {
        waitingTimeLeftInSeconds = value;
    }


    /**
     * Returns the number of seconds left in the game
     *
     * @return
     */
    public int getGameTimeLeft() {
        return gameTimeLeftInSeconds;
    }

    /**
     * Returns the maximum number of seconds per game
     *
     * @return
     */
    public int getGameTimeMax() {
        return gameTimeMax;
    }

    /**
     * Sets the time left in the game in seconds
     *
     * @param value
     */
    public void setGameTimeLeft(int value) {
        gameTimeLeftInSeconds = Math.max(0, value); //make sure it doesn't go below 0
    }


    /**
     * Resets the games internal statistics
     */
    private void resetGameStatistics() {
        setGameTimeLeft(getGameTimeMax());
        waitingTimeLeftInSeconds = arena.getSecondsWaitingRoom();
    }

    /**
     * Performs automated checks on the game to ensure status is always accurate
     */
    public void checkGameStatus() {
        if (isGameEmpty()) {
            if (getPlayerManager().getNumberOfPlayers() >= 2) {
                //There are people waiting and we've reached the min, change to waiting
                changeGameStatus(GameStatus.Waiting);
            } else {
                //Need more players before waiting countdown will begin
            }
        } else if (isGameWaiting()) {
            if (getPlayerManager().getNumberOfPlayers() >= 1) {
                if (waitingTimeLeftInSeconds <= 0) {
                    //BEGIN THE GAME
                    changeGameStatus(GameStatus.InProgress);

                } else {
                    getPlayerManager().displayWaitingCountdown();
                }
            } else {
                //Minimum player requirement no longer met - Cancel waiting countdown task and go back to empty status
                changeGameStatus(GameStatus.Empty);
            }
        } else if (isGameInProgress()) {

            if (getPlayerManager().getNumberOfPlayers() < 2) {
                endGame(); //End the game since there aren't enough players
            }
        }
    }

    /**
     * Returns if the game is empty
     *
     * @return
     */
    public boolean isGameEmpty() {
        return gameStatus.equals(GameStatus.Empty);
    }

    /**
     * Returns if the game is waiting
     *
     * @return
     */
    public boolean isGameWaiting() {
        return gameStatus.equals(GameStatus.Waiting);
    }

    /**
     * Returns if the game is in progress
     *
     * @return
     */
    public boolean isGameInProgress() {
        return gameStatus.equals(GameStatus.InProgress);
    }

    /**
     * Changes the game status
     *
     * @param status
     */
    public void changeGameStatus(GameStatus status) {
        //Changing to empty
        if (status.equals(GameStatus.Empty)) {
            //Tasks
            Bukkit.getScheduler().cancelTask(waitingCountdownTask);
            Bukkit.getScheduler().cancelTask(gameCountdownTask);
            waitingPlayerUpdateTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(SMUHC.instance, new PlayerWaitingDisplayUpdate(arena), 60, 60);

            if (isGameWaiting() && getPlayerManager().getNumberOfPlayers() == 0) {
                getPlayerManager().hideWaitingCountdown(); //Hide countdown from players
                //Resets all data structures with players since there are none left
            }

            if (isGameInProgress()) {
                getPlayerManager().resetPlayerStorage();
            }

            gameStatus = GameStatus.Empty; //Change mode
            resetGameStatistics();
        } else if (status.equals(GameStatus.Waiting)) //Changing to waiting (can only go from empty -> in waiting)
        {
            gameStatus = GameStatus.Waiting; //Change mode
            resetGameStatistics();

            //Start the tasks
            waitingCountdownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(SMUHC.instance, new GameWaitingCountdown(arena), 20, 20);

            //Reset players visuals (remove action bars)
            //getPlayerManager().resetPlayerActionBars();

            //Display waiting countdown
            getWaitingCountdownDisplayManager().updateCountdownValue();
            getPlayerManager().displayWaitingCountdown();

        } else if (status.equals(GameStatus.InProgress)) {//Changing to in progress (can only go from waiting -> in progress)
            if (isGameWaiting()) {
                Bukkit.getScheduler().cancelTask(waitingCountdownTask); //Cancel waiting countdown
                Bukkit.getScheduler().cancelTask(waitingPlayerUpdateTask); //Cancel waiting sidebar display for players
                getPlayerManager().hideWaitingCountdown(); //Hide countdown from players
            }

            gameStatus = GameStatus.InProgress; //Change mode

            //Start the game
            beginGame();

            //Schedule game countdown
            gameCountdownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(SMUHC.instance, new GameCountdown(arena), 0, 20);
        }

        arena.getSignManager().updateJoinSigns(); //update the join signs
    }

    /**
     * Performs actions to begin the game
     */
    private void beginGame() {

        //Assign all players roles
        getPlayerManager().beginGame();

        SMUHC.log.log(Level.INFO, SMUHC.consolePrefix + "Game in " + arena.getName() + " beginning...");
    }

    /**
     * Ends the game
     */
    protected void endGame() {
        if (isGameInProgress()) {

            //Remove all players
            getPlayerManager().endGame();

            //Make countdown bar for any counselors disappear
            getGameCountdownManager().hideCountdownBar();

            //Don't need to worry about tasks and timers here, handled automatically
            changeGameStatus(GameStatus.Empty);

            //Unload the current arena's world, boolean false is saying do not save it.
            Bukkit.unloadWorld(arena.getWorldName(), false);
            arena.deleteWorldFolder();

            //Now, it's time to regenerate the world, 30 seconds after the game ended and the world was deleted.
            Bukkit.getScheduler().scheduleSyncDelayedTask(SMUHC.instance, new Runnable() {
                @Override
                public void run() {
                   arena.prepareWorld();
                }
            }, 600L); //20 Tick (1 Second) delay before run() is called
        }
    }

    /**
     * Ends the game when the time expires
     */
    public void gameTimeUp() {
        //need to pass that the fighter who is alive won.
        endGame();
    }

    /**
     * Clears the arena of all players - does not award XP
     */
    public void clearArena() {
        if (isGameEmpty() || isGameWaiting()) {
            for (SMUHCPlayer player : getPlayerManager().getPlayers()) {
                getPlayerManager().onPlayerQuit(player);
            }
        } else if (isGameInProgress()) {
            endGame();
        }
    }
}