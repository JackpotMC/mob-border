package com.sxtanna.mc.mb.util;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.combat.CombatAPI;
import com.sxtanna.mc.mb.events.stats.StatsEvents;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpigotExpansion extends PlaceholderExpansion {
    private final CombatAPI api = new CombatAPI();
    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;
    private final StatsEvents stats = new StatsEvents();

    @NotNull
    public String getIdentifier() {
        return "bordersmp";
    }

    @NotNull
    public String getAuthor() {
        return "Pray";
    }

    @NotNull
    public String getVersion() {
        return "1.0";
    }

    public boolean canRegister() {
        return true;
    }

    public boolean persist() {
        return true;
    }

    @Nullable
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        if (params.equals("combat_tag")) return String.valueOf(this.api.remainingTagTimer(player));
        if(params.equals("ores_mined")) return stats.getOresMined(player);
        if(params.equals("kills")) return stats.getKills(player);
        if(params.equals("deaths")) return stats.getDeaths(player);
        if(params.equals("kd")) return stats.getKd(player);

        return onPlaceholderRequest(player, params);
    }

}
