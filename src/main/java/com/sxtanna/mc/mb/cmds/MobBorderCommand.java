package com.sxtanna.mc.mb.cmds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.conf.sections.BorderSettings;
import com.sxtanna.mc.mb.conf.sections.EntitySettings;
import com.sxtanna.mc.mb.data.MobBorderEntity;
import com.sxtanna.mc.mb.util.BukkitSerialization;
import com.sxtanna.mc.mb.util.LocationCodec;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

@CommandAlias("mobborder|mborder|mb")
@CommandPermission("jmc.mobborder")
public final class MobBorderCommand extends BaseCommand {

    @NotNull
    private final MobBorderPlugin plugin;


    @Contract(pure = true)
    public MobBorderCommand(@NotNull final MobBorderPlugin plugin) {
        this.plugin = plugin;
    }


    @Default
    @HelpCommand
    public void help(@NotNull final CommandHelp help) {
        help.showHelp();
    }


    @Subcommand("create")
    @CommandPermission("jmc.mobborder.create")
    public void create(@NotNull final CommandSender sender, @NotNull final World world) {
        final var origin = sender instanceof Player player && world.equals(player.getWorld()) ?
                player.getLocation() :
                world.getSpawnLocation();

        this.plugin.getConfiguration().setProperty(BorderSettings.BORDER_ORIGIN, LocationCodec.encode(origin));
        this.plugin.getConfiguration().save();

        this.plugin.killMobBorderEntity();
        this.plugin.loadMobBorderEntity();
    }

    @Subcommand("remove")
    @CommandPermission("jmc.mobborder.remove")
    public void remove(@NotNull final CommandSender sender) {
        this.plugin.getConfiguration().setProperty(BorderSettings.BORDER_ORIGIN, "");
        this.plugin.getConfiguration().save();

        this.plugin.saveMobBorderValues();
        this.plugin.killMobBorderEntity();
    }


    @Subcommand("resize")
    @CommandPermission("jmc.mobborder.resize")
    @CommandCompletion("250")
    public void resize(@NotNull final CommandSender sender, @Default("250") @Flags("min=16,max=10000") final int size) {
        this.plugin.getConfiguration().setProperty(BorderSettings.BORDER_SIZE, ((double) size));
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                .flatMap(MobBorderEntity::live)
                .ifPresent(this.plugin::updateWorldBorders);
    }

    @Subcommand("damage")
    @CommandPermission("jmc.mobborder.damage")
    @CommandCompletion("0.2")
    public void damage(@NotNull final CommandSender sender, @Default("0.2") @Flags("min=0.0,max=20.0") final double damage) {
        this.plugin.getConfiguration().setProperty(BorderSettings.BORDER_HURT, damage);
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                .flatMap(MobBorderEntity::live)
                .ifPresent(this.plugin::updateWorldBorders);
    }

    @Subcommand("scaling")
    @CommandPermission("jmc.mobborder.scaling")
    @CommandCompletion("true|false")
    public void scaling(@NotNull final CommandSender sender, @Default("false") final boolean scaling) {
        this.plugin.getConfiguration().setProperty(BorderSettings.BORDER_SCALING, scaling);
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                .flatMap(MobBorderEntity::live)
                .ifPresent(this.plugin::updateWorldBorders);
    }


    @Subcommand("entity rename")
    @CommandPermission("jmc.mobborder.entity.rename")
    public void rename(@NotNull final CommandSender sender, @NotNull final String name) {
        this.plugin.getConfiguration().setProperty(EntitySettings.ENTITY_NAME, name);
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                .flatMap(MobBorderEntity::live)
                .ifPresent(this.plugin::updateEntityValues);
    }

    @Subcommand("entity silent")
    @CommandPermission("jmc.mobborder.entity.silent")
    @CommandCompletion("true|false")
    public void silent(@NotNull final CommandSender sender, @Default("false") final boolean silent) {
        this.plugin.getConfiguration().setProperty(EntitySettings.ENTITY_SILENT, silent);
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                .flatMap(MobBorderEntity::live)
                .ifPresent(this.plugin::updateEntityValues);
    }

    @Subcommand("entity glowing")
    @CommandPermission("jmc.mobborder.entity.glowing")
    @CommandCompletion("true|false")
    public void glowing(@NotNull final CommandSender sender, @Default("true") final boolean glowing) {
        this.plugin.getConfiguration().setProperty(EntitySettings.ENTITY_GLOWING, glowing);
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                .flatMap(MobBorderEntity::live)
                .ifPresent(this.plugin::updateEntityValues);
    }

