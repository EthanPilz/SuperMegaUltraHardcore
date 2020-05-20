package com.ethanpilz.smuhc.listener;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.components.SMUHCPlayer;
import com.ethanpilz.smuhc.components.arena.Arena;
import com.ethanpilz.smuhc.components.rocket.Rocket;
import com.ethanpilz.smuhc.exceptions.game.GameFullException;
import com.ethanpilz.smuhc.exceptions.game.GameInProgressException;
import com.ethanpilz.smuhc.exceptions.player.PlayerNotPlayingException;
import com.ethanpilz.smuhc.factory.SMUHCItemFactory;
import org.bukkit.*;
import org.bukkit.block.Sign;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unused")

public class PlayerListener implements Listener {

    public Map<String, Long> cooldowns = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event){
        if(event.getPlayer().isOp() || event.getPlayer().hasPermission("smuhc.admin")){
            event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.AQUA + "SuperMegaUltraHardcore currently running.");

        } if (event.getPlayer().getUniqueId().toString().equalsIgnoreCase("1ed071ee-25e2-44cc-9bda-f5442b92143e") || event.getPlayer().getUniqueId().toString().equalsIgnoreCase("d2f0ac46-9b4d-4a2b-9661-872ba65f9ac9")) {
            //if (SMUHC.plugin.getConfig().getBoolean("broadcast")) {
                Bukkit.getServer().broadcastMessage(SMUHC.smuhcPrefix + ChatColor.YELLOW + "Plugin developer " + ChatColor.AQUA + event.getPlayer().getName() + ChatColor.YELLOW + " has joined");
                event.setJoinMessage(null);
           // }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCraft(PrepareItemCraftEvent event) {
        if (SMUHC.arenaController.isPlaying((Player) event.getViewers())) {
            if (event.getRecipe().getResult().getType().equals(Material.SHIELD) || event.getRecipe().getResult().getType().equals(Material.CROSSBOW)) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                for (HumanEntity he : event.getViewers()) {
                    if (he instanceof Player) {
                        he.sendMessage(SMUHC.smuhcPrefix + ChatColor.WHITE + "You cannot craft shields or crossbows in " + ChatColor.RED + "SuperMegaUltraHardcore!");
                        ((Player) he).playSound(he.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerRegen(EntityRegainHealthEvent event) {
        if (event.getEntity().getType().equals(EntityType.PLAYER)) {
            if (SMUHC.arenaController.isPlaying((Player) event.getEntity())) {
                if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN)
                    ((Player) event.getEntity()).playSound(event.getEntity().getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, -50);
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (SMUHC.arenaController.isPlaying(event.getPlayer())) {
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
                    Bukkit.getScheduler().scheduleSyncDelayedTask(SMUHC.instance, new Runnable() {
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
        //Here, we don't want them to be playing because they need to join before they can be considered a player.
        if (event.hasBlock() && (event.getClickedBlock().getType().equals(Material.OAK_WALL_SIGN) || event.getClickedBlock().getType().equals(Material.OAK_SIGN))) {
            Sign sign = (Sign) event.getClickedBlock().getState();

            Iterator it = SMUHC.arenaController.getArenas().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                Arena arena = (Arena) entry.getValue();
                if (arena.getSignManager().isJoinSign(sign)) {
                    try {
                        arena.getGameManager().getPlayerManager().playerJoinGame(SMUHC.playerController.getPlayer(event.getPlayer()));
                        //} catch (GameFullException e) {
                        //event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "The game in " + ChatColor.AQUA + arena.getName() + ChatColor.RED + " is full.");
                    } catch (GameInProgressException | GameFullException e) {
                        //Enter as a spectator
                        arena.getGameManager().getPlayerManager().becomeSpectator(SMUHC.playerController.getPlayer(event.getPlayer()));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerHandSwap(PlayerSwapHandItemsEvent event){
        if (SMUHC.arenaController.isPlaying(event.getPlayer())) {
            if (event.getMainHandItem().getType().equals(Material.TOTEM_OF_UNDYING) || event.getOffHandItem().getType().equals(Material.TOTEM_OF_UNDYING)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.WHITE + "You cannot use Totem of Undying in " + ChatColor.RED + "SuperMegaUltraHardcore!");
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                event.getPlayer().getInventory().remove(Material.TOTEM_OF_UNDYING);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event){
        if (SMUHC.arenaController.isPlaying((Player) event.getEntity())) {
            if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
                ((Player) event.getEntity()).setExhaustion(0);
                ((Player) event.getDamager()).setExhaustion(0);
            }
        }
    }
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPortalTravel(PlayerPortalEvent event){
        if (SMUHC.arenaController.isPlaying(event.getPlayer())) {
            if (event.getPlayer().getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                Bukkit.getServer().broadcastMessage(SMUHC.smuhcPrefix + ChatColor.AQUA + event.getPlayer().getName() + ChatColor.GREEN + " has entered the nether!");
            }
            if (event.getPlayer().getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                Bukkit.getServer().broadcastMessage(SMUHC.smuhcPrefix + ChatColor.AQUA + event.getPlayer().getName() + ChatColor.GREEN + " has left the nether!");
            }
        }
    }
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (SMUHC.arenaController.isPlaying(event.getPlayer())) {
            if (event.getBlock().getType().equals(Material.WHITE_BED) || event.getBlock().getType().equals(Material.RED_BED)) {
                Location loc = event.getBlock().getLocation();
                loc.setY(loc.getY() + 1);
                if (loc.getBlock().isLiquid()) {
                    event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Beds can't explode underwater.");
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PARROT_EAT, 1, 1);
                    //Block is underwater, don't detonate.

                } else {
                    event.getPlayer().sendTitle("", ChatColor.RED + "Bed detonating...", 20, 60, 20);
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5, 5);
                    event.getBlock().getLocation().getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getBlock().getLocation(), 100, 0, 1, 0, 0.3);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(SMUHC.instance, new Runnable() {
                        @Override
                        public void run() {

                            event.getPlayer().getWorld().createExplosion(event.getBlock().getLocation(), 5, true);
                            event.getBlock().getWorld().spawnParticle(Particle.DRAGON_BREATH, event.getBlock().getLocation(), 200, 0, 1, 0, 0.1);
                            event.getBlock().setType(Material.AIR);

                        }
                    }, 60L); //3s delay before explosion
                }
            }
            if (event.getBlock().getType().equals(Material.TNT)) {
                event.getBlock().setType(Material.AIR);
                event.getPlayer().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.PRIMED_TNT);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (SMUHC.arenaController.isPlaying(event.getPlayer())) {
            if (event.getBlock().getType().equals(Material.GOLD_ORE)) {
                event.getBlock().getWorld().spawnParticle(Particle.END_ROD, event.getBlock().getLocation(), 20, 0, 0, 0, 0.1);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 40);
            }
        }
    }
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) throws PlayerNotPlayingException {
        if (SMUHC.arenaController.isPlaying(event.getEntity())) {
            // This should never ever happen
            try {
                Player player = event.getEntity();
                SMUHC.arenaController.getPlayerArena(player).getGameManager().getPlayerManager().onPlayerDeath(SMUHC.playerController.getPlayer(player)); //See if they're playing

                event.setDeathMessage(null);
                event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
                event.getEntity().playSound(event.getEntity().getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
                Bukkit.getServer().broadcastMessage(SMUHC.smuhcPrefix + ChatColor.AQUA + event.getEntity().getName() + ChatColor.RED + " has been killed by " + ChatColor.AQUA + event.getEntity().getKiller().getName());

            } catch (PlayerNotPlayingException exception) {
                //Don't care
            }
        }
    }
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        if (SMUHC.arenaController.isPlaying(event.getPlayer())) {
            if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 40);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            SMUHC.arenaController.getPlayerArena(event.getPlayer()).getGameManager().getPlayerManager().onPlayerLogout(SMUHC.playerController.getPlayer(event.getPlayer()));
        } catch (PlayerNotPlayingException e){
            // Not playing, don't care.
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {

        if (SMUHC.arenaController.isPlaying(event.getPlayer())) {
            event.setCancelled(true); //Cannot manipulate armor stands while playing
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (SMUHC.arenaController.isPlaying(event.getPlayer())) {
            if (!event.getMessage().toLowerCase().startsWith("/smuhc")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(SMUHC.smuhcPrefix + ChatColor.RED + "Only SuperMegaUltraHardcore commands are available during gameplay.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            try {
                Player temp = (Player) event.getEntity();

                //Make sure they're an actual player and not an NPC
                if (Bukkit.getPlayer(temp.getUniqueId()) != null) {
                    SMUHCPlayer playerDamaged = SMUHC.playerController.getPlayer((Player) event.getEntity());
                    Arena arena = SMUHC.arenaController.getPlayerArena(playerDamaged); //See if they're playing

                    if (arena.getGameManager().isGameInProgress()) {
                        if (arena.getGameManager().getPlayerManager().isSpectator(playerDamaged)) {
                            event.setCancelled(true); //You can't get hurt in spectate mode
                        } else {
                            if (event instanceof EntityDamageByEntityEvent) {
                                EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent) event;
                                if (edbeEvent.getDamager() instanceof Player) {
                                    //The person doing the damage is a player, too.
                                    SMUHCPlayer playerDamager = SMUHC.playerController.getPlayer((Player) edbeEvent.getDamager());

                                    if (SMUHC.arenaController.isPlaying(playerDamager)) {
                                        //The person doing the damage is playing
                                        if (arena.getGameManager().getPlayerManager().isSpectator(playerDamager)) {
                                            //The damage is a player in spectate mode
                                            event.setCancelled(true);
                                        }
                                    }
                                }
                            }
                        }
                        if (!event.isCancelled()) {
                            if (playerDamaged.getBukkitPlayer().getHealth() <= event.getDamage()) {
                                //This blow would kill them
                                event.setCancelled(true);
                                playerDamaged.getBukkitPlayer().setHealth(20);
                                arena.getGameManager().getPlayerManager().onPlayerDeath(playerDamaged);
                            }
                        }
                    } else {
                        //You can't get damaged while waiting
                        event.setCancelled(true);
                    }
                }

            } catch (PlayerNotPlayingException exception) {

            }
        }
    }
}

