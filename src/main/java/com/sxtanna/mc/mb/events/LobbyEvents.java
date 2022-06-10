package com.sxtanna.mc.mb.events;

import com.sxtanna.mc.mb.MobBorderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class LobbyEvents implements Listener {

    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    @EventHandler
    public void onDropBelow(PlayerMoveEvent event) {
        if (event.getPlayer().getWorld() != Bukkit.getWorld("lobby")) return;
        if(event.getTo().getY() < plugin.cachedYLevel) {
            plugin.teleportToWorld(event.getPlayer());
        }
    }

}
