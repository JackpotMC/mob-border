package com.sxtanna.mc.mb;

import co.aikar.commands.PaperCommandManager;
import com.sxtanna.mc.mb.cmds.MobBorderCommand;
import com.sxtanna.mc.mb.conf.Config;
import com.sxtanna.mc.mb.conf.sections.BorderSettings;
import com.sxtanna.mc.mb.conf.sections.ChangeSettings;
import com.sxtanna.mc.mb.conf.sections.EntitySettings;
import com.sxtanna.mc.mb.data.BlockDropChange;
import com.sxtanna.mc.mb.data.MobBorderEntity;
import com.sxtanna.mc.mb.util.LocationCodec;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    private       MobBorderEntity       entity;
    @NotNull
    private final List<BlockDropChange> changes = new ArrayList<>();


    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        findMobBorderEntity();
        loadMobBorderEntity();


        this.changes.addAll(getConfiguration().get(ChangeSettings.CHANGES).values());


        final var manager = new PaperCommandManager(this);

        manager.enableUnstableAPI("help");
        manager.enableUnstableAPI("brigadier");
        manager.usePerIssuerLocale(true, true);

        manager.registerCommand(new MobBorderCommand(this));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(((Listener) this));

        killMobBorderEntity();

        INSTANCE = null;
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityMove(@NotNull final EntityMoveEvent event) {
        final var entity = this.entity;
        if (entity == null || !entity.uuid().equals(event.getEntity().getUniqueId()) || !event.hasExplicitlyChangedBlock()) {
            return;
        }

        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            event.setCancelled(true); // don't do this kids
            return;
        }

        updateWorldOrigins(event.getEntity().getWorld(), event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(@NotNull final PlayerMoveEvent event) {
        final var entity  = this.entity;
        final var vehicle = event.getPlayer().getVehicle();
        if (vehicle == null || entity == null || !entity.uuid().equals(vehicle.getUniqueId()) || !event.hasExplicitlyChangedBlock()) {
            return;
        }

        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            event.setCancelled(true); // don't do this kids
            return;
        }

        updateWorldOrigins(vehicle.getWorld(), event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPortalUsed(@NotNull final EntityPortalEvent event) {
        final var entity = this.entity;
        if (entity != null && entity.uuid().equals(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityHurt(@NotNull final EntityDamageEvent event) {
        final var entity = this.entity;
        if (entity != null && entity.uuid().equals(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
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

        final var entity = origin.getWorld().spawnEntity(origin, getConfiguration().get(EntitySettings.ENTITY_TYPE));
        updateEntityValues(entity);
        updateWorldBorders(entity);

        this.entity = new MobBorderEntity(entity.getUniqueId());

        getConfiguration().setProperty(EntitySettings.ENTITY_UUID, entity.getUniqueId().toString());
        getConfiguration().save();
    }

    public void killMobBorderEntity() {
        getEntity().flatMap(MobBorderEntity::live)
                   .ifPresent(entity -> {

                       entity.getWorld().getWorldBorder().reset();

                       Optional.of(entity.getWorld().getName() + "_nether")
                               .map(Bukkit::getWorld)
                               .map(World::getWorldBorder)
                               .ifPresent(WorldBorder::reset);

                       Optional.of(entity.getWorld().getName() + "_the_end")
                               .map(Bukkit::getWorld)
                               .map(World::getWorldBorder)
                               .ifPresent(WorldBorder::reset);

                       entity.remove();

                   });

        this.entity = null;

        getConfiguration().setProperty(EntitySettings.ENTITY_UUID, "");
        getConfiguration().save();
    }


    public void updateEntityValues(@NotNull final Entity entity) {
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

        entity.setGlowing(getConfiguration().get(EntitySettings.ENTITY_GLOWING));

        if (entity instanceof LivingEntity living) {
            final var speed = living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (speed != null) {
                speed.setBaseValue(getConfiguration().get(EntitySettings.ENTITY_SPEED));
            }
        }
    }

    public void updateWorldBorders(@NotNull final Entity entity) {
        final var overworld = entity.getWorld().getWorldBorder();
        overworld.setCenter(entity.getLocation());


        final var nether = Optional.of(entity.getWorld().getName() + "_nether")
                                   .map(Bukkit::getWorld)
                                   .map(World::getWorldBorder)
                                   .orElse(null);

        if (nether != null) {
            nether.setCenter(entity.getLocation());
        }

        final var theend = Optional.of(entity.getWorld().getName() + "_the_end")
                                   .map(Bukkit::getWorld)
                                   .map(World::getWorldBorder)
                                   .orElse(null);

        if (theend != null) {
            theend.setCenter(entity.getLocation());
        }


        final var size = getConfiguration().get(BorderSettings.BORDER_SIZE);
        final var hurt = getConfiguration().get(BorderSettings.BORDER_DIST_HURT);
        final var warn = getConfiguration().get(BorderSettings.BORDER_DIST_WARN);

        overworld.setSize(size);
        overworld.setDamageBuffer(hurt);
        overworld.setWarningDistance(warn);


        if (theend != null) { // I'm... not sure if there is a scale on this.. ?
            theend.setSize(size);
            theend.setDamageBuffer(hurt);
            theend.setWarningDistance(warn);
        }

        if (nether != null) {
            final var scaledSize = size < 16 || !getConfiguration().get(BorderSettings.BORDER_SCALING) ?
                                   size :
                                   size / 8;

            nether.setSize(scaledSize);
            nether.setDamageBuffer(hurt);
            nether.setWarningDistance(warn);
        }
    }

    public void updateWorldOrigins(@NotNull final World world, @NotNull final Location origin) {
        world.getWorldBorder().setCenter(origin);

        Optional.of(world.getName() + "_nether")
                .map(Bukkit::getWorld)
                .map(World::getWorldBorder)
                .ifPresent(border -> border.setCenter(origin));

        Optional.of(world.getName() + "_the_end")
                .map(Bukkit::getWorld)
                .map(World::getWorldBorder)
                .ifPresent(border -> border.setCenter(origin));
    }

}
