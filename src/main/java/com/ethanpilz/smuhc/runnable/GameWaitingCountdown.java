package com.ethanpilz.smuhc.runnable;

import com.ethanpilz.smuhc.components.arena.Arena;

public class GameWaitingCountdown implements Runnable {

    private Arena arena;

    public GameWaitingCountdown(Arena arena) {
            this.arena = arena;
        }

        @Override
        public void run() {
            if (arena.getGameManager().isGameWaiting() && arena.getGameManager().getWaitingTimeLeft() > 0) {
                arena.getGameManager().setWaitingTimeLeft(arena.getGameManager().getWaitingTimeLeft() - 1);
                //arena.getGameManager().getWaitingCountdownDisplayManager().updateCountdownValue();

            }
        }
    }