    @Subcommand("entity speed")
    @CommandPermission("jmc.mobborder.entity.speed")
    @CommandCompletion("0.25")
    public void speed(@NotNull final CommandSender sender, @Default("0.25") @Flags("min=0.0,max=1024.0") final double speed) {
        this.plugin.getConfiguration().setProperty(EntitySettings.ENTITY_SPEED, speed);
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                .flatMap(MobBorderEntity::live)
                .ifPresent(this.plugin::updateEntityValues);
    }

    @Subcommand("entity freeze")
    @CommandPermission("jmc.mobborder.entity.freeze")
    @CommandCompletion("true|false")
    public void freeze(@NotNull final CommandSender sender, @Default("false") final boolean frozen) {
        this.plugin.getConfiguration().setProperty(EntitySettings.ENTITY_FROZEN, frozen);
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                .flatMap(MobBorderEntity::live)
                .ifPresent(this.plugin::updateEntityValues);
    }


    @Subcommand("entity respawns")
    @CommandPermission("jmc.mobborder.entity.respawns")
    @CommandCompletion("true|false")
    public void respawns(@NotNull final CommandSender sender, @Default("false") final boolean respawns) {
        this.plugin.getConfiguration().setProperty(EntitySettings.ENTITY_RESPAWNS, respawns);
        this.plugin.getConfiguration().save();
    }

    @Subcommand("events luckyblock add")
    @CommandPermission("jmc.mobborder.events.luckyblocks")
    @CommandCompletion("10")
    public void luckyblocksAddItem(@NotNull final CommandSender sender, @Default("10") @Flags("min=1,max=100") final int chance) throws IOException, InvalidConfigurationException {
        List<String> items = plugin.config.getStringList("events.luckyblocks.possible-items");

        if (sender instanceof Player player) {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.sendMessage(format("&cYou can't add air to the list of rewards!"));
                return;
            }
            items.add(BukkitSerialization.itemStackToBase64(player.getInventory().getItemInMainHand()) + " 1 " + chance);
            plugin.config.set("events.luckyblocks.possible-items", items);
            plugin.config.save();
            player.sendMessage(format("&aSuccessfully added your item to the list of possible rewards with the chance of: &l" + chance + "%"));
        } else {
            sender.sendMessage(format("&cYou are not a player."));
        }
    }

    @Subcommand("events airdrops add")
    @CommandPermission("jmc.mobborder.events.airdrops")
    @CommandCompletion("10")
    public void airdropsAddItem(@NotNull final CommandSender sender, @Default("10") @Flags("min=1,max=100") final int chance) throws IOException, InvalidConfigurationException {
        List<String> items = plugin.config.getStringList("events.airdrops.possible-items");

        if (sender instanceof Player player) {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.sendMessage(format("&cYou can't add air to the list of rewards!"));
                return;
            }
            items.add(BukkitSerialization.itemStackToBase64(player.getInventory().getItemInMainHand()) + " 1 " + chance);
            plugin.config.set("events.airdrops.possible-items", items);
            plugin.config.save();
            player.sendMessage(format("&aSuccessfully added your item to the list of possible rewards with the chance of: &l" + chance + "%"));
        } else {
            sender.sendMessage(format("&cYou are not a player."));
        }
    }

    @Subcommand("events lootrain add")
    @CommandPermission("jmc.mobborder.events.lootrain")
    @CommandCompletion("10")
    public void addItem(@NotNull final CommandSender sender, @Default("10") @Flags("min=1,max=100") final int chance) throws IOException, InvalidConfigurationException {
        List<String> items = plugin.config.getStringList("events.loot-rain.possible-items");

        if (sender instanceof Player player) {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.sendMessage(format("&cYou can't add air to the list of rewards!"));
                return;
            }
            items.add(BukkitSerialization.itemStackToBase64(player.getInventory().getItemInMainHand()) + " 1 " + chance);
            plugin.config.set("events.loot-rain.possible-items", items);
            plugin.config.save();
            player.sendMessage(format("&aSuccessfully added your item to the list of possible rewards with the chance of: &l" + chance + "%"));
        } else {
            sender.sendMessage(format("&cYou are not a player."));
        }
    }


    @Subcommand("reload")
    @CommandPermission("jmc.mobborder.reload")
    public void reload(@NotNull final CommandSender sender) {
        this.plugin.reload();
    }


    private String format(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
