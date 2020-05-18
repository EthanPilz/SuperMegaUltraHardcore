package com.ethanpilz.smuhc.components.rocket;

import com.ethanpilz.smuhc.SMUHC;
import com.ethanpilz.smuhc.factory.SMUHCItemFactory;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class SuperMegaDeathRocketComponent {

    public static ShapedRecipe Recipe(){

        ItemStack FishBones = SMUHCItemFactory.FishBones();

        NamespacedKey key = new NamespacedKey(SMUHC.instance, "super_mega_death_rocket");

        ShapedRecipe recipe = new ShapedRecipe(key, FishBones);
        recipe.shape("TGT", "GHG", "TGT");
        recipe.setIngredient('T', Material.TNT);
        recipe.setIngredient('G', Material.GOLDEN_APPLE);
        recipe.setIngredient('H', Material.DIAMOND_HOE);

        return recipe;

    }
}

