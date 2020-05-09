package com.ethanpilz.smuhc.components.rocket;

import com.ethanpilz.smuhc.SMUHC;
import javafx.geometry.BoundingBox;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Rocket extends BukkitRunnable {

    private Item item;

    public Rocket(Player shooter, Location location){

        item = location.getWorld().dropItem(location, new ItemStack(Material.GHAST_TEAR));
        item.setVelocity(shooter.getEyeLocation().getDirection().multiply(3));
        runTaskTimer(JavaPlugin.getPlugin(SMUHC.class), 0, 2);
    }

    @Override
    public void run(){

        item.setPickupDelay(30);
        new InstantRocket(FireworkEffect.builder().with(FireworkEffect.Type.BURST).withColor(Color.RED).withFlicker().withFade(Color.ORANGE).build(), item.getLocation());
        if(item == null || item.isOnGround() || item.isDead() || item.isInvulnerable()){
            item.getLocation().getWorld().createExplosion(item.getLocation(), 15,true);
            item.getLocation().getWorld().createExplosion(item.getLocation(), 15,true);
            item.getLocation().getWorld().createExplosion(item.getLocation(), 15,true);
            cancel();
            item.remove();
        }

    }

}
