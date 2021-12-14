package com.sxtanna.mc.mb;

import com.sxtanna.mc.mb.conf.Config;
import com.sxtanna.mc.mb.data.MobBorderEntity;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class MobBorderPlugin extends JavaPlugin implements Listener {

    @Nullable
    private static MobBorderPlugin INSTANCE;

    @Contract(pure = true)
    public static @NotNull MobBorderPlugin get() {
        return Objects.requireNonNull(INSTANCE, "plugin not initialized");
    }


    @NotNull
    private final NamespacedKey namespace     = new NamespacedKey(this, "border-entity");
    @NotNull
    private final Config        configuration = new Config(getDataFolder().toPath().resolve("config.yml"));


    @Nullable
    private MobBorderEntity entity;


    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(((Listener) this));

        INSTANCE = null;
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(@NotNull final EntityMoveEvent event) {
        final var entity = this.entity;
        if (entity == null || !entity.uuid().equals(event.getEntity().getUniqueId()) || !event.hasExplicitlyChangedBlock()) {
            return;
        }

        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            event.setCancelled(true); // don't do this kids
            return;
        }

        event.getEntity().getWorld().getWorldBorder().setCenter(event.getTo());
    }


    @Contract(pure = true)
    public @NotNull Config getConfiguration() {
        return this.configuration;
    }


    @Contract(pure = true)
    public @NotNull Optional<MobBorderEntity> getEntity() {
        return Optional.ofNullable(this.entity);
    }

}
