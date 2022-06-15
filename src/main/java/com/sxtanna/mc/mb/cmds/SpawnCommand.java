package com.sxtanna.mc.mb.cmds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.combat.CombatAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@CommandAlias("spawn")
@CommandPermission("jmc.spawn")
public class SpawnCommand extends BaseCommand {

    @NotNull
    private final MobBorderPlugin plugin;
    private final CombatAPI api = new CombatAPI();

    @Contract(pure = true)
    public SpawnCommand(@NotNull final MobBorderPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void spawnCommand(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {

            World world = Bukkit.getWorld("lobby");
            double x = plugin.config.getDouble("lobby.spawn.x");
            double y = plugin.config.getDouble("lobby.spawn.y");
            double z = plugin.config.getDouble("lobby.spawn.z");
            if (world == null) {
                sender.sendMessage(format("&cNo spawn world has been setup yet, please contact an Administrator."));
                return;
            }

            Location loc = new Location(world, x, y, z);
            if (!api.isTagged(player)) {
                if (player.hasPermission("jmc.admin.spawnbypass")) {
                    player.teleport(loc);
                    player.sendMessage(format(plugin.config.getString("lobby.spawn.success")));
                } else {
                    startTimer(player, loc);
                }
            } else {
                player.sendMessage(format(plugin.config.getString("lobby.combat.still-tagged").replace("%remaining%", String.valueOf(api.remainingTagTimer(player)))));
            }
        }
    }

    public void startTimer(final Player player, final Location loc) {
        plugin.spawning.add(player.getUniqueId());
        new BukkitRunnable() {
            int time = plugin.config.getInt("lobby.spawn.delay") * 20;

            public void run() {
                if (!plugin.spawning.contains(player.getUniqueId())) {
                    player.sendMessage(format(plugin.config.getString("lobby.spawn.cancelled")));
                    this.cancel();
                    return;
                }
                if (time == 0) {
                    player.teleport(loc);
                    player.sendMessage(format(plugin.config.getString("lobby.spawn.success")));
                    this.cancel();
                    return;
                }
                player.sendMessage(format(plugin.config.getString("lobby.spawn.teleporting-in").replace("%remaining%", String.valueOf(time / 20))));
                time -= 20;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private String format(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
