package com.ethanpilz.smuhc.manager.game;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.arena.GameStatus;
import com.ethanpilz.smuhc.components.characters.Fighter;
import com.ethanpilz.smuhc.components.characters.SMUHCCharacter;
import com.ethanpilz.smuhc.components.SMUHCPlayer;
import com.ethanpilz.smuhc.components.characters.Spectator;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.exceptions.game.GameFullException;
import com.ethanpilz.smuhc.exceptions.game.GameInProgressException;
import com.ethanpilz.smuhc.exceptions.player.PlayerAlreadyPlayingException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class PlayerManager {

    private Arena arena;

    //Game Players
    private HashSet<SMUHCPlayer> players;
    private HashMap<SMUHCPlayer, Fighter> fighters;

    //Game Stat
    private HashSet<SMUHCPlayer> alivePlayers;
    private HashSet<SMUHCPlayer> deadPlayers;
    private HashMap<SMUHCPlayer, Spectator> spectators;


    /**
     * @param arena Game
     */
    PlayerManager(Arena arena) {
        this.arena = arena;
        this.players = new HashSet<>();
        this.fighters = new HashMap<>();
        this.alivePlayers = new HashSet<>();
        this.deadPlayers = new HashSet<>();
        this.spectators = new HashMap<>();
    }

    /**
     * Resets lists of alive and dead players
     */
    public void resetPlayerStorage() {
        players.clear();
        fighters.clear();
        alivePlayers.clear();
        deadPlayers.clear();
        spectators.clear();
    }

    /**
     * @return Current players
     */
    public HashSet<SMUHCPlayer> getPlayers() {
        return players;
    }

    /**
     * Adds player to the current game's player list
     *
     * @param player SMUHCPlayer
     */
    private void addPlayer(SMUHCPlayer player) {
        fighters.put(player, getFighter(player));
        players.add(player);

    }

    /**
     * Removes player from current game's player list
     *
     * @param player SMUHCPlayer
     */
    private void removePlayer(SMUHCPlayer player) {
        players.remove(player);
        SMUHC.arenaController.removePlayer(player);
    }

    public boolean isPlaying(SMUHCPlayer player) {
        return players.contains(player);
    }

    public Fighter getFighter(SMUHCPlayer player) {
        return fighters.get(player);
    }

    public HashMap<SMUHCPlayer, Fighter> getFighters() {
        return fighters;
    }
    /**
     * @param player SMUHCPlayer
     * @return If the player is a fighter
     */
    public boolean isFighter(SMUHCPlayer player) {
        return fighters.containsKey(player);
    }

    /**
     * Assigns the provided player as a fighter
     *
     * @param player SMUHCPlayer
     */
    private void assignFighter(SMUHCPlayer player) {
        addFighter(new Fighter(player, arena));
    }


    /**
     * Adds new Fighter
     *
     * @param fighter Fighter object
     */
    private void addFighter(Fighter fighter) {
        fighters.put(fighter.getSMUHCPlayer(), fighter);
    }


    /**
     * Sends in game message to all players
     *
     * @param message Message to be sent in chat
     */
    public void sendMessageToAllPlayers(String message) {
        for (SMUHCPlayer player : players) {
            if (player.isOnline()) {
                player.getBukkitPlayer().sendMessage(SMUHC.smuhcPrefix + message);
            }
        }
    }

    /**
     * @return Number of players in the current game
     */
    public int getNumberOfPlayers() {
        return players.size();
    }

    /**
     * @return Alive players
     */
    public HashSet<SMUHCPlayer> getAlivePlayers() {
        return alivePlayers;
    }

    /**
     * Adds player to the alive player list
     *
     * @param player SMUHCPlayer
     */
    private void addAlivePlayer(SMUHCPlayer player) {
        alivePlayers.add(player);
    }


    /**
     * Performs actions for players when the game begins
     */
    protected void beginGame() {

        for (Fighter player : fighters.values()) {
            player.prepareForGameplay();
            addAlivePlayer(player.getSMUHCPlayer());
        }

    }

    /**
     * Performs actions for players when the game ends
     */
    protected void endGame() {

        //Counselors - Award XP & Leave Game
        for (SMUHCCharacter player : fighters.values()) {
            player.getXpManager().awardXPToPlayer();
            player.leaveGame();
            removePlayer(player.getSMUHCPlayer());
        }

        for (Spectator spectator : spectators.values()) {
            spectator.leaveGame();
            removePlayer(spectator.getSMUHCPlayer());
        }

        resetPlayerStorage();
    }


    /**
     * Removes player from the alive player list
     *
     * @param player SMUHCPlayer
     */
    private void removeAlivePlayer(SMUHCPlayer player) {
        alivePlayers.remove(player);
    }

    /**
     * @return Number of players alive
     */
    public int getNumberOfPlayersAlive() {
        return alivePlayers.size();
    }

    /**
     * @param player SMUHCPlayer
     * @return If the player is alive
     */
    public boolean isAlive(SMUHCPlayer player) {
        return alivePlayers.contains(player);
    }

    /**
     * @return Dead players
     */
    private HashSet<SMUHCPlayer> getDeadPlayers() {
        return deadPlayers;
    }

    /**
     * Adds player to the dead player list
     *
     * @param player SMUHCPlayer
     */
    public void addDeadPlayer(SMUHCPlayer player) {
        deadPlayers.add(player);
    }

    /**
     * Removes player from the dead player list
     *
     * @param player SMUHCPlayer
     */
    public void removeDeadPlayer(SMUHCPlayer player) {
        deadPlayers.remove(player);
    }

    /**
     * @return Number of players dead
     */
    public int getNumberOfPlayersDead() {
        return deadPlayers.size();
    }

    /**
     * Updates waiting scoreboards
     */
    public void updateWaitingStatsScoreboards() {
        if (arena.getGameManager().isGameEmpty() || arena.getGameManager().isGameWaiting()) {
            for (SMUHCPlayer player : players) {
                player.getWaitingPlayerStatsDisplayManager().updateStatsScoreboard();
            }
        }
    }

    /**
     * Displays the waiting countdown for all players
     */
    public void displayWaitingCountdown() {
        for (SMUHCPlayer player : players) {
            arena.getGameManager().getWaitingCountdownDisplayManager().displayForPlayer(player.getBukkitPlayer());
        }
    }

    /**
     * Hides the waiting countdown from all players
     */
    public void hideWaitingCountdown() {
        arena.getGameManager().getWaitingCountdownDisplayManager().hideFromAllPlayers();
    }

    /**
     * Performs actions when a player dies in game
     *
     * @param player SMUHCPlayer
     */
    public void onPlayerDeath(SMUHCPlayer player) {
        if (arena.getGameManager().isGameInProgress()) {

            // Transition from alive to dead hash set
            removeAlivePlayer(player);
            addDeadPlayer(player);

            // Let everyone know
            sendMessageToAllPlayers(ChatColor.GRAY + player.getBukkitPlayer().getName());

            // See if there are still others alive
            if (getNumberOfPlayersAlive() >= 2) {
                arena.getGameManager().getPlayerManager().fireFirework(player, Color.RED);
                //Enter spectating mode
                getFighter(player).transitionToSpectatingMode();
                becomeSpectator(player);
            } else {
                // They were the last to die, so end the game
                String winner = arena.getGameManager().getPlayerManager().getAlivePlayers().toString();
                sendMessageToAllPlayers(SMUHC.smuhcPrefix + ChatColor.GREEN + "" + ChatColor.BOLD + winner + ChatColor.YELLOW + " is the winner of SuperMegaUltraHardcore!");

                arena.getGameManager().endGame();
            }
        }
    }

    public HashMap<SMUHCPlayer, Spectator> getSpectators() {
        return spectators;
    }

    /**
     * Adds a spectator to the HashMap
     *
     * @param spectator Spectator
     */
    private void addSpectator(Spectator spectator) {
        spectators.put(spectator.getSMUHCPlayer(), spectator);
    }

    /**
     * Removes a spectator from the HashMap
     *
     * @param player SMUHCPlayer
     */
    private void removeSpectator(SMUHCPlayer player) {
        spectators.remove(player);
    }

    /**
     * @param player SMUHCPlayer
     * @return Spectator
     */
    private Spectator getSpectator(SMUHCPlayer player) {
        return spectators.get(player);
    }

    /**
     * @return Number of spectators
     */
    public int getNumberOfSpectators() {
        return spectators.size();
    }

    /**
     * @param player SMUHCPlayer
     * @return If the player is a spectator
     */
    public boolean isSpectator(SMUHCPlayer player) {
        return spectators.containsKey(player);
    }

    /**
     * @return If there is enough room for the player to join
     */
    public synchronized boolean isRoomForPlayerToJoin() {
        //Determine if we have enough spawn points for the game's 8 counselors
        if (arena.getGameManager().getPlayerManager().getPlayers().size() <= 100) {
            //We don't have to worry about spawn points - Games capped at 8 counselors + Jason.
            return getNumberOfPlayers() < 100;
        } else {
            //They're are less than 8 spawn points for counselors
            return false;
        }
    }

    /**
     * Adds the player as a spectator and performs all actions to put them into the game
     *
     * @param player SMUHCPlayer
     */
    public void becomeSpectator(SMUHCPlayer player) {
        addSpectator(new Spectator(player, arena));
        getSpectator(player).enterSpectatingMode();

        try {
            SMUHC.arenaController.addPlayer(player, arena);
            addPlayer(player);
        } catch (PlayerAlreadyPlayingException exception) {
            //Don't need to do anything
        }
    }

    /**
     * Removes the player as a spectator and performs all actions to remove them from the game
     *
     * @param player SMUHCPlayer
     */
    public void leaveSpectator(SMUHCPlayer player) {
        //Need to remove from player lists if they're just a spectator
        if (getSpectators().containsKey(player)) {
            SMUHC.arenaController.removePlayer(player);
            removePlayer(player);
        }

        removeSpectator(player);
    }

    /**
     * Fires a firework
     *
     * @param player SMUHCPlayer whose location to fire the firework at
     * @param color  Firework color
     */
    public void fireFirework(SMUHCPlayer player, Color color) {
        Firework f = player.getBukkitPlayer().getWorld().spawn(player.getBukkitPlayer().getLocation().getWorld().getHighestBlockAt(player.getBukkitPlayer().getLocation()).getLocation(), Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder()
                .flicker(true)
                .trail(true)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(color)
                .build());
        fm.setPower(1);
        f.setFireworkMeta(fm);
    }

    /**
     * Removes player from all game data structures
     *
     * @param player SMUHCPlayer
     */
    private void removePlayerFromDataStructures(SMUHCPlayer player) {
        removePlayer(player);
        removeAlivePlayer(player);
        removeDeadPlayer(player);
        removeSpectator(player);
    }

    /**
     * Adds player to the game, if room is available
     *
     * @param player SMUHCPlayer
     */
    public synchronized void playerJoinGame(SMUHCPlayer player) throws GameFullException, GameInProgressException {
        if (arena.getGameManager().isGameEmpty() || arena.getGameManager().isGameWaiting()) {
            //Determine if there's room for this user
            if (isRoomForPlayerToJoin()) {
                try {
                    //Add to lists
                    SMUHC.arenaController.addPlayer(player, arena);
                    addPlayer(player);

                    //Prepare them for the game
                    assignFighter(player);
                    getFighter(player).enterWaitingRoom();

                    //Announce arrival
                    int playerNumber = players.size();
                    sendMessageToAllPlayers(ChatColor.GREEN + player.getBukkitPlayer().getName() + ChatColor.YELLOW + " has joined the game. There are now " + ChatColor.GREEN + playerNumber + ChatColor.YELLOW + " players in the game.");
                    if (players.size() == 1) {
                        arena.getSignManager().updateJoinSigns(); //If it's just them, update signs
                    }

                } catch (PlayerAlreadyPlayingException exception) {
                    //They're already in the controller global player list
                    player.getBukkitPlayer().sendMessage(SMUHC.smuhcPrefix + "Failed to add you to game because you're already registered as playing a game.");
                }
            } else {
                throw new GameFullException();
            }
        } else {
            throw new GameInProgressException();
        }
    }


    /**
     * Performs actions when a player quits the game via command
     *
     * @param player F13Player
     */
    public void onPlayerQuit(SMUHCPlayer player) {

        //Message everyone in game
        sendMessageToAllPlayers(ChatColor.AQUA + player.getBukkitPlayer().getName() + ChatColor.RED + " has left the game!");

        if (arena.getGameManager().isGameInProgress()) {
            if (isAlive(player) && getNumberOfPlayersAlive() <= 1) {
                //They were the last one
                //getPlayers(player).getXpManager().registerXPAward(XPAward.Counselor_Quitter);
                arena.getGameManager().endGame();
            } else {
                arena.getGameManager().getPlayerManager().removePlayer(player);
            }

        } else if (arena.getGameManager().isGameWaiting()) {
            if (players.size() < 2) {
                arena.getGameManager().changeGameStatus(GameStatus.Empty);
            }

        } else if (isSpectator(player)) {
            arena.getGameManager().getPlayerManager().getSpectator(player).leaveGame();
        } else {
            getFighter(player).leaveGame();
        }

        removePlayerFromDataStructures(player);
    }

    /**
     * Performs actions when a player logs off of server
     *
     * @param player F13Player
     */
    public void onPlayerLogout(SMUHCPlayer player) {
        //Hurry and see if we can teleport them out and clear inventory
        player.getBukkitPlayer().teleport(arena.getReturnLocation());
        player.getBukkitPlayer().getInventory().clear();

        //Message everyone in game
        sendMessageToAllPlayers(SMUHC.smuhcPrefix + ChatColor.AQUA + Bukkit.getOfflinePlayer(UUID.fromString(player.getPlayerUUID())).getName() + ChatColor.RED + " has logged out and left the game.");

        if (!isSpectator(player)) {
            if (arena.getGameManager().isGameInProgress()) {
                if (isAlive(player) && getNumberOfPlayersAlive() <= 1) {
                    //They were the last one
                    arena.getGameManager().endGame();
                } else {
                    getFighter(player).leaveGame();
                }
            }
        } else {
            //Just a spectator who logged out
            getSpectator(player).leaveGame();
        }
        removePlayerFromDataStructures(player);
    }
}