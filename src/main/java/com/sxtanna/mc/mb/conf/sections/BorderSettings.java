package com.sxtanna.mc.mb.conf.sections;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public final class BorderSettings implements SettingsHolder {

    public static final Property<Double> BORDER_SIZE =
            newProperty("border.size", 250.0);


    public static final Property<Double> BORDER_HURT =
            newProperty("border.hurt", 0.2);


    public static final Property<Integer> BORDER_DIST_WARN =
            newProperty("border.dist.warn", 5);

    public static final Property<Integer> BORDER_DIST_HURT =
            newProperty("border.dist.hurt", 5);


    public static final Property<String> BORDER_ORIGIN =
            newProperty("border.origin", "");

    public static final Property<Boolean> BORDER_SCALING =
            newProperty("border.scaling", false);


    public static final Property<Boolean> TELEPORT_IN_ON_JOIN =
            newProperty("border.teleport-in-on-join", false);

    public static final Property<Boolean> RANDOMIZED_RESPAWNS =
            newProperty("border.randomized-respawns", true);

    public static final Property<Boolean> RESPAWN_WITH_ENTITY =
            newProperty("border.respawn-with-entity", true);

}
