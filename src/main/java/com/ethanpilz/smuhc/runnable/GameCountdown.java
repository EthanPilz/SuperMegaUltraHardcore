package com.ethanpilz.smuhc.runnable;

import com.ethanpilz.smuhc.components.arena.Arena;

public class GameCountdown implements Runnable {
    private Arena arena;

    public GameCountdown(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        if (arena.getGameManager().isGameInProgress()) {
            arena.getGameManager().setGameTimeLeft(arena.getGameManager().getGameTimeLeft() - 1); //Decrement a second
            arena.getGameManager().getGameCountdownManager().updateCountdown(); //Update the countdown display & in-game messages
            arena.getGameManager().getGameScoreboardManager().updateScoreboard(); //Update the sidebar scoreboard with stats
        }
    }
}