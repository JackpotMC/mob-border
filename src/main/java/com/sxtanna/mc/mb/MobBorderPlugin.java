package com.sxtanna.mc.mb;

import co.aikar.commands.PaperCommandManager;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sxtanna.mc.mb.cmds.Backpack;
import com.sxtanna.mc.mb.cmds.MobBorderCommand;
import com.sxtanna.mc.mb.cmds.PrayConfigReload;
import com.sxtanna.mc.mb.cmds.SpawnCommand;
import com.sxtanna.mc.mb.cmds.pray.donator.AnvilCommand;
import com.sxtanna.mc.mb.cmds.pray.donator.EnchantingTableCommand;
import com.sxtanna.mc.mb.cmds.pray.donator.SmithingTableCommand;
import com.sxtanna.mc.mb.cmds.stats.Stats;
import com.sxtanna.mc.mb.combat.CombatTagEvent;
import com.sxtanna.mc.mb.conf.Config;
import com.sxtanna.mc.mb.conf.sections.BorderSettings;
import com.sxtanna.mc.mb.conf.sections.ChangeSettings;
import com.sxtanna.mc.mb.conf.sections.EntitySettings;
import com.sxtanna.mc.mb.data.BlockDropChange;
import com.sxtanna.mc.mb.data.MobBorderEntity;
import com.sxtanna.mc.mb.events.AutoSmelt;
import com.sxtanna.mc.mb.events.BackpackEvents;
import com.sxtanna.mc.mb.events.LobbyEvents;
import com.sxtanna.mc.mb.events.stats.StatsEvents;
import com.sxtanna.mc.mb.revents.EventManager;
import com.sxtanna.mc.mb.revents.events.LuckyBlock;
import com.sxtanna.mc.mb.util.FileHandler;
import com.sxtanna.mc.mb.util.LocationCodec;
import com.sxtanna.mc.mb.util.RandomLocation;
import com.sxtanna.mc.mb.util.SpigotExpansion;
import io.papermc.paper.event.entity.EntityMoveEvent;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public final class MobBorderPlugin extends JavaPlugin implements Listener {

    @Nullable
    public static MobBorderPlugin INSTANCE;

    @Contract(pure = true)
    public static @NotNull MobBorderPlugin get() {
        return Objects.requireNonNull(INSTANCE, "plugin not initialized");
    }


    @NotNull
    private static final LongSet IGNORED = new LongOpenHashSet();

    @NotNull
    private final NamespacedKey namespace = new NamespacedKey(this, "border-entity");
    @NotNull
    private final Config configuration = new Config(getDataFolder().toPath().resolve("config.yml"));


    @Nullable
    private MobBorderEntity entity;
    @NotNull
    private final List<BlockDropChange> changes = new ArrayList<>();

    @NotNull
    private final AtomicReference<BukkitTask> randomSpeedTask = new AtomicReference<>();

    @NotNull
    public ConcurrentHashMap<UUID, Long> combatLog = new ConcurrentHashMap<>();

    public FileHandler config;

    public MongoClient mongoClient;

    public MongoDatabase db;

    public MongoCollection<Document> getPlayerCollection() {
        return playerCollection;
    }

    public MongoCollection<Document> playerCollection;
    public MongoCollection<Document> backpacksCollection;

    public double cachedAcidRainDamageAmount;
    public double cachedYLevel;

    public ArrayList<UUID> spawning = new ArrayList<>();

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {


        try {
            this.config = new FileHandler(this, "pray-config.yml", true);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().log(Level.SEVERE, "failed to initialize config file", e);
        }

        this.cachedAcidRainDamageAmount = config.getDouble("events.acid-rain.damage-amount");
        this.cachedYLevel = config.getInt("lobby.drop-below-y");

        registerEvents();
        registerDatabase();
        findMobBorderEntity();
        loadMobBorderEntity();

        this.changes.addAll(getConfiguration().get(ChangeSettings.CHANGES).values());

        final var manager = new PaperCommandManager(this);

        manager.enableUnstableAPI("help");
        manager.enableUnstableAPI("brigadier");
        manager.usePerIssuerLocale(true, true);

        registerCommands(manager);

        try {
            if (getServer().getPluginManager().isPluginEnabled("PinataParty")) {
                getServer().getPluginManager().registerEvents(new com.sxtanna.mc.mb.hook.PParty(this), this);
            }
        } catch (final Throwable ex) {
            getLogger().log(Level.SEVERE, "failed to initialize PinataParty hook", ex);
        }
        new EventManager().startGlobalEvents();
        new SpigotExpansion().register();
    }

    public void registerCommands(PaperCommandManager manager) {
        manager.registerCommand(new MobBorderCommand(this));
        manager.registerCommand(new AnvilCommand(this));
        manager.registerCommand(new SmithingTableCommand(this));
        manager.registerCommand(new EnchantingTableCommand(this));
        manager.registerCommand(new Stats(this));
        manager.registerCommand(new Backpack(this));
        manager.registerCommand(new PrayConfigReload(this));
        manager.registerCommand(new SpawnCommand(this));
    }

    public void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(this, this);
//        this.getServer().getPluginManager().registerEvents(new PreEnchant(), this);
        pm.registerEvents(new AutoSmelt(), this);
        pm.registerEvents(new CombatTagEvent(), this);
        pm.registerEvents(new StatsEvents(), this);
        pm.registerEvents(new BackpackEvents(), this);
        pm.registerEvents(new LuckyBlock(), this);
        pm.registerEvents(new LobbyEvents(), this);
    }

    @Override
    public void onDisable() {

        try {
            config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HandlerList.unregisterAll(((Listener) this));

        saveMobBorderValues();
        killMobBorderEntity();

        INSTANCE = null;

        IGNORED.clear();
    }

    private void registerDatabase() {
        mongoClient = MongoClients.create(Objects.requireNonNull(config.getString("database.conn-string")));
        db = mongoClient.getDatabase(Objects.requireNonNull(config.getString("database.db-name")));
        playerCollection = db.getCollection("BorderPlayerInfo");
        backpacksCollection = db.getCollection("BackpacksInfo");
    }


    public void reload() {
        getConfiguration().reload();

        saveMobBorderValues();
        killMobBorderEntity();
        loadMobBorderEntity();

        IGNORED.clear();

        this.changes.clear();
        this.changes.addAll(getConfiguration().get(ChangeSettings.CHANGES).values());
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
        final var entity = this.entity;
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


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(@NotNull final BlockPlaceEvent event) {
        if (getConfiguration().get(ChangeSettings.IGNORE_BLOCKS_PLACED)) {

            if (event.getPlayer().getGameMode() == GameMode.CREATIVE && getConfiguration().get(ChangeSettings.ALLOW_CREATIVE_PLACED_BLOCKS)) {
                return;
            }

            IGNORED.add(event.getBlock().getBlockKey());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(@NotNull final BlockDropItemEvent event) {
        if (IGNORED.contains(event.getBlock().getBlockKey())) {
            return;
        }

        final var added = new ArrayList<ItemStack>();

        for (final var itemEntity : event.getItems()) {
            for (final var change : changes) {
                if (ThreadLocalRandom.current().nextDouble(0.0, 100.0) > Math.max(0.0, Math.min(100.0, change.getChance()))) {
                    continue;
                }

                final var prev = itemEntity.getItemStack();

                if (!change.applicable(prev)) {
                    continue;
                }

                final var drop = change.getDrops();

                switch (change.getMode()) {
                    case ADD -> {
                        final var next = drop.toBukkit();

                        if (next.getAmount() == -1) {
                            next.setAmount(itemEntity.getItemStack().getAmount());
                        }

                        added.add(next);
                    }
                    case SET -> {
                        prev.setType(drop.getType());

                        if (drop.getAmount() != -1) {
                            prev.setAmount(drop.getAmount());
                        }

                        itemEntity.setItemStack(prev);
                    }
                }

                break;
            }
        }

        added.stream()
                .map(item -> event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item))
                .forEach(event.getItems()::add);
    }


    public void teleportToWorld(@NotNull final Player player) {

        final var entity = getEntity().flatMap(MobBorderEntity::live).orElse(null);
        if (entity == null) {
            return;
        }

        final var border = entity.getWorld().getWorldBorder();

        player.setInvulnerable(true);

        RandomLocation.of(this, border.getCenter(), (int) (border.getSize() / 2))
                .find()
                .orTimeout(10L, TimeUnit.SECONDS)
                .whenComplete((location, throwable) -> getServer().getScheduler().runTask(this, () ->
                {
                    player.teleportAsync(location != null ? location : entity.getLocation());
                }));

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setInvulnerable(false);
            }
        }.runTaskLater(this, 20);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoinTeleportToLobby(PlayerJoinEvent event) {
        if (Bukkit.getWorld("lobby") == null) return;
        Location loc = new Location(Bukkit.getWorld("lobby"), config.getDouble("lobby.spawn.x"), config.getDouble("lobby.spawn.y"), config.getDouble("lobby.spawn.z"));
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().teleport(loc);
            }
        }.runTaskLater(this, 10);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(@NotNull final PlayerQuitEvent event) {
        final var entity = getEntity().flatMap(MobBorderEntity::live).orElse(null);
        if (entity == null) {
            return;
        }

        final var riding = event.getPlayer().getVehicle();
        if (riding == null || !riding.getUniqueId().equals(entity.getUniqueId())) {
            return;
        }

        entity.eject();

        event.getPlayer().leaveVehicle(); // just to make sure
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawning(@NotNull final PlayerRespawnEvent event) {
        if (event.getRespawnFlags().contains(PlayerRespawnEvent.RespawnFlag.END_PORTAL)) {
            return;
        }

        if (!getConfiguration().get(BorderSettings.RANDOMIZED_RESPAWNS)) {
            return;
        }

        final var entity = getEntity().flatMap(MobBorderEntity::live).orElse(null);
        if (entity == null) {
            return;
        }

        final var border = getConfiguration().get(BorderSettings.RESPAWN_WITH_ENTITY) ?
                entity.getWorld().getWorldBorder() :
                event.getPlayer().getWorld().getWorldBorder();

        RandomLocation.of(this, border.getCenter(), (int) (border.getSize() / 2))
                .findNow()
                .ifPresentOrElse(event::setRespawnLocation,
                        () -> event.setRespawnLocation(entity.getLocation()));
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityRide(@NotNull final VehicleEnterEvent event) {
        final var entity = this.entity;
        if (entity != null && entity.uuid().equals(event.getEntered().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityLead(@NotNull final PlayerLeashEntityEvent event) {
        final var entity = this.entity;
        if (entity != null && entity.uuid().equals(event.getEntity().getUniqueId())) {

            if (event.getPlayer().getGameMode() == GameMode.CREATIVE && getConfiguration().get(EntitySettings.ENTITY_ALLOW_CREATIVE_LEASHING)) {
                return;
            }

            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDead(@NotNull final EntityRemoveFromWorldEvent event) {
        if (!getConfiguration().get(EntitySettings.ENTITY_RESPAWNS)) {
            return;
        }

        final var entity = this.entity;
        if (entity != null && entity.uuid().equals(event.getEntity().getUniqueId())) {

            if (event.getEntity().hasMetadata("removing")) {
                return;
            }

            saveMobBorderValues(event.getEntity());

            getServer().getScheduler().runTaskLater(this, this::loadMobBorderEntity, 20L);
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
        killMobBorderEntity(false);
    }

    public void killMobBorderEntity(final boolean allowRespawning) {
        final var entity = getEntity().flatMap(MobBorderEntity::live)
                .orElse(null);

        if (entity != null) {
            if (!allowRespawning) {
                entity.setMetadata("removing", new FixedMetadataValue(this, 1));
            }

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
        }

        this.entity = null;

        getConfiguration().setProperty(EntitySettings.ENTITY_UUID, "");
        getConfiguration().save();
    }

    public void saveMobBorderValues() {
        saveMobBorderValues(null);
    }

    public void saveMobBorderValues(@Nullable final Entity override) {
        final var entity = Optional.ofNullable(override)
                .or(() -> getEntity().flatMap(MobBorderEntity::live))
                .orElse(null);
        if (entity == null) {
            return;
        }

        getConfiguration().setProperty(BorderSettings.BORDER_ORIGIN, LocationCodec.encode(entity.getLocation()));
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

        ItemStack frostWalkerBoots = new ItemStack(Material.NETHERITE_BOOTS);
        ItemMeta fwbMeta = frostWalkerBoots.getItemMeta();
        fwbMeta.addEnchant(Enchantment.FROST_WALKER, 1, true);
        frostWalkerBoots.setItemMeta(fwbMeta);

        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).getEquipment().setBoots(frostWalkerBoots);
        }

        entity.setSilent(getConfiguration().get(EntitySettings.ENTITY_SILENT));

        entity.setGlowing(getConfiguration().get(EntitySettings.ENTITY_GLOWING));

        final BukkitTask prev;

        if (!(entity instanceof LivingEntity living)) {
            prev = this.randomSpeedTask.getAndSet(null);
        } else {
            living.setRemoveWhenFarAway(false);

            final var frozen = getConfiguration().get(EntitySettings.ENTITY_FROZEN);
            living.setAI(!frozen);

            if (frozen) {
                prev = this.randomSpeedTask.getAndSet(null);
            } else {
                final var speedAttribute = living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                if (speedAttribute == null) {
                    prev = this.randomSpeedTask.getAndSet(null);
                } else {
                    final var speed = getConfiguration().get(EntitySettings.ENTITY_SPEED);
                    speedAttribute.setBaseValue(speed);

                    if (!getConfiguration().get(EntitySettings.ENTITY_RANDOM_SPEED_ENABLED)) {
                        prev = this.randomSpeedTask.getAndSet(null);
                    } else {
                        final var intervalTime = getConfiguration().get(EntitySettings.ENTITY_RANDOM_SPEED_INTERVAL_TIME);
                        final var intervalUnit = getConfiguration().get(EntitySettings.ENTITY_RANDOM_SPEED_INTERVAL_UNIT);

                        final var intervalTicks = TimeUnit.MILLISECONDS.convert(intervalTime, intervalUnit) / (1000L / 20L);

                        prev = this.randomSpeedTask.getAndSet(getServer().getScheduler().runTaskTimer(this, () ->
                        {
                            final var min = getConfiguration().get(EntitySettings.ENTITY_RANDOM_SPEED_MIN);
                            final var max = getConfiguration().get(EntitySettings.ENTITY_RANDOM_SPEED_MAX);

                            final var randomSpeed = ThreadLocalRandom.current().nextDouble(min, max);
                            speedAttribute.setBaseValue(randomSpeed);


                            final var sustainsTime = getConfiguration().get(EntitySettings.ENTITY_RANDOM_SPEED_SUSTAINS_TIME);
                            final var sustainsUnit = getConfiguration().get(EntitySettings.ENTITY_RANDOM_SPEED_SUSTAINS_UNIT);

                            final var sustainsTicks = TimeUnit.MILLISECONDS.convert(sustainsTime, sustainsUnit) / (1000L / 20L);

                            if (sustainsTicks < intervalTicks) {
                                getServer().getScheduler().runTaskLater(this, () -> speedAttribute.setBaseValue(speed), sustainsTicks);
                            }
                        }, intervalTicks, intervalTicks));
                    }
                }
            }
        }

        if (prev != null) {
            prev.cancel();
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
        final var hurt = getConfiguration().get(BorderSettings.BORDER_HURT);
        final var distHurt = getConfiguration().get(BorderSettings.BORDER_DIST_HURT);
        final var distWarn = getConfiguration().get(BorderSettings.BORDER_DIST_WARN);

        overworld.setSize(size);
        overworld.setDamageAmount(hurt);
        overworld.setDamageBuffer(distHurt);
        overworld.setWarningDistance(distWarn);


        if (theend != null) { // I'm... not sure if there is a scale on this.. ?
            theend.setSize(size);
            theend.setDamageAmount(hurt);
            theend.setDamageBuffer(distHurt);
            theend.setWarningDistance(distWarn);
        }

        if (nether != null) {
            final var scaledSize = size < 16 || !getConfiguration().get(BorderSettings.BORDER_SCALING) ?
                    size :
                    size / 8;

            nether.setSize(scaledSize);
            nether.setDamageAmount(hurt);
            nether.setDamageBuffer(distHurt);
            nether.setWarningDistance(distWarn);
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
