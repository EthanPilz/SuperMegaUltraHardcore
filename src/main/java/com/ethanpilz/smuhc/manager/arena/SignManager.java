package com.ethanpilz.smuhc.manager.arena;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

import java.util.HashSet;

public class SignManager {

    private Arena arena;
    private HashSet<org.bukkit.block.Sign> joinSigns;

    public SignManager(Arena arena) {

        this.arena = arena;
        this.joinSigns = new HashSet<>();
    }

    /**
     * Returns if the supplied sign is a join sign
     * @param sign
     * @return
     */
    public boolean isJoinSign(org.bukkit.block.Sign sign)
    {
        return joinSigns.contains(sign);
    }

    /**
     * Adds join sign
     * @param sign
     */
    public void addJoinSign(org.bukkit.block.Sign sign)
    {
        joinSigns.add(sign);
    }

    /**
     * Removes join sign
     * @param sign
     */
    public void removeJoinSign(org.bukkit.block.Sign sign) { joinSigns.remove(sign); }

    /**
     * Updates all arena join signs
     */
    public void updateJoinSigns()
    {
        for (org.bukkit.block.Sign sign : joinSigns)
        {
            updateSign(sign);
        }
    }

    /**
     * Updates the sign's display
     *
     * @param sign
     */
    private void updateSign(org.bukkit.block.Sign sign) {
        sign.setLine(0, SMUHC.signPrefix);
        sign.setLine(1, arena.getName());

        if (arena.getGameManager().isGameEmpty()) {
            if (arena.getGameManager().getPlayerManager().getNumberOfPlayers() == 0) {
                //Display empty
                sign.setLine(2, ChatColor.GREEN + "Empty");
            } else {
                //Display counter
                if (arena.getGameManager().getPlayerManager().getPlayers().size() >= 1) {
                    sign.setLine(2, arena.getGameManager().getPlayerManager().getNumberOfPlayers() + " / 100");
                } else {
                    sign.setLine(2, arena.getGameManager().getPlayerManager().getNumberOfPlayers() + " / " + "100");
                }
            }

            sign.setLine(3, ChatColor.WHITE + "Click To Join!");
        } else if (arena.getGameManager().isGameWaiting()) {
            sign.setLine(2, ChatColor.AQUA + "Waiting" + ChatColor.BLACK + " - " + arena.getGameManager().getWaitingTimeLeft() + "s");
            sign.setLine(3, ChatColor.WHITE + "Click To Join!");
        } else if (arena.getGameManager().isGameInProgress()) {
            int rem = arena.getGameManager().getGameTimeLeft() % 3600;
            int mn = rem / 60;
            int sec = rem % 60;

            sign.setLine(2, ChatColor.DARK_RED + "" + mn + " m " + sec + " sec");
            sign.setLine(3, ChatColor.WHITE + "Click To Spectate!");
        }

        sign.update();
    }

    /**
     * Removes all signs from memory and database
     */
    public void deleteSigns() {
        for (org.bukkit.block.Sign sign : joinSigns) {
            markDeleted(sign);
            SMUHC.inputOutput.deleteSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld().getName());
            removeJoinSign(sign);
        }
    }

    /**
     * Marks all signs as deleted
     */
    private void markDeleted(Sign sign) {
        sign.setLine(0, ChatColor.RED + SMUHC.signPrefix);
        sign.setLine(1, "[" +  "Deleted" + "]");
        sign.setLine(2, "");
        sign.setLine(3, "");
        sign.update();
    }
}
