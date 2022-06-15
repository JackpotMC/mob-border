package com.sxtanna.mc.mb.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class AutoSmelt implements Listener {

    @EventHandler
    public void onMineEvent(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("jmc.autosmelt")) {
            Block block = event.getBlock();
            Location blockLoc = block.getLocation();
            Player player = event.getPlayer();

            switch (event.getBlock().getType()) {
                case IRON_ORE, DEEPSLATE_IRON_ORE -> {
                    event.setCancelled(true);
                    if (player.hasPermission("jmc.donator.double-ores")) {
                        blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.IRON_INGOT, block.getDrops().size()));
                    }
                    blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.IRON_INGOT, block.getDrops().size()));
                    blockLoc.getWorld().spawn(blockLoc, ExperienceOrb.class).setExperience(event.getExpToDrop());

                    block.setType(Material.AIR);
                }
                case GOLD_ORE, DEEPSLATE_GOLD_ORE -> {
                    event.setCancelled(true);
                    if (player.hasPermission("jmc.donator.double-ores")) {
                        blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.GOLD_INGOT, block.getDrops().size()));
                    }

                    blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.GOLD_INGOT, block.getDrops().size()));
                    blockLoc.getWorld().spawn(blockLoc, ExperienceOrb.class).setExperience(event.getExpToDrop());

                    block.setType(Material.AIR);
                }
                case COAL_ORE, DEEPSLATE_COAL_ORE -> {
                    event.setCancelled(true);
                    if (player.hasPermission("jmc.donator.double-ores")) {
                        blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.COAL, block.getDrops().size()));
                    }

                    blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.COAL, block.getDrops().size()));
                    blockLoc.getWorld().spawn(blockLoc, ExperienceOrb.class).setExperience(event.getExpToDrop());

                    block.setType(Material.AIR);
                }
                case COPPER_ORE, DEEPSLATE_COPPER_ORE -> {
                    event.setCancelled(true);
                    if (player.hasPermission("jmc.donator.double-ores")) {
                        blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.COPPER_INGOT, block.getDrops().size()));
                    }

                    blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.COPPER_INGOT, block.getDrops().size()));
                    blockLoc.getWorld().spawn(blockLoc, ExperienceOrb.class).setExperience(event.getExpToDrop());


                    block.setType(Material.AIR);
                }
                case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> {
                    event.setCancelled(true);
                    if (player.hasPermission("jmc.donator.double-ores")) {
                        blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.LAPIS_LAZULI, block.getDrops().size()));
                    }

                    blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.LAPIS_LAZULI, block.getDrops().size()));
                    blockLoc.getWorld().spawn(blockLoc, ExperienceOrb.class).setExperience(event.getExpToDrop());

                    block.setType(Material.AIR);
                }
                case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> {
                    event.setCancelled(true);
                    if (player.hasPermission("jmc.donator.double-ores")) {
                        blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.EMERALD, block.getDrops().size()));
                    }

                    blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.EMERALD, block.getDrops().size()));
                    blockLoc.getWorld().spawn(blockLoc, ExperienceOrb.class).setExperience(event.getExpToDrop());

                    block.setType(Material.AIR);
                }
                case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> {
                    event.setCancelled(true);
                    if (player.hasPermission("jmc.donator.double-ores")) {
                        blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.DIAMOND, block.getDrops().size()));
                    }

                    blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.DIAMOND, block.getDrops().size()));
                    blockLoc.getWorld().spawn(blockLoc, ExperienceOrb.class).setExperience(event.getExpToDrop());

                    block.setType(Material.AIR);
                }
                case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE-> {
                    event.setCancelled(true);
                    if (player.hasPermission("jmc.donator.double-ores")) {
                        blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.REDSTONE, block.getDrops().size()));
                    }

                    blockLoc.getWorld().dropItemNaturally(blockLoc, new ItemStack(Material.REDSTONE, block.getDrops().size()));
                    blockLoc.getWorld().spawn(blockLoc, ExperienceOrb.class).setExperience(event.getExpToDrop());

                    block.setType(Material.AIR);
                }
            }
        }
    }

}
