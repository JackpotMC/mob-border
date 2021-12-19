package com.sxtanna.mc.mb.hook;

import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.data.MobBorderEntity;
import me.hexedhero.pp.api.PinataSpawnEvent;
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
    }

}
