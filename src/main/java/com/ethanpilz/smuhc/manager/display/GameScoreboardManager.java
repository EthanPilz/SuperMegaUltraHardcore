package com.ethanpilz.smuhc.manager.display;

import com.coloredcarrot.api.sidebar.Sidebar;
import com.coloredcarrot.api.sidebar.SidebarString;
import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameScoreboardManager {
    private Arena arena;
    private Sidebar gameScoreboard;

    private SidebarString timeLeftValue;
    private SidebarString jasonValue;

    public GameScoreboardManager(Arena a) {
        arena = a;
        gameScoreboard = new Sidebar(ChatColor.RED + "" + ChatColor.BOLD + "SuperMegaUltraHardcore", SMUHC.instance, 60);
        timeLeftValue = new SidebarString("");
        jasonValue = new SidebarString("");
    }

    /**
     * Updates the scoreboard values
     */
    public void updateScoreboard() {
        List<SidebarString> newList = new ArrayList<>(gameScoreboard.getEntries());
        for (SidebarString string : newList) {
            gameScoreboard.removeEntry(string);
        }

        SidebarString arenaTitle = new SidebarString(ChatColor.GOLD +  "Game");
        gameScoreboard.addEntry(arenaTitle);

        SidebarString arenaName = new SidebarString(arena.getName());
        gameScoreboard.addEntry(arenaName);

        gameScoreboard.addEntry(new SidebarString(" "));

        //Time Left
        SidebarString timeLeftTitle = new SidebarString(ChatColor.GOLD + "Time Left");
        gameScoreboard.addEntry(timeLeftTitle);

        int rem = arena.getGameManager().getGameTimeLeft() % 3600;
        int mn = rem / 60;
        int sec = rem % 60;

        timeLeftValue = new SidebarString(mn + "m " + sec + "s");
        gameScoreboard.addEntry(timeLeftValue);

        //Space
        gameScoreboard.addEntry(new SidebarString("   "));

        //ALIVE
        gameScoreboard.addEntry(new SidebarString(ChatColor.GOLD + "Alive / Dead"));
        gameScoreboard.addEntry(new SidebarString(ChatColor.GREEN + "" + arena.getGameManager().getPlayerManager().getNumberOfPlayersAlive() + ChatColor.WHITE + " / " + ChatColor.RED + "" + arena.getGameManager().getPlayerManager().getNumberOfPlayersDead()));

        gameScoreboard.update();
    }

    public void displayForPlayer(Player p)
    {
        gameScoreboard.showTo(p);
    }

    public void hideFromPlayer(Player p)
    {
        gameScoreboard.hideFrom(p);
    }


}