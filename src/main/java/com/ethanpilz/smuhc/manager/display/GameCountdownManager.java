package com.ethanpilz.smuhc.manager.display;

import com.ethanpilz.smuhc.components.arena.Arena;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class GameCountdownManager {
    private Arena arena;
    private BossBar gameCountdownBar;


    public GameCountdownManager (Arena arena) {
        this.arena = arena;
    }

    public void updateCountdown() {
        if (arena.getGameManager().getGameTimeLeft() == 0)
        {
            arena.getGameManager().gameTimeUp(); //game ran out of time

        } else {
            //Update the bar
            float percentage = ((float) arena.getGameManager().getGameTimeLeft()) / arena.getGameManager().getGameTimeMax();
            gameCountdownBar.setProgress(percentage);

            int rem = arena.getGameManager().getGameTimeLeft() % 3600;
            int mn = rem / 60;
            int sec = rem % 60;
            }
        }

    /**
     * Shows the time left bar for the specified player
     * @param p
     */
    public void showForPlayer(Player p) { gameCountdownBar.addPlayer(p); }

    /**
     * Removes the time left bar from the specified player
     * @param p
     */
    public void hideFromPlayer(Player p) { gameCountdownBar.removePlayer(p); }

    /**
     * Hides the time left bar from everyone
     */
    public void hideCountdownBar()
    {
        gameCountdownBar.removeAll();
    }
}
