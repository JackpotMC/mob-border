package com.sxtanna.mc.mb.revents.events;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LootRain {

    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    public void rainLoot() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ColorUtil.getHex("#87ceebR#82d1edA#7cd4efI#77d7f0N#72daf2I#6dddf4N#67dff6G #62e2f8L#5de5faO#58e8fbO#52ebfdT#4deeff!"), ColorUtil.getHex("#87ceebL#82d1edo#7cd4efo#77d7f0k #72daf2i#6dddf4n #67dff6t#62e2f8h#5de5fae #58e8fbs#52ebfdk#4deeffy"), 10, 60, 10);
        }

        new BukkitRunnable() {

            int i = 0;
            final int amountPerPlayer = plugin.config.getInt("events.airdrops.amount-per-player");

            @Override
            public void run() {
                if (i >= amountPerPlayer) {
                    this.cancel();
                }

                Bukkit.getOnlinePlayers().forEach(player -> {
                    dropItems(player);
                });


                i++;

            }
        }.runTaskTimer(plugin, 20L, 20L);

    }

    private void dropItems(Player player) {
        List<String> possibleItems = plugin.config.getStringList("events.loot-rain.possible-items");
        int droppedItem = 0;
        while (droppedItem < plugin.config.getInt("events.loot-rain.rewards-amount")) {
            for (String s : possibleItems) {
                String[] args = s.split("\\s+");
                int percentage = Integer.parseInt(args[2]);
                int amount = Integer.parseInt(args[1]);
                Material reward = Material.valueOf(args[0]);

                int random = (int) (Math.random() * 100);
                if (percentage <= random) {
                    player.getWorld().dropItemNaturally(findValidLocation(player.getLocation()), new ItemStack(reward, amount));
                    droppedItem++;
                }
            }
        }
    }

    private Location findValidLocation(Location location) {
        Location spawnLoc = location.clone();
        int randomX = ThreadLocalRandom.current().nextInt(0, 3);
        int randomZ = ThreadLocalRandom.current().nextInt(0, 3);
        spawnLoc.add(randomX, 45, randomZ);
        return spawnLoc;
    }

}
