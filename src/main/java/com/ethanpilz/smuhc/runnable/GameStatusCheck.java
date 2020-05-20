package com.ethanpilz.smuhc.runnable;

import com.ethanpilz.smuhc.components.arena.Arena;

public class GameStatusCheck implements Runnable
{
    private Arena arena;

    public GameStatusCheck(Arena arena)
    {
        this.arena = arena;
    }

    @Override
    public void run()
    {
        arena.getGameManager().checkGameStatus();
        arena.getSignManager().updateJoinSigns(); //Update signs
    }

}