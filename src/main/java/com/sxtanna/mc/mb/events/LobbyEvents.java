package com.sxtanna.mc.mb.events;

import com.sxtanna.mc.mb.MobBorderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class LobbyEvents implements Listener {

    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    @EventHandler
    public void onDropBelow(PlayerMoveEvent event) {
        if (event.getPlayer().getWorld() != Bukkit.getWorld("lobby")) return;
        if (event.getTo().getY() < plugin.cachedYLevel) {
            plugin.teleportToWorld(event.getPlayer());
        }
    }

    @EventHandler
    public void onMoveWhileSpawning(PlayerMoveEvent event) {
        if (!plugin.spawning.contains(event.getPlayer().getUniqueId())) return;
        Location from = event.getFrom();
        Location to = event.getTo();
        if ((int) from.getX() != (int) to.getX() || (int) from.getY() != (int) to.getY() || (int) from.getZ() != (int) to.getZ()) {
            plugin.spawning.remove(event.getPlayer().getUniqueId());
        }
    }

}
