package com.sxtanna.mc.mb.cmds.pray.donator;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.sxtanna.mc.mb.MobBorderPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@CommandAlias("smithingtable|smithing|smithtable")
@CommandPermission("jmc.donator.smithingtable")
public final class SmithingTableCommand extends BaseCommand {
    @NotNull
    private final MobBorderPlugin plugin;

    @Contract(pure = true)
    public SmithingTableCommand(@NotNull final MobBorderPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void smithingTableCommand(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            player.openSmithingTable(null, true);
        }
    }

}
