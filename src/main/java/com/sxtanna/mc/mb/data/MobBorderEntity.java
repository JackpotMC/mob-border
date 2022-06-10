package com.sxtanna.mc.mb.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public record MobBorderEntity(@NotNull UUID uuid) {

    @Contract(pure = true)
    public @NotNull Optional<Entity> live() {
        return Optional.of(this.uuid()).map(Bukkit::getEntity);
    }

    public @NotNull Location location() {
        return Bukkit.getEntity(uuid).getLocation();
    }

}
