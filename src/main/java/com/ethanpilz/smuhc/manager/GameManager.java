package com.ethanpilz.smuhc.manager;

public class GameManager {

    //Game Variables
    private int gameTimeLeftInSeconds;
    private int gameTimeMax;
    private int waitingTimeLeftInSeconds;

    //Tasks
    //second countdown (only when in waiting and in progress)
    private int gameCountdownTask = -1;
    private int waitingCountdownTask = -1;
    private int waitingPlayerUpdateTask = -1;

    public GameManager(){
    }
    private static GameManager manager;
    private GameManager getInstance(){
        if(manager == null){
            manager = new GameManager();
        }
        return manager;
    }

}
