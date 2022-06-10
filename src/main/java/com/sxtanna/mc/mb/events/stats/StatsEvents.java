package com.sxtanna.mc.mb.events.stats;

import com.mongodb.client.model.Updates;
import com.sxtanna.mc.mb.MobBorderPlugin;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class StatsEvents implements Listener {

    private final MobBorderPlugin plugin = MobBorderPlugin.INSTANCE;

    List<Material> ores = new ArrayList<>(List.of(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE, Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE));

    @EventHandler
    public void onPlayerDeathUpdateDB(PlayerDeathEvent event) {
        if (antiNullChecks(event)) {
            Bson query = eq("UUID", event.getEntity().getUniqueId().toString());
            if (containsUser(query)) {
                updateDeaths(query);
                updateKd(query);
            }
        }
    }

    @EventHandler
    public void onPlayerKillUpdateDB(PlayerDeathEvent event) {
        if (antiNullChecks(event)) {
            Bson query = eq("UUID", event.getEntity().getKiller().getUniqueId().toString());
            if (containsUser(query)) {
                updateKills(query);
                updateKd(query);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinAddOrUpdate(PlayerJoinEvent event) {
        Bson query = eq("UUID", event.getPlayer().getUniqueId().toString());
        if (!containsUser(query)) {
            addUser(event.getPlayer().getUniqueId(), 0, 0, "0.00", 0L, System.currentTimeMillis(), 0);
        } else {
            updateJoinTime(query);
        }

        if (!hasBackpack(query)) {
            addBackpack(event.getPlayer().getUniqueId(), "nothing");
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitUpdate(PlayerQuitEvent event) {
        Bson query = eq("UUID", event.getPlayer().getUniqueId().toString());
        if (containsUser(query)) {
            updatePlaytime(query);
        }
    }

    @EventHandler
    public void onOreBreak(BlockBreakEvent event) {
        Bson query = eq("UUID", event.getPlayer().getUniqueId().toString());
        Block block = event.getBlock();
        if (!isOre(block.getType())) return;
        if (block.getMetadata("PLACED") != null) return;
        if (!containsUser(query)) return;
        updateOres(query);
    }

    @EventHandler
    public void blockPlaced(BlockPlaceEvent event) {
        Block b = event.getBlock();
        b.setMetadata("PLACED", new FixedMetadataValue(plugin, "holder"));
    }

    private boolean isOre(Material blockType) {
        return ores.contains(blockType);
    }

    private boolean containsUser(Bson query) {
        return plugin.playerCollection.find(query).first() != null;
    }

    private boolean hasBackpack(Bson query) {
        return plugin.backpacksCollection.find(query).first() != null;
    }

    private void addUser(UUID UUID, int kills, int deaths, String kdRatio, Long playtime, Long joinTime, int oresMined) {
        //String.valueOf(Math.round((kills / deaths * 100)) / 100.0D) - kd
        Document playerDoc = new Document().append("UUID", UUID.toString()).append("Kills", kills).append("Deaths", deaths).append("KD", kdRatio).append("Playtime", playtime).append("JoinTime", joinTime).append("OresMined", oresMined);
        plugin.playerCollection.insertOne(playerDoc);
    }

    private void addBackpack(UUID UUID, String data) {
        Document playerDoc = new Document().append("UUID", UUID.toString()).append("BackpackItems", data);
        plugin.backpacksCollection.insertOne(playerDoc);
    }

    private void updateDeaths(Bson query) {
        Document first = plugin.playerCollection.find(query).first();
        Integer d = first.getInteger("Deaths");
        Bson deaths = Updates.set("Deaths", d + 1);
        plugin.playerCollection.findOneAndUpdate(first, deaths);
    }

    private void updateKills(Bson query) {
        Document first = plugin.playerCollection.find(query).first();
        Integer k = first.getInteger("Kills");
        Bson kills = Updates.set("Kills", k + 1);
        plugin.playerCollection.findOneAndUpdate(first, kills);
    }

    private void updateKd(Bson query) {
        Document first = plugin.playerCollection.find(query).first();
        Integer k = first.getInteger("Kills");
        Integer d = first.getInteger("Deaths");
        Bson kd = Updates.set("KD", String.valueOf(Math.round((k / d * 100)) / 100.0D));
        plugin.playerCollection.findOneAndUpdate(first, kd);
    }

    private void updatePlaytime(Bson query) {
        Document first = plugin.playerCollection.find(query).first();
        Long pt = first.getLong("Playtime");
        Long jt = first.getLong("JoinTime");
        Bson playtime = Updates.set("Playtime", (System.currentTimeMillis() - jt) + pt);
        plugin.playerCollection.findOneAndUpdate(first, playtime);
    }

    private void updateJoinTime(Bson query) {
        Document first = plugin.playerCollection.find(query).first();
        Bson playtime = Updates.set("JoinTime", System.currentTimeMillis());
        plugin.playerCollection.findOneAndUpdate(first, playtime);
    }

    private void updateOres(Bson query) {
        Document first = plugin.playerCollection.find(query).first();
        Integer o = first.getInteger("OresMined");
        Bson ores = Updates.set("OresMined", o + 1);
        plugin.playerCollection.findOneAndUpdate(first, ores);
    }

    private boolean antiNullChecks(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if (killer == null) return false;
        return player.getUniqueId() != killer.getUniqueId();
    }

    public String getKills(Player player) {
        Bson query = eq("UUID", player.getUniqueId().toString());
        Document first = plugin.playerCollection.find(query).first();
        return String.valueOf(first.getInteger("Kills"));
    }

    public String getDeaths(Player player) {
        Bson query = eq("UUID", player.getUniqueId().toString());
        Document first = plugin.playerCollection.find(query).first();
        return String.valueOf(first.getInteger("Deaths"));
    }

    public String getKd(Player player) {
        Bson query = eq("UUID", player.getUniqueId().toString());
        Document first = plugin.playerCollection.find(query).first();
        return first.getString("KD");
    }

    public String getOresMined(Player player) {
        Bson query = eq("UUID", player.getUniqueId().toString());
        Document first = plugin.playerCollection.find(query).first();
        return String.valueOf(first.getInteger("OresMined"));
    }


}
