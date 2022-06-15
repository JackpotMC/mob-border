package com.sxtanna.mc.mb.revents.events;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.util.BukkitSerialization;
import com.sxtanna.mc.mb.util.ColorUtil;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AirDrops implements Listener {

    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    @EventHandler
    public void onChestClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof Chest) {
            Chest chest = (Chest) inv.getHolder();
            if (!plugin.airdropLocations.contains(chest.getLocation())) return;
            ItemStack[] contents = chest.getBlockInventory().getContents();
            for (ItemStack item : contents) {
                if (item == null) continue;
                inv.getLocation().getWorld().dropItemNaturally(inv.getLocation(), item);
            }
            inv.getLocation().getBlock().setType(Material.AIR);
            plugin.airdropLocations.remove(inv.getLocation());
        }
    }

    public void spawnAirdrops() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ColorUtil.getHex("#d2042dA#d40a34I#d50f3bR #d71541D#d81b48R#da204fO#db2656P#dd2b5cS#de3163!"), ColorUtil.getHex("#d2042dL#d30932o#d40d38o#d6123dk #d71643u#d81b48p #d91f4da#da2453b#dc2858o#dd2d5ev#de3163e"), 10, 60, 10);
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
                    Location airdropLoc = randomLocation(player.getLocation()).add(0, 1, 0);
                    int i = 0;
                    while (!player.getWorld().getWorldBorder().isInside(airdropLoc)) {
                        if (i == 3) return;
                        airdropLoc = randomLocation(player.getLocation()).add(0, 1, 0);
                        i++;
                    }
                    player.getWorld().getBlockAt(airdropLoc).setType(Material.CHEST);
                    airdropLoc.getBlock().setMetadata("airdrop", new FixedMetadataValue(plugin, airdropLoc.getBlock()));
                    if (airdropLoc.getBlock().getType() == Material.CHEST) {
                        try {
                            fillChest((Chest) airdropLoc.getBlock().getState());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    particilizeAirdrop(airdropLoc);
                    plugin.airdropLocations.add(airdropLoc.getBlock().getLocation());
                });

                i++;

            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void particilizeAirdrop(Location airdropLoc) {

        new BukkitRunnable() {

            int i = 3;


            @Override
            public void run() {
                if (i <= 0) {
                    this.cancel();
                }

                Location airdropLocClone = airdropLoc.clone();

                Firework fw = airdropLocClone.getWorld().spawn(airdropLocClone.add(0, i, 0), Firework.class);
                FireworkMeta fwm = fw.getFireworkMeta();

                FireworkEffect effect = FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BALL).build();

                fwm.addEffects(effect);
                fwm.setPower(0);
                fw.setFireworkMeta(fwm);
                i--;
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    public void fillChest(Chest chest) throws IOException {
        List<String> possibleItems = plugin.config.getStringList("events.airdrops.possible-items");
        int addedItem = 1;
        int dropAmount = plugin.config.getInt("events.airdrops.rewards-amount");
        while (addedItem < dropAmount) {
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
                    int i = ThreadLocalRandom.current().nextInt(0, 27);

                    while (chest.getBlockInventory().getItem(i) != null) {
                        i = ThreadLocalRandom.current().nextInt(0, 27);
                    }

                    chest.getBlockInventory().setItem(i, reward);
                    addedItem++;
                }
            }
        }
    }

    public Location randomLocation(Location loc) {
        int randomX = ThreadLocalRandom.current().nextInt(0, 15);
        int randomZ = ThreadLocalRandom.current().nextInt(0, 15);

        loc.setY(loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()));
        loc.add(randomX, 0, randomZ);

        return loc;
    }
}
