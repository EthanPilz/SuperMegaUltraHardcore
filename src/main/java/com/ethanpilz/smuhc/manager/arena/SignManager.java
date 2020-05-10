package com.ethanpilz.smuhc.manager.arena;

import com.ethanpilz.smuhc.components.arena.Arena;
import org.bukkit.block.data.type.Sign;

import java.util.HashSet;

public class SignManager {

    private Arena arena;

    private HashSet<Sign> joinSigns;

    public SignManager(Arena arena){

        this.arena = arena;
        this.joinSigns = new HashSet<>();

    }

}
