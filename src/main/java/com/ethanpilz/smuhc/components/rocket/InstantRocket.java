package com.ethanpilz.smuhc.components.rocket;

import net.minecraft.server.v1_15_R1.EntityFireworks;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class InstantRocket {

    public InstantRocket(FireworkEffect fireworkEffect, Location location) {

        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(fireworkEffect);
        firework.setFireworkMeta(fireworkMeta);

        try {

            Object entityFirework = firework.getClass().getMethod("getHandle").invoke(firework);
            Field lifespan = entityFirework.getClass().getDeclaredField("expectedLifespan");
            lifespan.setAccessible(true);
            lifespan.set(entityFirework, 1);

        }
    catch (NoSuchMethodException e){
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

}
