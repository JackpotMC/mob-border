package com.sxtanna.mc.mb.conf.sections;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import org.bukkit.entity.EntityType;

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

    public static final Property<Boolean> ENTITY_RESPAWNS =
            newProperty("entity.automatically-respawns", false);

    public static final Property<Boolean> ENTITY_ALLOW_CREATIVE_LEASHING =
            newProperty("entity.overrides.creative-leashing", true);

}
