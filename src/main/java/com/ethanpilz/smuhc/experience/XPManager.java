package com.ethanpilz.smuhc.experience;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.SMUHCPlayer;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.components.level.XPAward;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class XPManager {

    private Arena arena;
    private SMUHCPlayer player;
    private ArrayList<XPAward> xpAwards;

    public XPManager(SMUHCPlayer player, Arena arena) {
        this.arena = arena;
        this.player = player;
        this.xpAwards = new ArrayList<>();
    }

    /**
     * Adds XP Award
     *
     * @param toAward XP Award
     */
    public void registerXPAward(XPAward toAward) {
        Map<XPAward, Long> counts = xpAwards.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        if ((!counts.containsKey(toAward) || (counts.containsKey(toAward) && counts.get(toAward) < toAward.getMaxUses()))) {
            xpAwards.add(toAward);

            //Send the message
           // if (!toAward.getMessageOnAward().isEmpty() && player.isOnline()) {
                //ActionBarAPI.sendActionBar(player.getBukkitPlayer(), toAward.getMessageOnAward(), 60);
            }
        }
    //}

/*    *//**
     * @param toAward
     * @return If the award's character type matches the role of the player
     *//*
    private boolean doesAwardXPMatchPlayersType(XPAward toAward) {
        return ((arena.getGameManager().getPlayerManager().getPlayers()));
    }*/

    /**
     * Calculates the total XP for the game for the player
     *
     * @return Total XP for player's current game
     */
    private int calculateTotalXP() {
        Map<XPAward, Long> counts = xpAwards.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        int xp = 0;

        Iterator it = counts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            XPAward award = (XPAward) entry.getKey();
            Long count = (Long) entry.getValue();

            xp += award.getXPAward() * count.intValue(); //Max # of uses checking is done before they're added to the ArrayList, so we can just use the count value here
        }

        return Math.max(0, xp);
    }

    /**
     * Awards XP from the game to the player
     */
    public void awardXPToPlayer() {
        if (arena.getGameManager().getPlayerManager().isPlaying(player)) {
            registerXPAward(XPAward.player_kill);
        }

        int xpToAward = calculateTotalXP();
        player.addXP(xpToAward);

        //Send confirmation message to the player
        if (player.isOnline()) {
            player.getBukkitPlayer().sendMessage(SMUHC.smuhcPrefix + "You earned" + "xp from this round and now have a total of" + xpToAward +"xp.");
        }
    }
}
