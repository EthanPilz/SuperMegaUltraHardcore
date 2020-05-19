package com.ethanpilz.smuhc.listener;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.exceptions.SaveToDatabaseException;
import com.ethanpilz.smuhc.exceptions.arena.ArenaDoesNotExistException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Iterator;
import java.util.Map;

public class BlockListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        if (event.getBlock().getType().equals(Material.OAK_WALL_SIGN) || event.getBlock().getType().equals(Material.OAK_SIGN)) {
            Sign s = (Sign) event.getBlock().getState();
            String[] lines = event.getLines();

            if (lines[0].equalsIgnoreCase("[SMUHC]")) {
                if (event.getPlayer().hasPermission("smuhc.admin")) {
                    if (!lines[1].isEmpty() && lines[1] != "") {
                        try {
                            event.setCancelled(true);
                            SMUHC.inputOutput.storeSign(s, SMUHC.arenaController.getArena(lines[1]));
                            SMUHC.arenaController.getArena(lines[1]).getSignManager().addJoinSign(s);
                            SMUHC.arenaController.getArena(lines[1]).getSignManager().updateJoinSigns();
                        } catch (ArenaDoesNotExistException exception) {
                            //Game does not exist
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Game " + ChatColor.AQUA + lines[1] + ChatColor.RED + "doesn't exist.");
                        } catch (SaveToDatabaseException exception) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Error while trying to save sign to database. " + ChatColor.GRAY + "" + ChatColor.UNDERLINE + "See console for full error.");
                        }
                    } else {
                        //They didn't supply the sign name
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "You need to supply the arena name on the 2nd line.");
                    }
                } else {
                    //No permissions
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "You don't have permission to add signs.");
                }
            }
        }
    }
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.OAK_WALL_SIGN) || event.getBlock().getType().equals(Material.OAK_SIGN))
        {
            Sign sign = (Sign)event.getBlock().getState();

            Iterator it = SMUHC.arenaController.getArenas().entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry entry = (Map.Entry) it.next();
                Arena arena = (Arena) entry.getValue();
                if (arena.getSignManager().isJoinSign(sign))
                {
                    if (event.getPlayer().hasPermission("FridayThe13th.Admin"))
                    {
                        arena.getSignManager().removeJoinSign(sign);
                        SMUHC.inputOutput.deleteSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld().getName());
                        event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.GREEN + "Sign removed successfully.");
                    }
                    else
                    {
                        //Don't have permission to break the sign
                        event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "You don't have permission to break SMUHC signs.");
                    }
                }
            }
        }
    }
}
