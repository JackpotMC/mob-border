package com.sxtanna.mc.mb;

import com.sxtanna.mc.mb.conf.Config;
import com.sxtanna.mc.mb.conf.sections.BorderSettings;
import com.sxtanna.mc.mb.conf.sections.EntitySettings;
import com.sxtanna.mc.mb.data.MobBorderEntity;
import com.sxtanna.mc.mb.util.LocationCodec;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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

        findMobBorderEntity();
        loadMobBorderEntity();

    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(((Listener) this));

        killMobBorderEntity();

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


    public void findMobBorderEntity() {
        final var uuid = getConfiguration().get(EntitySettings.ENTITY_UUID);
        if (uuid.isBlank()) {
            return;
        }

        try {
            this.entity = new MobBorderEntity(UUID.fromString(uuid));
        } catch (final IllegalArgumentException ignored) {
            // nothing
        }
    }

    public void loadMobBorderEntity() {
        killMobBorderEntity();

        final var origin = LocationCodec.decode(getConfiguration().get(BorderSettings.BORDER_ORIGIN))
                                        .orElse(null);

        if (origin == null) {
            return;
        }

        final var entity = origin.getWorld().spawnEntity(origin,
                                                         getConfiguration().get(EntitySettings.ENTITY_TYPE));

        entity.getPersistentDataContainer()
              .set(namespace, PersistentDataType.PrimitivePersistentDataType.BYTE, ((byte) 1));


        final var name = getConfiguration().get(EntitySettings.ENTITY_NAME);

        if (name.isBlank()) {
            entity.setCustomNameVisible(false);
            entity.customName(Component.empty());
        } else {
            entity.setCustomNameVisible(true);
            entity.customName(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
        }

        entity.setInvulnerable(true);
        entity.setSilent(getConfiguration().get(EntitySettings.ENTITY_SILENT));

        final var border = entity.getWorld().getWorldBorder();
        border.setCenter(origin);

        updateWorldBorderValues(border);

        this.entity = new MobBorderEntity(entity.getUniqueId());

        getConfiguration().setProperty(EntitySettings.ENTITY_UUID, entity.getUniqueId().toString());
        getConfiguration().save();
    }

    public void killMobBorderEntity() {
        getEntity().flatMap(MobBorderEntity::live)
                   .ifPresent(entity -> {

                       entity.getWorld().getWorldBorder().reset();
                       entity.remove();

                   });

        this.entity = null;

        getConfiguration().setProperty(EntitySettings.ENTITY_UUID, "");
        getConfiguration().save();
    }


    public void updateWorldBorderValues(@NotNull final WorldBorder border) {
        border.setSize(getConfiguration().get(BorderSettings.BORDER_SIZE));
        border.setDamageBuffer(getConfiguration().get(BorderSettings.BORDER_DIST_HURT));
        border.setWarningDistance(getConfiguration().get(BorderSettings.BORDER_DIST_WARN));
    }

}
