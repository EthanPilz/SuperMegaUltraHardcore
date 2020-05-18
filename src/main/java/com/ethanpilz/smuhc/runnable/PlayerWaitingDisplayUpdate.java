package com.ethanpilz.smuhc.runnable;

import com.ethanpilz.smuhc.components.arena.Arena;

public class PlayerWaitingDisplayUpdate implements Runnable {

    private Arena arena;

    public PlayerWaitingDisplayUpdate(Arena a) {
        arena = a;
    }

    @Override
    public void run() {
        if ((arena.getGameManager().isGameEmpty() || arena.getGameManager().isGameWaiting()) && arena.getGameManager().getPlayerManager().getNumberOfPlayers() > 0) {
            arena.getGameManager().getPlayerManager().updateWaitingStatsScoreboards();
        }
    }
}
