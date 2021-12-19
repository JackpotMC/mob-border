package com.sxtanna.mc.mb.conf.sections;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import org.bukkit.entity.EntityType;

import java.util.concurrent.TimeUnit;

import static ch.jalu.configme.properties.PropertyInitializer.newBeanProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public final class EntitySettings implements SettingsHolder {

    public static final Property<EntityType> ENTITY_TYPE =
            newBeanProperty(EntityType.class, "entity.type", EntityType.PIG);


    public static final Property<String> ENTITY_UUID =
            newProperty("entity.uuid", "");


    public static final Property<String> ENTITY_NAME =
            newProperty("entity.name", "&cPiggy");

    public static final Property<Boolean> ENTITY_SILENT =
            newProperty("entity.silent", false);

    public static final Property<Double> ENTITY_SPEED =
            newProperty("entity.speed", 0.25);

    public static final Property<Boolean> ENTITY_GLOWING =
            newProperty("entity.glowing", true);

    public static final Property<Boolean> ENTITY_FROZEN =
            newProperty("entity.frozen", false);

    public static final Property<Boolean> ENTITY_RESPAWNS =
            newProperty("entity.automatically-respawns", false);

    public static final Property<Boolean> ENTITY_ALLOW_CREATIVE_LEASHING =
            newProperty("entity.overrides.creative-leashing", true);


    public static final Property<Boolean> ENTITY_RANDOM_SPEED_ENABLED =
            newProperty("entity.random-speed.enabled", false);


    public static final Property<Integer> ENTITY_RANDOM_SPEED_INTERVAL_TIME =
            newProperty("entity.random-speed.interval.time", 30);

    public static final Property<TimeUnit> ENTITY_RANDOM_SPEED_INTERVAL_UNIT =
            newBeanProperty(TimeUnit.class, "entity.random-speed.interval.unit", TimeUnit.SECONDS);


    public static final Property<Integer> ENTITY_RANDOM_SPEED_SUSTAINS_TIME =
            newProperty("entity.random-speed.sustains.time", 15);

    public static final Property<TimeUnit> ENTITY_RANDOM_SPEED_SUSTAINS_UNIT =
            newBeanProperty(TimeUnit.class, "entity.random-speed.sustains.unit", TimeUnit.SECONDS);


    public static final Property<Double> ENTITY_RANDOM_SPEED_MIN =
            newProperty("entity.random-speed.min", 0.50);

    public static final Property<Double> ENTITY_RANDOM_SPEED_MAX =
            newProperty("entity.random-speed.max", 1.0);

}
