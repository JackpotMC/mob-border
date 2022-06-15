package com.sxtanna.mc.mb.cmds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.util.BukkitSerialization;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@CommandAlias("backpack")
@CommandPermission("jmc.backpack")
public final class Backpack extends BaseCommand {
    @NotNull
    private final MobBorderPlugin plugin;

    @Contract(pure = true)
    public Backpack(@NotNull final MobBorderPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void backpackCommand(@NotNull final CommandSender sender) throws IOException {
        if (sender instanceof Player player) {
            Inventory backpack;
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

    @Subcommand("give")
    @CommandPermission("jmc.backpack.give")
    public void give(@NotNull final CommandSender sender) throws IOException {
        if (sender instanceof Player player) {
            if (getBackpack(player) != null) {
                player.sendMessage(format(plugin.config.getString("backpack.error-messages.already-have-backpack")));
            } else {
                player.getInventory().addItem(getBackpackItem());
                player.sendMessage(format(plugin.config.getString("backpack.messages.give")));
            }
        }
    }

    @Subcommand("remove")
    @CommandPermission("jmc.backpack.remove")
    public void remove(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            if (getBackpack(player) == null) {
                player.sendMessage(format(plugin.config.getString("backpack.error-messages.dont-have-backpack")));
            } else {
                player.getInventory().remove(getBackpack(player));
                player.sendMessage(format(plugin.config.getString("backpack.messages.remove")));
            }
        }
    }

    @Subcommand("setitem")
    @CommandPermission("jmc.admin.setbackpack")
    public void setItem(CommandSender sender) throws IOException {
        if (sender instanceof Player player) {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.sendMessage(format("&cYou are not holding an item!"));
                return;
            }

            ItemStack newBackpack = player.getInventory().getItemInMainHand();
            ItemMeta newBackpackMeta = newBackpack.getItemMeta();
            newBackpackMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "backpack"), PersistentDataType.STRING, "true");
            newBackpack.setItemMeta(newBackpackMeta);
            String finalItem = BukkitSerialization.itemStackToBase64(newBackpack);
            plugin.config.set("backpack.item.data", finalItem);
            plugin.config.save();
            player.sendMessage(format("&aSuccessfully set the backpack item to the item in your hand!"));

        }
    }


    public ItemStack getBackpackItem() throws IOException {
        ItemStack backpack = BukkitSerialization.itemStackFromBase64(plugin.config.getString("backpack.item.data"));
        ItemMeta backpackMeta = backpack.getItemMeta();
        if (!backpackMeta.getPersistentDataContainer().has(new NamespacedKey(plugin, "backpack"))) {
            backpackMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "backpack"), PersistentDataType.STRING, "true");
        }
        backpack.setItemMeta(backpackMeta);


        return backpack;
    }

    public ItemStack getBackpack(Player player) {

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getItemMeta() == null) continue;

            try {
                if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "backpack"), PersistentDataType.STRING)) {
                    return item;
                }
            } catch (NullPointerException npe) {
                continue;
            }
        }

        return null;
    }

    private String format(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public String format(List<String> string) {
        String s = "";

        for (String value : string) {
            String st = value;

            if (st.isEmpty()) {
                s += " " + "\n\n";
                continue;
            }

            s += st.replace("&", "§");
            s += " " + "\n";

        }
        return s;
    }
}
