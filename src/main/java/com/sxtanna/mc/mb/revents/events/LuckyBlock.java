package com.sxtanna.mc.mb.revents.events;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.util.BukkitSerialization;
import com.sxtanna.mc.mb.util.ColorUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LuckyBlock implements Listener {

    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    private ArrayList<Location> locations = new ArrayList<>();

    public void spawnLuckyBlocks() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ColorUtil.getHex("#ffd700L#fed90eU#fddb1cC#fddc2bK#fcde39Y #fbe047B#fae255L#f9e463O#f9e572C#f8e780K#f7e98e!"), ColorUtil.getHex("#ffd700L#ffd807o#fed90eo#feda15k #fddb1cf#fddc24o#fddc2br #fcdd32t#fcde39h#fbdf40e #fbe047l#fbe14eu#fae255c#fae35ck#f9e463y #f9e56bb#f9e572l#f8e679o#f8e780c#f7e887k#f7e98es"), 10, 60, 10);
        }


        new BukkitRunnable() {

            int i = 1;
            final int amountPerPlayer = plugin.config.getInt("events.luckyblocks.amount-per-player");

            @Override
            public void run() {
                if (i >= amountPerPlayer) {
                    this.cancel();
                }

                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.getWorld().getName().equalsIgnoreCase("lobby")) return;
                    Location luckyBlockLoc = randomLocation(player.getLocation()).add(0, 1, 0);
                    int i = 0;
                    while (!player.getWorld().getWorldBorder().isInside(luckyBlockLoc)) {
                        if (i == 3) return;
                        luckyBlockLoc = randomLocation(player.getLocation()).add(0, 1, 0);
                        i++;
                    }
                    player.getWorld().getBlockAt(luckyBlockLoc).setType(Material.valueOf(plugin.config.getString("events.luckyblocks.material").toUpperCase()));
                    //lbl = Lucky Block Locations
                    luckyBlockLoc.getBlock().setMetadata("luckyblock", new FixedMetadataValue(plugin, luckyBlockLoc.getBlock()));
                    locations.add(luckyBlockLoc);
                });

                i++;

            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @EventHandler
    public void onLuckyBlockClick(PlayerInteractEvent event) throws IOException {
        if (event.getClickedBlock() == null) return;
        if (!isLuckyBlock(event.getClickedBlock())) return;
        Block block = event.getClickedBlock();
        Location blockLoc = block.getLocation();

        event.setCancelled(true);
        block.setType(Material.AIR);
        blockLoc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, blockLoc, 15, 0, 0, 0);
        List<String> possibleItems = plugin.config.getStringList("events.luckyblocks.possible-items");
        int droppedItem = 1;
        while (droppedItem < plugin.config.getInt("events.luckyblocks.rewards-amount")) {
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
                    blockLoc.getWorld().dropItemNaturally(blockLoc, reward);
                    droppedItem++;
                }
            }
        }
    }


    public boolean isLuckyBlock(Block b) {
        List<MetadataValue> metaDataValues = b.getMetadata("luckyblock");
        for (MetadataValue value : metaDataValues) {
            return value.asBoolean();
        }
        return false;
    }

    public Location randomLocation(Location loc) {

        int randomX = ThreadLocalRandom.current().nextInt(0, 15);
        int randomZ = ThreadLocalRandom.current().nextInt(0, 15);

        loc.setY(loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()));
        loc.add(randomX, 0, randomZ);

        return loc;
    }


    public String format(String string) {
        return string.replace("&", "§");
    }

}
