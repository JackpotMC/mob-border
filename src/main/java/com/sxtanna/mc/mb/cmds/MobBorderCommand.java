package com.sxtanna.mc.mb.cmds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.conf.sections.BorderSettings;
import com.sxtanna.mc.mb.conf.sections.EntitySettings;
import com.sxtanna.mc.mb.data.MobBorderEntity;
import com.sxtanna.mc.mb.util.LocationCodec;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mobborder|mborder|mb")
@CommandPermission("jmc.mobborder")
public final class MobBorderCommand extends BaseCommand {

    @NotNull
    private final MobBorderPlugin plugin;


    @Contract(pure = true)
    public MobBorderCommand(@NotNull final MobBorderPlugin plugin) {
        this.plugin = plugin;
    }


    @Default
    @HelpCommand
    public void help(@NotNull final CommandHelp help) {
        help.showHelp();
    }


    @Subcommand("create")
    @CommandPermission("jmc.mobborder.create")
    public void create(@NotNull final CommandSender sender, @NotNull final World world) {
        final var origin = sender instanceof Player player && world.equals(player.getWorld()) ?
                           player.getLocation() :
                           world.getSpawnLocation();

        this.plugin.getConfiguration().setProperty(BorderSettings.BORDER_ORIGIN, LocationCodec.encode(origin));
        this.plugin.getConfiguration().save();

        this.plugin.loadMobBorderEntity();
    }

    @Subcommand("remove")
    @CommandPermission("jmc.mobborder.remove")
    public void remove(@NotNull final CommandSender sender) {
        this.plugin.getConfiguration().setProperty(BorderSettings.BORDER_ORIGIN, "");
        this.plugin.getConfiguration().save();

        this.plugin.killMobBorderEntity();
    }


    @Subcommand("resize")
    @CommandPermission("jmc.mobborder.resize")
    public void resize(@NotNull final CommandSender sender, @Default("250") @Flags("min=16,max=10000") final int size) {
        this.plugin.getConfiguration().setProperty(BorderSettings.BORDER_SIZE, ((double) size));
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                   .flatMap(MobBorderEntity::live)
                   .ifPresent(this.plugin::updateWorldBorders);
    }

    @Subcommand("scaling")
    @CommandPermission("jmc.mobborder.scaling")
    public void scaling(@NotNull final CommandSender sender, @Default("false") final boolean scaling) {
        this.plugin.getConfiguration().setProperty(BorderSettings.BORDER_SCALING, scaling);
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                   .flatMap(MobBorderEntity::live)
                   .ifPresent(this.plugin::updateWorldBorders);
    }


    @Subcommand("entity rename")
    @CommandPermission("jmc.mobborder.entity.rename")
    public void silent(@NotNull final CommandSender sender, @NotNull final String name) {
        this.plugin.getConfiguration().setProperty(EntitySettings.ENTITY_NAME, name);
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                   .flatMap(MobBorderEntity::live)
                   .ifPresent(this.plugin::updateEntityValues);
    }

    @Subcommand("entity silent")
    @CommandPermission("jmc.mobborder.entity.silent")
    public void silent(@NotNull final CommandSender sender, @Default("false") final boolean silent) {
        this.plugin.getConfiguration().setProperty(EntitySettings.ENTITY_SILENT, silent);
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                   .flatMap(MobBorderEntity::live)
                   .ifPresent(this.plugin::updateEntityValues);
    }

    @Subcommand("entity glowing")
    @CommandPermission("jmc.mobborder.entity.glowing")
    public void glowing(@NotNull final CommandSender sender, @Default("true") final boolean glowing) {
        this.plugin.getConfiguration().setProperty(EntitySettings.ENTITY_GLOWING, glowing);
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                   .flatMap(MobBorderEntity::live)
                   .ifPresent(this.plugin::updateEntityValues);
    }

    @Subcommand("entity speed")
    @CommandPermission("jmc.mobborder.entity.speed")
    public void speed(@NotNull final CommandSender sender, @Default("0.25") @Flags("min=0.0,max=1024.0") final double speed) {
        this.plugin.getConfiguration().setProperty(EntitySettings.ENTITY_SPEED, speed);
        this.plugin.getConfiguration().save();

        this.plugin.getEntity()
                   .flatMap(MobBorderEntity::live)
                   .ifPresent(this.plugin::updateEntityValues);
    }


    @Subcommand("reload")
    @CommandPermission("jmc.mobborder.reload")
    public void reload(@NotNull final CommandSender sender) {
        this.plugin.getConfiguration().reload();

        this.plugin.loadMobBorderEntity();
    }

}
