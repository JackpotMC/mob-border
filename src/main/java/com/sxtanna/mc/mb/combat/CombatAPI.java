package com.sxtanna.mc.mb.combat;

import com.sxtanna.mc.mb.MobBorderPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CombatAPI {
    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    public boolean isTagged(Player player) {
        UUID uuid = player.getUniqueId();
        long lastTag = plugin.combatLog.getOrDefault(uuid, 0L);
        long currentTime = System.currentTimeMillis();
        long timeTill = lastTag + plugin.config.getInt("combat.tag-duration") * 1000L - currentTime;
        return (timeTill > 0L);
    }

    public void tagPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (plugin.combatLog.get(uuid) != null &&  isTagged(player)) {
            plugin.combatLog.put(uuid, System.currentTimeMillis());
            return;
        }
        plugin.combatLog.put(uuid, System.currentTimeMillis());
        player.sendMessage(format(plugin.config.getString("combat.message").replace("%duration%", String.valueOf(plugin.config.get("combat.tag-duration")))));
    }

    public void removeTag(Player player) {
        UUID uuid = player.getUniqueId();
        if (plugin.combatLog.get(uuid) != null)
            plugin.combatLog.remove(uuid);
    }

    public int remainingTagTimer(Player player) {
        UUID uuid = player.getUniqueId();
        long lastTag = plugin.combatLog.getOrDefault(uuid, 0L);
        long currentTime = System.currentTimeMillis();
        long timeTill = lastTag + plugin.config.getInt("combat.tag-duration") * 1000L - currentTime;
        return (int) (timeTill / 1000);
    }

    private String format(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}