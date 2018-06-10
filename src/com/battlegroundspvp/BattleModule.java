package com.battlegroundspvp;
/* Created by GamerBah on 10/15/2017 */

import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.util.cosmetic.Cosmetic;
import com.battlegroundspvp.util.cosmetic.Gore;
import com.battlegroundspvp.util.cosmetic.ParticlePack;
import com.battlegroundspvp.util.cosmetic.Warcry;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/*
 * This interface must be implemented by every main class of every plugin module
 * The purpose of this interface is to prove modules with a way to run their
 * own code for events & commands that happen within this plugin, in order
 * to avoid conflicts.
 *
 * This Core plugin will automatically detect which modules are active through
 * BattleModuleLoader.java, and will run the code for each module where it needs
 * to be run to avoid as many conflicts as possible.
 */
public class BattleModule<T extends JavaPlugin> {

    @Getter
    private final int hash;
    @Getter
    private final String name;
    @Getter
    private final T plugin;

    public BattleModule(final String name, final T plugin) {
        this.hash = name.hashCode();
        this.name = name;
        this.plugin = plugin;
    }

    public List<UUID> getInCombat() {
        return new ArrayList<>();
    }

    public ParticlePack getActiveTrail(Player player) {
        return null;
    }

    public Warcry getActiveWarcry(Player player) {
        return null;
    }

    public Gore getActiveGore(Player player) {
        return null;
    }

    public ArrayList<Cosmetic> getCosmetics() {
        return new ArrayList<>();
    }

    public void updateScoreboardRank(Player player, Rank rank) {
    }

    public void updateScoreboardCoins(Player player, int amount) {
    }

    // STANDARD EVENTS
    public void onBlockBreakEvent(BlockBreakEvent event) {
    }

    public void onBlockPlaceEvent(BlockPlaceEvent event) {
    }

    public void onInventoryClickEvent(InventoryClickEvent event) {
    }

    public void WeatherChangeEvent(WeatherChangeEvent event) {
    }

    // PLAYER EVENTS
    public void onPlayerCloseInventory(InventoryCloseEvent event) {
    }

    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) {
    }

    public void onPlayerDamage(EntityDamageEvent event) {
    }

    public void onPlayerInteractItem(PlayerInteractEvent event) {
    }

    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
    }

    public void onPlayerRespawn(PlayerRespawnEvent event) {
    }

    // COMMANDS
    public HashMap<String, CommandExecutor> getCommands() {
        return new HashMap<>();
    }

    boolean onDailyRewardCommand() {
        return false;
    }

    boolean onHelpCommand() {
        return false;
    }

    boolean onRulesCommand() {
        return false;
    }

    // UPDATES
    public void onPreUpdate() {
    }

}
