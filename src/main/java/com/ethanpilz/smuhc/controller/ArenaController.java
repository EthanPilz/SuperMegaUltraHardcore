package com.ethanpilz.smuhc.controller;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.SMUHCPlayer;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.exceptions.arena.ArenaAlreadyExistsException;
import com.ethanpilz.smuhc.exceptions.arena.ArenaDoesNotExistException;
import com.ethanpilz.smuhc.exceptions.player.PlayerAlreadyPlayingException;
import com.ethanpilz.smuhc.exceptions.player.PlayerNotPlayingException;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ArenaController {

    private HashMap<String, Arena> arenas;
    private HashMap<SMUHCPlayer, Arena> players;

    public ArenaController(){
        arenas = new HashMap<>();
        players = new HashMap<>();
    }

    /**
     * Adds arena into the controller memory
     * @param arena Arena object
     * @throws ArenaAlreadyExistsException The arena already exists in memory
     */
    public void addArena(Arena arena) throws ArenaAlreadyExistsException {
        if (!arenas.containsValue(arena)) {
            arenas.put(arena.getName(), arena);
        } else {
            throw new ArenaAlreadyExistsException();
        }
    }

    /**
     * Returns the arena supplied by name
     * @param name Name of desired arena
     * @return Arena object
     */

    public Arena getArena(String name) throws ArenaDoesNotExistException {
        if(doesArenaExist(name)){
            return arenas.get(name);
        } else {
            throw new ArenaDoesNotExistException("Game " + name + " does not exist in the controller memory");
        }
    }

    /**
     * Returns if the supplied arena by name exists in the controller memory
     * @param name Name of the arena
     * @return If the arena exists
     */

    public boolean doesArenaExist(String name){
        return arenas.containsKey(name);
    }

    /**
     * Returns the number of arenas loaded in the controller memory
     * @return The number of arenas
     */

    public int getNumberOfArenas() {
        return arenas.size();
    }

    /**
     * Returns all arenas
     * @return
     */

    public HashMap<String, Arena> getArenas() {
        return arenas;
    }

    /**
     * Adds player to global players list
     * @param player SMUHCPlayer
     * @param arena Arena
     * @throws PlayerAlreadyPlayingException
     */

    public void addPlayer(SMUHCPlayer player, Arena arena) throws PlayerAlreadyPlayingException {
        if (!isPlaying(player)) {
            players.put(player, arena);
        } else {
            throw new PlayerAlreadyPlayingException();
        }
    }

    /**
     * Removes player from player hash map
     * @param player SMUHCPlayer
     */

    public void removePlayer(SMUHCPlayer player) {
        players.remove(player);
    }

    /**
     * Returns if the player is actively playing within an arena
     * @param player SMUHCPlayer
     * @return If the player is actively playing within an arena
     */

    public boolean isPlaying(SMUHCPlayer player){
        return players.containsKey(player);
    }

    /**
     * Returns if the player is actively playing within an arena
     * @param player Bukkit player
     * @return If the player is actively playing within an arena
     */

    public boolean isPlaying(Player player, Arena arena){
        return isPlaying(SMUHC.playerController.getPlayer(player));
    }

    /**
     * Returns the arena which the player is in
     * @param player SMUHCPlayer
     * @return Arena that the player is playing within
     * @throws PlayerNotPlayingException
     */

    public Arena getPlayerArena(SMUHCPlayer player) throws PlayerNotPlayingException {
        if (isPlaying(player)){
            return players.get(player);
        } else {
            throw new PlayerNotPlayingException();
        }

    }

    /**
     * Returns the arena which the player is in
     *
     * @param player Bukkit player
     * @return Arena That the player is playing within
     * @throws PlayerNotPlayingException
     */

    public Arena getPlayerArena(Player player) throws PlayerNotPlayingException {
        return getPlayerArena(SMUHC.playerController.getPlayer(player));
    }

    /**
     * Returns hashmap of player UUID string and Game objects
     * @return
     */
    public HashMap<SMUHCPlayer, Arena> getPlayers() {
        return players;
    }
}
