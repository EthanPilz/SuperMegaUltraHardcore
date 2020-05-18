package com.ethanpilz.smuhc.controller;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.SMUHCPlayer;
import com.ethanpilz.smuhc.components.Spectator;
import com.ethanpilz.smuhc.SMUHC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PlayerController {

    private HashMap<String, SMUHCPlayer> players;

    public PlayerController() {
        players = new HashMap<>();
    }

    /**
     * Gets SMUHC player object of supplied Bukkit player
     *
     * @param player Player
     * @return SMUCHPlayer object of player
     */
    public SMUHCPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId().toString());
    }

    /**
     * Gets F13 player object of supplied player UUID
     *
     * @param uuid UUID of the player
     * @return SMUCHPlayer object
     */

    public SMUHCPlayer getPlayer(String uuid){
        SMUHCPlayer player = new SMUHCPlayer(uuid, 0); //need to change 0 to getXP method
        return player;
    }

    public void addPlayer(SMUHCPlayer player){
        players.put(player.getPlayerUUID(), player);
    }

    public int getNumPlayers(){
        return players.size();
    }

    /**
     * Returns if the player has ever played Friday the 13th before
     * @param uuid UUID of player
     * @return If the player has played SMUHC before
     */
    public boolean hasPlayerPlayed(String uuid) {
        if (doesPlayerExist(uuid)) {
            return true;
        } else {
            SMUHC.inputOutput.loadPlayer(uuid);
            return doesPlayerExist(uuid);
        }
    }

    /**
     * Returns if the F13 is in memory
     *
     * @param uuid UUID of the player
     * @return If the player has played SMUHC before
     */

    private boolean doesPlayerExist(String uuid) { return  players.containsKey(uuid); }

    /**
     * Removes
     */
    public void cleanupMemory() {
        Iterator it = players.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String playerUUID = (String) entry.getKey();
            SMUHCPlayer player = (SMUHCPlayer) entry.getValue();

            if (!Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).isOnline()) {
                player.updateDB();
                it.remove();
            }
        }
    }

}
