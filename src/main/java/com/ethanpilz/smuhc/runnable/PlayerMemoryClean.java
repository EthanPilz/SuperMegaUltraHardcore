package com.ethanpilz.smuhc.runnable;

import com.ethanpilz.smuhc.SMUHC;

public class PlayerMemoryClean implements Runnable {
    @Override
    public void run() {
        SMUHC.playerController.cleanupMemory();
    }
}
