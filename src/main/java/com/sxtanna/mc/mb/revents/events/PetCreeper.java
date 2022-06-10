package com.sxtanna.mc.mb.revents.events;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PetCreeper {

    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    public void spawnCreepers() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ColorUtil.getHex("#0f800fP#1d8b1dE#2b962bT #3aa13aC#48ac48R#56b756E#64c264E#73cd73P#81d881E#8fe38fR"), ColorUtil.getHex("#0f800f?#0f800f!"), 10, 60, 10);
        }

        new BukkitRunnable() {

            int i = 0;
            final int amountPerPlayer = plugin.config.getInt("events.creepers.amount-per-player");

            @Override
            public void run() {
                if (i >= amountPerPlayer) {
                    this.cancel();
                }

                Bukkit.getOnlinePlayers().forEach(player -> {
                    Location spawnLoc = player.getLocation();
                    player.getWorld().spawnEntity(newLocation(spawnLoc), EntityType.CREEPER);
                });

                i++;

            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public Location newLocation(Location loc) {
        int x = (int) Math.round(Math.random());
        int y = (int) Math.round(Math.random());

        if (x == 0 && y == 0) {
            loc.add(5, 0, 5);
            loc.setY(loc.getWorld().getHighestBlockYAt(loc.getBlockX() + 5, loc.getBlockZ() + 5));
            return loc;
        }


        if (x == 1) {
            loc.add(5, 0, 0);
        }

        if (y == 1) {
            loc.add(0, 0, 5);
        }

        loc.setY(loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()));

        return loc;
    }

}
