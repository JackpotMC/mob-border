package com.sxtanna.mc.mb.events;

import com.sxtanna.mc.mb.MobBorderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class HideInLobby implements Listener {

    private MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("lobby")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(plugin, player);
            }
            plugin.hiddenPlayers.add(player.getUniqueId());
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(plugin, player);
            }

            if (!plugin.hiddenPlayers.contains(player.getUniqueId())) return;

            plugin.hiddenPlayers.remove(player.getUniqueId());

        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player quitter = event.getPlayer();

        //show all current hidden players
        for (UUID uuid : plugin.hiddenPlayers) {
            if (uuid == quitter.getUniqueId()) return;
            event.getPlayer().showPlayer(plugin, Bukkit.getPlayer(uuid));
        }

        if (plugin.hiddenPlayers.contains(quitter.getUniqueId())) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getUniqueId() == quitter.getUniqueId()) continue;
                player.showPlayer(plugin, quitter);
            }
            plugin.hiddenPlayers.remove(quitter.getUniqueId());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        //hide all current hidden players
        for (UUID uuid : plugin.hiddenPlayers) {
            event.getPlayer().hidePlayer(plugin, Bukkit.getPlayer(uuid));
        }

        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("lobby")) return;

        plugin.hiddenPlayers.add(event.getPlayer().getUniqueId());
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(plugin, event.getPlayer());
        }
    }


}
