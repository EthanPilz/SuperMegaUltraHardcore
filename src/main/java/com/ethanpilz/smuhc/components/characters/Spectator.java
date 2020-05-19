package com.ethanpilz.smuhc.components.characters;

import com.ethanpilz.smuhc.components.SMUHCPlayer;
import com.ethanpilz.smuhc.components.arena.Arena;
import org.bukkit.GameMode;

public class Spectator extends SMUHCCharacter {

    public Spectator(SMUHCPlayer player, Arena arena) {
        super(player, arena);
    }

    /**
     * Puts the player into spectate mode
     */
    public void enterSpectatingMode() {
        //Show countdown bar & scoreboard
        arena.getGameManager().getGameCountdownManager().showForPlayer(getSMUHCPlayer().getBukkitPlayer());
        arena.getGameManager().getGameScoreboardManager().displayForPlayer(getSMUHCPlayer().getBukkitPlayer());

        //Enter flight
        makePlayerVisibleToEveryone(false);
        getSMUHCPlayer().getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
        getSMUHCPlayer().getBukkitPlayer().setAllowFlight(true);
        getSMUHCPlayer().getBukkitPlayer().setFlying(true);
        getSMUHCPlayer().getBukkitPlayer().setHealth(20);
        getSMUHCPlayer().getBukkitPlayer().setWalkSpeed(0.2f);

        //Location
        getSMUHCPlayer().getBukkitPlayer().getInventory().clear();

        //Give them the selector
        //SpectateMenu.addMenuOpenItem(getSMUHCPlayer().getBukkitPlayer());

        //Let them know
        //ActionBarAPI.sendActionBar(getF13Player().getBukkitPlayer(), ChatColor.RED + FridayThe13th.language.get(getF13Player().getBukkitPlayer(), "actionBar.counselor.becomeSpectator", "You are now in spectating mode.", ChatColor.WHITE), 300);

        //Hide other spectators from this person
        for (Spectator existingSpectator : arena.getGameManager().getPlayerManager().getSpectators().values()) {
            if (!getSMUHCPlayer().getBukkitPlayer().equals(existingSpectator.getSMUHCPlayer().getBukkitPlayer())) {
                getSMUHCPlayer().getBukkitPlayer().hidePlayer(existingSpectator.getSMUHCPlayer().getBukkitPlayer());
            }
        }
    }
}