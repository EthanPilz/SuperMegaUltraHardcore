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

    public GameScoreboardManager(Arena a) {
        arena = a;
        gameScoreboard = new Sidebar(ChatColor.RED + "" + ChatColor.BOLD + "SuperMegaUltraHardcore", SMUHC.instance, 60);
        timeLeftValue = new SidebarString("");
    }

    /**
     * Updates the scoreboard values
     */
    public void updateScoreboard() {
        List<SidebarString> newList = new ArrayList<>(gameScoreboard.getEntries());
        for (SidebarString string : newList) {
            gameScoreboard.removeEntry(string);
        }

        SidebarString arenaTitle = new SidebarString(ChatColor.GOLD +  "Game " + ChatColor.AQUA + arena.getName());
        gameScoreboard.addEntry(arenaTitle);

        gameScoreboard.addEntry(new SidebarString(" "));

        //Time Left
        int rem = arena.getGameManager().getGameTimeLeft() % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        SidebarString timeLeftTitle = new SidebarString(ChatColor.GOLD + "Time Left " + ChatColor.AQUA + mn + "m " + sec + "s");
        gameScoreboard.addEntry(timeLeftTitle);

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