package com.sxtanna.mc.mb.conf.sections;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public final class BorderSettings implements SettingsHolder {

    public static final Property<Double> BORDER_SIZE =
            newProperty("border.size", 250.0);


    public static final Property<Integer> BORDER_DIST_WARN =
            newProperty("border.dist.warn", 5);

    public static final Property<Integer> BORDER_DIST_HURT =
            newProperty("border.dist.hurt", 5);


    public static final Property<String> BORDER_ORIGIN =
            newProperty("border.origin", "");

}
