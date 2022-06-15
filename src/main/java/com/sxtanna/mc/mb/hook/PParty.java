package com.sxtanna.mc.mb.hook;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.util.ColorUtil;
import me.hexedhero.pp.api.PinataSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public record PParty(@NotNull MobBorderPlugin plugin) implements Listener {

    @EventHandler
    public void onSpawn(@NotNull final PinataSpawnEvent event) {


        event.getPinata().getEntity().teleport(plugin.getEntity().get().location());
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ColorUtil.getHex("#ab0101P#c8581ei#e5b13bn#f3ff51a#96ff32t#3aff14a#00de16 #00894fP#003388a#1d00aar#6300aat#aa00aay"), null, 10, 60, 10);
        }
    }

}
