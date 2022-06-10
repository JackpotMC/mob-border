package com.sxtanna.mc.mb.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

import java.util.Set;

public class PreEnchant implements Listener {

    @EventHandler
    public void onPreEnchantTest(PrepareItemEnchantEvent e) {

        Player player = e.getEnchanter();

        Block targetBlock = player.getTargetBlock((Set<Material>) null, 5);

        if (targetBlock.getType() == Material.ENCHANTING_TABLE) return;

        EnchantmentOffer[] offers = e.getOffers();
        offers[0].setCost(0);
        offers[1].setCost(0);
        offers[2].setCost(0);

        offers[0].setEnchantmentLevel(1);
        offers[1].setEnchantmentLevel(15);
        offers[2].setEnchantmentLevel(30);
    }

}
