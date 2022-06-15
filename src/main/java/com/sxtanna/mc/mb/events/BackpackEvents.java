package com.sxtanna.mc.mb.events;

import com.mongodb.client.model.Updates;
import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.util.BukkitSerialization;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

public class BackpackEvents implements Listener {

    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    @EventHandler
    public void onBackpackClose(InventoryCloseEvent event) {
        if (ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase(plugin.config.getString("backpack.inventory.name"))) {
            Bson query = eq("UUID", event.getPlayer().getUniqueId().toString());
            Document first = plugin.backpacksCollection.find(query).first();
            Bson backpackInfo = Updates.set("BackpackItems", BukkitSerialization.itemStackArrayToBase64(event.getInventory().getStorageContents()));
            plugin.backpacksCollection.findOneAndUpdate(first, backpackInfo);
        }
    }

    @EventHandler
    public void onBackpackDrop(PlayerDropItemEvent event) {

        if (isBackpack(event.getItemDrop().getItemStack())) {
            if (!plugin.config.getBoolean("backpack.drop.enabled")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(format(plugin.config.getString("backpack.drop.disabled-message")));
            } else {
                event.getItemDrop().remove();
                event.getPlayer().sendMessage(format(plugin.config.getString("backpack.drop.deleted-message")));
            }
        }

    }

    @EventHandler
    public void onBackpackPlace(BlockPlaceEvent event) {
        if (isBackpack(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) throws IOException {
        if (isBackpack(event.getPlayer().getInventory().getItemInMainHand()) || isBackpack(event.getPlayer().getInventory().getItemInOffHand())) {
            event.setCancelled(true);

            Inventory backpack;
            Player player = event.getPlayer();
            Bson query = eq("UUID", player.getUniqueId().toString());
            Document first = plugin.backpacksCollection.find(query).first();

            if (player.hasPermission("jmc.backpack.donator")) {
                backpack = Bukkit.createInventory(null, 54, format(plugin.config.getString("backpack.inventory.name")));
            } else {
                backpack = Bukkit.createInventory(null, 27, format(plugin.config.getString("backpack.inventory.name")));
            }

            if (!first.getString("BackpackItems").equals("nothing")) {
                ItemStack[] items = BukkitSerialization.itemStackArrayFromBase64(first.getString("BackpackItems"));
                if (items.length > backpack.getSize()) {
                    ItemStack[] modifiedArray = new ItemStack[backpack.getSize()];
                    if (backpack.getSize() >= 0) System.arraycopy(items, 0, modifiedArray, 0, backpack.getSize());
                    backpack.setContents(modifiedArray);
                } else {
                    backpack.setContents(items);
                }
            }

            player.openInventory(backpack);
        }
    }

    public boolean isBackpack(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return false;

        try {
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "backpack"), PersistentDataType.STRING)) {
                return true;
            }
        } catch (NullPointerException ignored) {
            return false;
        }

        return false;
    }

    private String format(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
