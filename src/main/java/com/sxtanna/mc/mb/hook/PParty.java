package com.sxtanna.mc.mb.hook;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.data.MobBorderEntity;
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
        final var pinata = event.getPinata().getEntity();
        if (pinata == null) {
            return;
        }

        final var entity = this.plugin.getEntity()
                .flatMap(MobBorderEntity::live)
                .orElse(null);
        if (entity == null) {
            return;
        }

        pinata.teleport(entity);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ColorUtil.getHex("&#65ff00A&#6aff09C&#70ff12I&#75ff1bD &#7bff25R&#80ff2eA&#86ff37I&#8bff40N"), null, 10, 60, 10);
        }
    }

}
