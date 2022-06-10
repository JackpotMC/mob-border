package com.sxtanna.mc.mb.cmds.stats;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.sxtanna.mc.mb.MobBorderPlugin;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

@CommandAlias("stats|statistics")
@CommandPermission("jmc.player.stats")
public final class Stats extends BaseCommand {
    @NotNull
    private final MobBorderPlugin plugin;

    @Contract(pure = true)
    public Stats(@NotNull final MobBorderPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void statsCommand(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            player.sendMessage(format(plugin.config.getStringList("stats.message"), player));
        }
    }

    public String formatMessage(String message, long[] time, Player player) {

        Bson query = eq("UUID", player.getUniqueId().toString());
        Document first = plugin.playerCollection.find(query).first();

        return ChatColor.translateAlternateColorCodes('&',
                message.replace("%d%", time[0] + "")
                        .replace("%h%", time[1] + "")
                        .replace("%m%", time[2] + "")
                        .replace("%s%", time[3] + "")
                        .replace("%player%", player.getName())
                        .replace("%kills%", String.valueOf(first.getInteger("Kills")))
                        .replace("%deaths%", String.valueOf(first.getInteger("Deaths")))
                        .replace("%kd%", first.getString("KD"))
                        .replace("%ores_mined%", String.valueOf(first.getInteger("OresMined"))));
    }


    public long[] formatDuration(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return new long[]{days, hours, minutes, seconds};
    }

    public String format(List<String> string, Player player) {
        String s = "";
        Bson query = eq("UUID", player.getUniqueId().toString());

        Document first = plugin.playerCollection.find(query).first();
        Long pt = first.getLong("Playtime");
        Long jt = first.getLong("JoinTime");
        Long finalPlaytime = (System.currentTimeMillis() - jt) + pt;

        for (String value : string) {
            String st = formatMessage(value, formatDuration(finalPlaytime), player);

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