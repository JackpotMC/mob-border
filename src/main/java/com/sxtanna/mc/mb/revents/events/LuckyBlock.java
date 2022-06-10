package com.sxtanna.mc.mb.revents.events;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LuckyBlock implements Listener {

    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    private ArrayList<Location> locations = new ArrayList<>();

    public void spawnLuckyBlocks() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ColorUtil.getHex("#ffd700L#fed90eU#fddb1cC#fddc2bK#fcde39Y #fbe047B#fae255L#f9e463O#f9e572C#f8e780K#f7e98e!"), ColorUtil.getHex("#ffd700L#ffd807o#fed90eo#feda15k #fddb1cf#fddc24o#fddc2br #fcdd32t#fcde39h#fbdf40e #fbe047l#fbe14eu#fae255c#fae35ck#f9e463y #f9e56bb#f9e572l#f8e679o#f8e780c#f7e887k#f7e98es"), 10, 60, 10);
        }


        new BukkitRunnable() {

            int i = 0;
            final int amountPerPlayer = plugin.config.getInt("events.luckyblocks.amount-per-player");

            @Override
            public void run() {
                if (i >= amountPerPlayer) {
                    clearLocs();
                    this.cancel();
                }

                Bukkit.getOnlinePlayers().forEach(player -> {
                    Location luckyBlockLoc = randomLocation(player.getLocation());
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
    public void onLuckyBlockClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (!isLuckyBlock(event.getClickedBlock())) return;
        Block block = event.getClickedBlock();
        Location blockLoc = block.getLocation();

        event.setCancelled(true);
        block.setType(Material.AIR);
        blockLoc.getWorld().createExplosion(block.getX(), block.getY(), block.getZ(), 3, false, false);
        List<String> possibleItems = plugin.config.getStringList("events.luckyblocks.possible-items");
        int droppedItem = 0;
        while (droppedItem < plugin.config.getInt("events.luckyblocks.rewards-amount")) {
            for (String s : possibleItems) {
                String[] args = s.split("\\s+");
                int percentage = Integer.parseInt(args[2]);
                int amount = Integer.parseInt(args[1]);
                Material reward = Material.valueOf(args[0]);

                int random = (int) (Math.random() * 100);
                if (percentage <= random) {
                    blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(reward, amount));
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
        int x = (int) Math.round(Math.random());
        int y = (int) Math.round(Math.random());

        if (x == 0 && y == 0) {
            loc.add(15, 0, 15);
            loc.setY(loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()));
            return loc;
        }


        if (x == 1) {
            loc.add(15, 0, 0);
        }

        if (y == 1) {
            loc.add(0, 0, 15);
        }

        loc.setY(loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()));

        return loc;
    }

    private void clearLocs() {
        for (Location loc : locations) {
            loc.getBlock().setType(Material.AIR);
        }

        locations.clear();
    }

    public String format(String string) {
        return string.replace("&", "§");
    }

}
