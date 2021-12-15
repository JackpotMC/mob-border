package com.sxtanna.mc.mb.conf;

import ch.jalu.configme.SettingsManagerImpl;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.YamlFileResource;
import com.sxtanna.mc.mb.conf.sections.BorderSettings;
import com.sxtanna.mc.mb.conf.sections.ChangeSettings;
import com.sxtanna.mc.mb.conf.sections.EntitySettings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static ch.jalu.configme.configurationdata.ConfigurationDataBuilder.createConfiguration;

public final class Config extends SettingsManagerImpl {


    public Config(@NotNull final Path path) {
        super(new AutoYamlResource(path),
              createConfiguration(BorderSettings.class,
                                  ChangeSettings.class,
                                  EntitySettings.class),
              new PlainMigrationService());
    }


    public <T> @NotNull T get(@NotNull final Property<T> property) {
        try {
            return Optional.ofNullable(getProperty(property)).orElse(property.getDefaultValue());
        } catch (final Throwable ignored) {
            return property.getDefaultValue();
        }
    }


    private static final class AutoYamlResource extends YamlFileResource {

        AutoYamlResource(@NotNull final Path path) {
            super(ensureExists(path));
        }


        @Contract("_ -> param1")
        private static Path ensureExists(@NotNull final Path path) {
            if (Files.exists(path)) {
                return path;
            }

            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (final IOException ignored) {
                // ignored
            }

            return path;
        }

    }

}
