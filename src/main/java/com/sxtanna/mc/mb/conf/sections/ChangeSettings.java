package com.sxtanna.mc.mb.conf.sections;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;
import ch.jalu.configme.properties.types.BeanPropertyType;
import com.sxtanna.mc.mb.data.BlockDropChange;
import com.sxtanna.mc.mb.data.BlockDropChange.Drop;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.Map;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public final class ChangeSettings implements SettingsHolder {

    public static final Property<Boolean> IGNORE_BLOCKS_PLACED =
            newProperty("change.ignore-blocks-placed", true);

    public static final Property<Map<String, BlockDropChange>> CHANGES =
            PropertyInitializer.mapProperty(BeanPropertyType.of(BlockDropChange.class))
                               .path("change.changes")

                               .defaultEntry("iron_ore",
                                             BlockDropChange.set(Drop.of(Material.IRON_INGOT),
                                                                 Drop.of(Material.RAW_IRON)))

                               .defaultEntry("gold_ore",
                                             BlockDropChange.set(Drop.of(Material.GOLD_INGOT),
                                                                 Drop.of(Material.RAW_GOLD)))

                               .defaultEntry("copper_ore",
                                             BlockDropChange.set(Drop.of(Material.COPPER_INGOT),
                                                                 Drop.of(Material.RAW_COPPER)))

                               .defaultEntry("flower_to_golden_apple",
                                             BlockDropChange.add(1.0,
                                                                 Drop.of(Material.GOLDEN_APPLE),
                                                                 Tag.FLOWERS.getValues().stream().map(Drop::of).toList()))

                               .defaultEntry("bushes_to_carrots",
                                             BlockDropChange.add(Drop.of(Material.CARROT),
                                                                 Drop.of(Material.SWEET_BERRIES)))

                               .build();

}
