package com.sxtanna.mc.mb.cmds;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.sxtanna.mc.mb.MobBorderPlugin;
import com.sxtanna.mc.mb.combat.CombatAPI;
import com.sxtanna.mc.mb.revents.events.*;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@CommandAlias("events")
@CommandPermission("jmc.events")
public class StartEvents extends BaseCommand {

    private final AcidRain acidRain = new AcidRain();
    private final AirDrops airDrops = new AirDrops();
    private final EndPortal endPortal = new EndPortal();
    private final LuckyBlock luckyBlock = new LuckyBlock();
    private final PetCreeper petCreeper = new PetCreeper();
    private final LootRain lootRain = new LootRain();

    @NotNull
    private final MobBorderPlugin plugin;
    private final CombatAPI api = new CombatAPI();

    @Contract(pure = true)
    public StartEvents(@NotNull final MobBorderPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @HelpCommand
    public void help(@NotNull final CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("acidrain")
    @CommandPermission("jmc.events.acidrain")
    public void acidrain(@NotNull final CommandSender sender) {
        acidRain.commenceAcidRain();
    }

    @Subcommand("airdrops")
    @CommandPermission("jmc.events.airdrops")
    public void airdrops(@NotNull final CommandSender sender) {
        airDrops.spawnAirdrops();
    }

    @Subcommand("endportal")
    @CommandPermission("jmc.events.endportal")
    public void endportal(@NotNull final CommandSender sender) {
        endPortal.spawnRandomEndPortal();
    }

    @Subcommand("luckyblock")
    @CommandPermission("jmc.events.luckyblock")
    public void luckyblock(@NotNull final CommandSender sender) {
        luckyBlock.spawnLuckyBlocks();
    }

    @Subcommand("petcreeper")
    @CommandPermission("jmc.events.petcreeper")
    public void petcreeper(@NotNull final CommandSender sender) {
        petCreeper.spawnCreepers();
    }

    @Subcommand("lootrain")
    @CommandPermission("jmc.events.lootrain")
    public void lootrain(@NotNull final CommandSender sender) {
        lootRain.rainLoot();
    }

}
