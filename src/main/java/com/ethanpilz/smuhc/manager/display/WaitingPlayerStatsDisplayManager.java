package com.ethanpilz.smuhc.manager.display;

import com.coloredcarrot.api.sidebar.Sidebar;
import com.coloredcarrot.api.sidebar.SidebarString;
import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.SMUHCPlayer;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.components.level.SMUHCPlayerLevel;
import com.ethanpilz.smuhc.exceptions.player.PlayerNotPlayingException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WaitingPlayerStatsDisplayManager {

    private SMUHCPlayer player;
    private Sidebar statsScoreboard;

    public WaitingPlayerStatsDisplayManager(SMUHCPlayer p) {
        player = p;
        statsScoreboard = new Sidebar(ChatColor.RED + "SuperMegaUltraHardcore" , SMUHC.instance, 999);
    }

    /**
     * Updates stats scoreboard
     */
    public void updateStatsScoreboard() {
        try {

            Arena arena = SMUHC.arenaController.getPlayerArena(player);

            List<SidebarString> newList = new ArrayList<>(statsScoreboard.getEntries());
            for (SidebarString string : newList) {
                statsScoreboard.removeEntry(string);
            }

            //Game
            SidebarString arenaTitle = new SidebarString(ChatColor.GOLD + "Game " + ChatColor.AQUA + arena.getName());
            statsScoreboard.addEntry(arenaTitle);

            statsScoreboard.addEntry(new SidebarString(" "));

            SidebarString waitingPlayersTitle = new SidebarString(ChatColor.GOLD + "Waiting Players");
            statsScoreboard.addEntry(waitingPlayersTitle);

            SidebarString waitingPlayersValue = new SidebarString(arena.getGameManager().getPlayerManager().getNumberOfPlayers() + " ");
            statsScoreboard.addEntry(waitingPlayersValue);

            statsScoreboard.addEntry(new SidebarString("    "));

            SidebarString playerLevelTitle = new SidebarString(ChatColor.GOLD + "Your Level");
            statsScoreboard.addEntry(playerLevelTitle);

            SidebarString playerLevelValue = new SidebarString(player.getLevel().getLevelNumber() + "  ");
            statsScoreboard.addEntry(playerLevelValue);

            statsScoreboard.addEntry(new SidebarString("     "));

            SidebarString xpTitle = new SidebarString(ChatColor.GOLD +"Your XP");
            statsScoreboard.addEntry(xpTitle);

            SidebarString xpValue = new SidebarString(Integer.toString(player.getXP()) + "   ");
            statsScoreboard.addEntry(xpValue);

            //Display XP until next level, if there is a next level
            if (player.getLevel().isLessThan(SMUHCPlayerLevel.L20)) {

                statsScoreboard.addEntry(new SidebarString("      "));

                SidebarString xpNeededTitle = new SidebarString(ChatColor.GOLD + "XP Until Level Up");
                statsScoreboard.addEntry(xpNeededTitle);

                SidebarString xpNeededValue = new SidebarString(Integer.toString(player.getNextLevel().getMinXP() - player.getXP()) + "    ");
                statsScoreboard.addEntry(xpNeededValue);
            }

            statsScoreboard.update();
        } catch (PlayerNotPlayingException exception) {

        }

    }

    /**
     * Displays stats scoreboard
     */
    public void displayStatsScoreboard(Player player) {
        statsScoreboard.showTo(player);
    }

    /**
     * Hides stats scoreboard
     */
    public void removeStatsScoreboard(Player player) {
        statsScoreboard.hideFrom(player);
    }
}

