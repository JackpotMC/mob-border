package com.sxtanna.mc.mb.revents.events;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.util.BukkitSerialization;
import com.sxtanna.mc.mb.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
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
                    if (player.getWorld().getName().equalsIgnoreCase("lobby")) return;
                    try {
                        dropItems(player);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });


                i++;

            }
        }.runTaskTimer(plugin, 20L, 20L);

    }

    private void dropItems(Player player) throws IOException {
        List<String> possibleItems = plugin.config.getStringList("events.loot-rain.possible-items");
        int droppedItem = 1;
        while (droppedItem < plugin.config.getInt("events.loot-rain.rewards-amount")) {
            for (String s : possibleItems) {
                String[] args = s.split("\\s+");

                String finalItem = "";
                for (int i = 0; i < args.length - 2; i++) {
                    finalItem += args[i] + " ";
                }

                ItemStack reward = BukkitSerialization.itemStackFromBase64(finalItem);
                int percentage = Integer.parseInt(args[args.length - 1]);

                int random = (int) (Math.random() * 100);
                if (percentage <= random) {
                    if (reward == null) continue;
                    player.getWorld().dropItemNaturally(findValidLocation(player.getLocation()), reward);
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
