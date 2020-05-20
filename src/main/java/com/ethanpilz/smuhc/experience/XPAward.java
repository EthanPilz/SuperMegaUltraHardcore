package com.ethanpilz.smuhc.experience;

public enum XPAward {

    player_kill(-1, 5, "Player Kill");

    private int maxUses;
    private int xpAward;
    private String messageOnAward;

    XPAward(int maxUses, int xpAward, String messageOnAward) {
        this.maxUses = maxUses;
        this.xpAward = xpAward;
        this.messageOnAward = messageOnAward;
    }

    /**
     * Returns the XP amount for this award
     * @return XP amount
     */
    public int getXPAward()
    {
        return xpAward;
    }

    /**
     * Returns the max number of uses for this xp award
     *
     * @return Max number of uses
     */
    public int getMaxUses() {
        return maxUses;
    }

    /**
     * Returns the message to be sent to the player upon award
     *
     * @return Award message
     */
    public String getMessageOnAward() {
        String returnString;

        if (getXPAward() >= 0) {
            returnString = messageOnAward + ": +" + getXPAward() + "xp";
        } else {
            returnString = messageOnAward + ": -" + getXPAward() + "xp";
        }

        return returnString;
    }

}