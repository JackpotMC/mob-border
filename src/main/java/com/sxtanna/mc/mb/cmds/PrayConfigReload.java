package com.sxtanna.mc.mb.cmds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.sxtanna.mc.mb.MobBorderPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@CommandAlias("pcreload|prayconfigreload|prayconfigurationreload|prayconfreload")
@CommandPermission("jmc.prayconfigreload")
public final class PrayConfigReload extends BaseCommand {
    @NotNull
    private final MobBorderPlugin plugin;

    @Contract(pure = true)
    public PrayConfigReload(@NotNull final MobBorderPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void backpackCommand(@NotNull final CommandSender sender) throws IOException, InvalidConfigurationException {
        plugin.config.reload();
        plugin.cachedYLevel = plugin.config.getInt("lobby.drop-below-y");
        plugin.cachedAcidRainDamageAmount = plugin.config.getDouble("events.acid-rain.damage-amount");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aReloaded Pray's configuration file"));
    }

}
