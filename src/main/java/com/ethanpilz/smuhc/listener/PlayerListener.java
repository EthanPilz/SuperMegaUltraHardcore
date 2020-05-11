package com.ethanpilz.smuhc.listener;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.rocket.Rocket;
import com.ethanpilz.smuhc.factory.SMUHCItemFactory;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")

public class PlayerListener implements Listener {

    public Map<String, Long> cooldowns = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event){
        if(event.getPlayer().isOp() || event.getPlayer().hasPermission("smuhc.admin")){
            event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.AQUA + "SuperMegaUltraHardcore currently running.");

        } if (event.getPlayer().getUniqueId().toString().equalsIgnoreCase("1ed071ee-25e2-44cc-9bda-f5442b92143e") || event.getPlayer().getUniqueId().toString().equalsIgnoreCase("d2f0ac46-9b4d-4a2b-9661-872ba65f9ac9")) {
            if (SMUHC.plugin.getConfig().getBoolean("broadcast")) {
                Bukkit.getServer().broadcastMessage(SMUHC.smuhcPrefix + ChatColor.YELLOW + "Plugin developer " + ChatColor.AQUA + event.getPlayer().getName() + ChatColor.YELLOW + " has joined");
                event.setJoinMessage(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe().getResult().getType().equals(Material.SHIELD) || event.getRecipe().getResult().getType().equals(Material.CROSSBOW)) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            for (HumanEntity he : event.getViewers()) {
                if (he instanceof Player) {
                    he.sendMessage(SMUHC.smuhcPrefix + ChatColor.WHITE +  "You cannot craft shields or crossbows in " + ChatColor.RED + "SuperMegaUltraHardcore!");
                    ((Player) he).playSound(he.getLocation(), Sound.BLOCK_GLASS_BREAK, 1,1);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRegen(EntityRegainHealthEvent event){
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.hasItem() && event.getItem().getType().equals(Material.TOTEM_OF_UNDYING)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.WHITE + "You cannot use Totem of Undying in " + ChatColor.RED + "SuperMegaUltraHardcore!");
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
            event.getPlayer().getInventory().remove(Material.TOTEM_OF_UNDYING);

        }

        if (event.hasItem() && event.getItem().equals(SMUHCItemFactory.FishBones())) {
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_AIR)) {
                if (cooldowns.containsKey(event.getPlayer().getName())) {
                    //Player is inside HashMap
                    if (cooldowns.get(event.getPlayer().getName()) > System.currentTimeMillis()) {
                        //Player has time left to wait
                        long timeLeft = (cooldowns.get(event.getPlayer().getName()) - System.currentTimeMillis()) / 1000;
                        event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "You have to wait " + ChatColor.AQUA + timeLeft + ChatColor.RED + " seconds before using that again.");
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 30);
                        return;
                    }
                }

                cooldowns.put(event.getPlayer().getName(), System.currentTimeMillis() + (60 * 1000));
                Bukkit.getServer().broadcastMessage(SMUHC.smuhcPrefix + ChatColor.AQUA + event.getPlayer().getName() + ChatColor.DARK_GRAY + ":" + ChatColor.GREEN + " Bye bye!");
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
                event.getPlayer().sendTitle("", ChatColor.RED + "Super Mega Death Rocket firing...", 5, 35, 5);
                Bukkit.getScheduler().scheduleSyncDelayedTask(SMUHC.plugin, new Runnable() {
                    @Override
                    public void run() {

                        new Rocket(event.getPlayer(), event.getPlayer().getEyeLocation());
                        Bukkit.getServer().broadcastMessage(SMUHC.smuhcPrefix + ChatColor.AQUA + event.getPlayer().getName() + ChatColor.RED + " has just fired the Super Mega Death Rocket!");
                    }
                }, 40L);

            } else {
                event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "This is an extremely powerful weapon, it cannot be detonated close to you! Aim far away!");
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerHandSwap(PlayerSwapHandItemsEvent event){
         if (event.getMainHandItem().getType().equals(Material.TOTEM_OF_UNDYING) || event.getOffHandItem().getType().equals(Material.TOTEM_OF_UNDYING)){
            event.setCancelled(true);
            event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.WHITE + "You cannot use Totem of Undying in " + ChatColor.RED + "SuperMegaUltraHardcore!");
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 1,1);
            event.getPlayer().getInventory().remove(Material.TOTEM_OF_UNDYING);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerAttack(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            ((Player) event.getEntity()).setExhaustion(0);
            ((Player) event.getDamager()).setExhaustion(0);
        }
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPortalTravel(PlayerPortalEvent event){
        if (event.getPlayer().getWorld().getEnvironment().equals(World.Environment.NORMAL)){
            Bukkit.getServer().broadcastMessage(SMUHC.smuhcPrefix + ChatColor.AQUA + event.getPlayer().getName() + ChatColor.GREEN + " has entered the nether!");
        } if (event.getPlayer().getWorld().getEnvironment().equals(World.Environment.NETHER)){
            Bukkit.getServer().broadcastMessage(SMUHC.smuhcPrefix + ChatColor.AQUA + event.getPlayer().getName() + ChatColor.GREEN + " has left the nether!");
        }
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPlaceBlock(BlockPlaceEvent event){
        if (event.getBlock().getType().equals(Material.WHITE_BED) || event.getBlock().getType().equals(Material.RED_BED)) {
            Location loc = event.getBlock().getLocation();
            loc.setY(loc.getY()+1);
            if(loc.getBlock().isLiquid()) {
                event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Beds can't explode underwater.");
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PARROT_EAT, 1,1);
                //Block is underwater, don't detonate.

            } else {
                event.getPlayer().sendTitle("", ChatColor.RED + "Bed detonating...", 20, 60, 20);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5,5);
                event.getBlock().getLocation().getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getBlock().getLocation(), 100, 0, 1, 0, 0.3);
                Bukkit.getScheduler().scheduleSyncDelayedTask(SMUHC.plugin, new Runnable() {
                    @Override
                    public void run() {

                        event.getPlayer().getWorld().createExplosion(event.getBlock().getLocation(), 5, true);
                        event.getBlock().getWorld().spawnParticle(Particle.DRAGON_BREATH, event.getBlock().getLocation(), 200, 0, 1, 0, 0.1);
                        event.getBlock().setType(Material.AIR);

                    }
                }, 60L); //3s delay before explosion
            } }
        if (event.getBlock().getType().equals(Material.TNT)){
            event.getBlock().setType(Material.AIR);
            event.getPlayer().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.PRIMED_TNT);
            }
        }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBreakBlock(BlockBreakEvent event){
        if (event.getBlock().getType().equals(Material.GOLD_ORE)){
            event.getBlock().getWorld().spawnParticle(Particle.END_ROD, event.getBlock().getLocation(), 20, 0, 0, 0, 0.1);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 40);
        }
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            event.setDeathMessage(null);
            event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
            event.getEntity().playSound(event.getEntity().getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
            Bukkit.getServer().broadcastMessage(SMUHC.smuhcPrefix + ChatColor.AQUA + event.getEntity().getName() + ChatColor.RED + " has been killed by " + ChatColor.AQUA + event.getEntity().getKiller().getName());

        }
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        if(event.getItem().getType().equals(Material.GOLDEN_APPLE)){
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 40);
        }
    }
}

