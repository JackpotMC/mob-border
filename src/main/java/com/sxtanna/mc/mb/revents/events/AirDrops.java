package com.sxtanna.mc.mb.revents.events;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.util.ColorUtil;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AirDrops {

    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;
    private ArrayList<Location> locations = new ArrayList<>();

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
                    clearLocs();
                    this.cancel();
                }

                Bukkit.getOnlinePlayers().forEach(player -> {
                    Location airdropLoc = randomLocation(player.getLocation());
                    player.getWorld().getBlockAt(airdropLoc).setType(Material.CHEST);
                    airdropLoc.getBlock().setMetadata("airdrop", new FixedMetadataValue(plugin, airdropLoc.getBlock()));
                    if (airdropLoc.getBlock().getType() == Material.CHEST) {
                        fillChest((Chest) airdropLoc.getBlock().getState());
                    }
                    particilizeAirdrop(airdropLoc);
                    locations.add(airdropLoc);
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

    public void fillChest(Chest chest) {
        Location blockLoc = chest.getLocation();
        List<String> possibleItems = plugin.config.getStringList("events.airdrops.possible-items");
        int addedItem = 0;
        int dropAmount = plugin.config.getInt("events.airdrops.rewards-amount");
        ItemStack[] rewardContents = new ItemStack[dropAmount];
        while (addedItem < dropAmount) {
            for (String s : possibleItems) {
                String[] args = s.split("\\s+");
                int percentage = Integer.parseInt(args[2]);
                int amount = Integer.parseInt(args[1]);
                Material reward = Material.valueOf(args[0]);

                int random = (int) (Math.random() * 100);
                if (percentage <= random) {
                    blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(reward, amount));
                    addedItem++;
                }
            }
        }
        chest.getInventory().setContents(rewardContents);
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

}
