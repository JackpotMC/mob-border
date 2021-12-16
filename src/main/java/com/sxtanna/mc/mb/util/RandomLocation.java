package com.sxtanna.mc.mb.util;

import com.sxtanna.mc.mb.MobBorderPlugin;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Math.sqrt;

public final class RandomLocation {

    private static final long MAX_ATTEMPTS = 100L;


    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull RandomLocation of(@NotNull final MobBorderPlugin plugin, @NotNull final Location origin, final int radius) {
        return new RandomLocation(plugin, origin, radius);
    }


    @NotNull
    private final MobBorderPlugin plugin;

    private final int      radius;
    @NotNull
    private final Location origin;

    @NotNull
    private final AtomicLong attempts = new AtomicLong();
    @NotNull
    private final LongSet    previous = new LongOpenHashSet();

    @Nullable
    private CompletableFuture<Location> future;


    @Contract(pure = true)
    private RandomLocation(@NotNull final MobBorderPlugin plugin, @NotNull final Location origin, final int radius) {
        this.plugin = plugin;
        this.radius = radius;
        this.origin = origin;
    }


    public @NotNull CompletableFuture<Location> find() {
        if (this.future != null) {
            return this.future;
        }

        return this.future = this.radius <= 0 ?
                             CompletableFuture.completedFuture(this.origin) :
                             CompletableFuture.supplyAsync(this::poll).whenComplete(($0, $1) -> this.previous.clear());
    }


    private @Nullable Location poll() {
        if (attempts.incrementAndGet() > MAX_ATTEMPTS) {
            return null;
        }

        final var random = generate(this.origin, this.radius);

        if (this.previous.add(toChunkKey(random))) {

            final var passes = new CompletableFuture<Boolean>();

            this.plugin.getServer()
                       .getScheduler()
                       .runTask(this.plugin, () ->
                       {
                           random.setY(random.getWorld().getHighestBlockYAt(random));

                           passes.complete(random.getWorld().getWorldBorder().isInside(random) &&
                                           random.getBlock().getRelative(BlockFace.DOWN).getType().isSolid());
                       });

            if (passes.join()) {

                random.setX(random.getBlockX() + 0.5);
                random.setY(random.getBlockY() + 1.1);
                random.setZ(random.getBlockZ() + 0.5);

                return random;
            }
        }

        return poll();
    }


    private static long toChunkKey(@NotNull Location location) {
        final var x = (long) location.getBlockX() << 4;
        final var z = (long) location.getBlockZ() << 4;

        return (x & 0xffffffffL) | (z & 0xffffffffL) << 32;
    }

    private static @NotNull Location generate(@NotNull final Location origin, final double radius) {
        final var t = Math.random() * 2.0 * Math.PI;
        final var r = (radius - 1.0) * sqrt(Math.random());

        return new Location(origin.getWorld(),
                            origin.getX() + r * Math.cos(t),
                            0.0,
                            origin.getZ() + r * Math.sin(t));
    }

}
