package com.sxtanna.mc.mb.revents;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.revents.events.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class EventManager {
    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    private final AcidRain acidRain = new AcidRain();
    private final AirDrops airDrops = new AirDrops();
    private final EndPortal endPortal = new EndPortal();
    private final LuckyBlock luckyBlock = new LuckyBlock();
    private final PetCreeper petCreeper = new PetCreeper();
    private final LootRain lootRain = new LootRain();


    public void startGlobalEvents() {

        long initialDelay = plugin.config.getLong("events.first-event-delay");
        long eventDelay = plugin.config.getLong("events.event-delay");

        new BukkitRunnable() {

            @Override
            public void run() {

                //random 0-5 (doesn't include 6)
                int i = ThreadLocalRandom.current().nextInt(0, 6);

                switch (i) {
                    case 0 -> {
                        acidRain.commenceAcidRain();
                    }
                    case 1 -> {
                        airDrops.spawnAirdrops();
                    }
                    case 2 -> {
                        endPortal.spawnRandomEndPortal();
                    }
                    case 3 -> {
                        luckyBlock.spawnLuckyBlocks();
                    }
                    case 4 -> {
                        petCreeper.spawnCreepers();
                    }
                    case 5 -> {
                        lootRain.rainLoot();
                    }
                }
            }
        }.runTaskTimer(plugin, initialDelay, eventDelay);
    }

}
