package com.sxtanna.mc.mb.revents.events;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AcidRain {

    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    public void commenceAcidRain() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ColorUtil.getHex("#65ff00A#6aff09C#70ff12I#75ff1bD #7bff25R#80ff2eA#86ff37I#8bff40N"), ColorUtil.getHex("#51cc00H#56d005i#5bd50bd#60d910e #64dd15f#69e11bo#6ee620r #73ea25c#78ee2bo#7df230v#81f735e#86fb3br#8bff40!"), 10, 60, 10);
        }

        Bukkit.getServer().getWorlds().forEach(world -> world.setStorm(true));

        new BukkitRunnable() {

            int i = 0;
            final int endAt = plugin.config.getInt("events.acid-rain.duration");

            @Override
            public void run() {
                if (i >= endAt) {
                    Bukkit.getServer().getWorlds().forEach(world -> world.setStorm(false));
                    this.cancel();
                    if (plugin.config.getBoolean("events.acid-rain.ended-message.enabled")) {
                        Bukkit.getServer().sendMessage(Component.text(format(plugin.config.getString("events.acid-rain.ended-message.msg"))));
                    }
                }

                Bukkit.getOnlinePlayers().forEach(player -> checkAndDamage(player));

                i++;

            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void checkAndDamage(Player player) {
        if (player.getWorld().getHighestBlockAt(player.getLocation()).getY() < player.getLocation().getY()) {
            player.damage(plugin.cachedAcidRainDamageAmount);
        }
    }

    public String format(String string) {
        return string.replace("", "§");
    }

}

