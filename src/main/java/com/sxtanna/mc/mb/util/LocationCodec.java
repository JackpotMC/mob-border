package com.sxtanna.mc.mb.util;

import com.google.common.primitives.Doubles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public enum LocationCodec {
    ;

    @NotNull
    private static final Pattern PATTERN = Pattern.compile("^(?<world>[^:]+):(?<x>([^:])+):(?<y>[^:]+):(?<z>[^:]+)$");


    public static @NotNull String encode(@NotNull final Location loc) {
        return String.format("%s:%f:%f:%f", loc.getWorld().getUID(), loc.getX(), loc.getY(), loc.getZ());
    }


    @SuppressWarnings("UnstableApiUsage")
    public static @NotNull Optional<Location> decode(@NotNull final String str) {
        final var matcher = PATTERN.matcher(str);

        if (!matcher.matches()) {
            return Optional.empty();
        }

        final var w = matcher.group("world");
        final var x = Doubles.tryParse(matcher.group("x"));
        final var y = Doubles.tryParse(matcher.group("y"));
        final var z = Doubles.tryParse(matcher.group("z"));

        if (w == null || x == null || y == null || z == null) {
            return Optional.empty();
        }

        World world;

        try {
            world = Bukkit.getWorld(w);

            if (world == null) {
                world = Bukkit.getWorld(UUID.fromString(w));
            }
        } catch (final IllegalArgumentException ignored) {
            return Optional.empty();
        }

        if (world == null) {
            return Optional.empty();
        }

        return Optional.of(new Location(world, x, y, z));
    }

}
