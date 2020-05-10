package com.ethanpilz.smuhc.factory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SMUHCItemFactory {

    //Super Mega Death Rocket, also known as FishBones
    public static ItemStack FishBones(){

        ItemStack FishBones = new ItemStack(Material.DIAMOND_HOE);
        ItemMeta meta = FishBones.getItemMeta();
        meta.setLore(Arrays.asList(ChatColor.RESET + "" + ChatColor.GRAY + "Blast Radius X"));

        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "SUPER MEGA DEATH ROCKET");
        meta.addEnchant(Enchantment.FIRE_ASPECT, 10, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        FishBones.setItemMeta(meta);

        return FishBones;
    }

}
