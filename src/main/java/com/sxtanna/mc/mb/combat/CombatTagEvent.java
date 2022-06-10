package com.sxtanna.mc.mb.combat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatTagEvent implements Listener {

    private final CombatAPI api = new CombatAPI();

    @EventHandler
    public void onCombatTag(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof Player damager)) return;

        if (player.getWorld().getName().equals("lobby")) {
            return;
        }

        this.api.tagPlayer(player);
        this.api.tagPlayer(damager);
    }

    @EventHandler
    public void onQuitWhileTagged(PlayerQuitEvent event) {
        if (this.api.isTagged(event.getPlayer())) {
            event.getPlayer().setHealth(0);
        }
    }
}
