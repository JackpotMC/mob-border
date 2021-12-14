package com.sxtanna.mc.mb;

import com.sxtanna.mc.mb.conf.Config;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class MobBorderPlugin extends JavaPlugin {

    @Nullable
    private static MobBorderPlugin INSTANCE;

    @Contract(pure = true)
    public static @NotNull MobBorderPlugin get() {
        return Objects.requireNonNull(INSTANCE, "plugin not initialized");
    }


    @NotNull
    private final Config configuration = new Config(getDataFolder().toPath().resolve("config.yml"));


    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        INSTANCE = null;
    }


    @Contract(pure = true)
    public @NotNull Config getConfiguration() {
        return this.configuration;
    }

}
