package com.sxtanna.mc.mb.revents.events;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class EndPortal {

    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    private ArrayList<Location> locations = new ArrayList<>();

    public void spawnRandomEndPortal() {

        Location validLoc = findValidLocation(plugin.getEntity().get().location());

        createEndPortal(validLoc);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ColorUtil.getHex("#aa1babE#b128b4N#b934beD #c041c7P#c84dd0O#cf5adaR#d766e3T#de73ecA#e67ff6L#ed8cff!"), ColorUtil.getHex("#D000BB(" + (int) validLoc.getX() + ", " + (int) validLoc.getY() + ", " + (int) validLoc.getZ() + ")"), 10, 80, 10);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                clearLocs();
            }
        }.runTaskLater(plugin, (plugin.config.getInt("events.end-portal.duration") * 20));
    }


    public void createEndPortal(Location loc1) {
        World w = loc1.getWorld();
        Location origLoc1 = loc1.clone();

        w.getBlockAt(loc1).setType(Material.END_PORTAL); // ORIG - 24,112,9
        loc1.add(1, 0, 0); // 25,112,9
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL);
        loc1 = origLoc1.clone();
        loc1.add(1, 0, -1); // 25,112,8
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL);
        loc1 = origLoc1.clone();
        loc1.add(1, 0, 1); // 25,112,10
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL);
        loc1 = origLoc1.clone();
        loc1.add(0, 0, -1); // 24,112,8
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL);
        loc1 = origLoc1.clone();
        loc1.add(0, 0, 1); // 24,112,10
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL);
        loc1 = origLoc1.clone();
        loc1.add(-1, 0, -1); // 23,112,8
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL);
        loc1 = origLoc1.clone();
        loc1.add(-1, 0, 0); // 23,112,9
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL);
        loc1 = origLoc1.clone();
        loc1.add(-1, 0, 1); // 23,112,10
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL);
        loc1 = origLoc1.clone();

        /* Ender Portal 01 - Frame */   // ORIG - 24,112,9
        loc1.add(2, 0, -1); // 26,112,8
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL_FRAME);
        loc1 = origLoc1.clone();
        loc1.add(2, 0, 0); // 26,112,9
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL_FRAME);
        loc1 = origLoc1.clone();
        loc1.add(2, 0, 1); // 26,112,10
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL_FRAME);
        loc1 = origLoc1.clone();
        loc1.add(1, 0, -2); // 25,112,7
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL_FRAME);
        loc1 = origLoc1.clone();
        loc1.add(0, 0, -2); // 24,112,7
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL_FRAME);
        loc1 = origLoc1.clone();
        loc1.add(-1, 0, -2); // 23,112,7
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL_FRAME);
        loc1 = origLoc1.clone();
        loc1.add(1, 0, 2); // 25,112,11
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL_FRAME);
        loc1 = origLoc1.clone();
        loc1.add(0, 0, 2); // 24,112,11
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL_FRAME);
        loc1 = origLoc1.clone();
        loc1.add(-1, 0, 2); // 23,112,11
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL_FRAME);
        loc1 = origLoc1.clone();
        loc1.add(-2, 0, -1); // 22,112,8
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL_FRAME);
        loc1 = origLoc1.clone();
        loc1.add(-2, 0, 0); // 22,112,9
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL_FRAME);
        loc1 = origLoc1.clone();
        loc1.add(-2, 0, 1); // 22,112,10
        locations.add(loc1);
        w.getBlockAt(loc1).setType(Material.END_PORTAL_FRAME);
        loc1 = origLoc1.clone();
        locations.add(loc1);
    }

    private Location findValidLocation(Location center) {
        Location spawnLoc = center.clone();
        int randomX = ThreadLocalRandom.current().nextInt(0, 26);
        int randomZ = ThreadLocalRandom.current().nextInt(0, 26);
        while (!Bukkit.getWorld("world").getWorldBorder().isInside(spawnLoc.add(randomX, 5, randomZ))) {
            randomX = ThreadLocalRandom.current().nextInt(0, 26);
            randomZ = ThreadLocalRandom.current().nextInt(0, 26);
        }
        spawnLoc.add(randomX, 5, randomZ);
        return spawnLoc;
    }

    private void clearLocs() {
        for (Location loc : locations) {
            loc.getBlock().setType(Material.AIR);
        }

        locations.clear();
    }

}
