package com.battlegroundspvp.runnables;

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.commands.FreezeCommand;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.utils.PluginUtil;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.messages.ColorBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class UpdateRunnable implements Runnable {

    public static boolean updating = false;
    private BattlegroundsCore plugin;
    private File updateFile, currentFile;

    public UpdateRunnable(BattlegroundsCore plugin) {
        this.plugin = plugin;

        if (!plugin.getServer().getUpdateFolderFile().exists()) {
            plugin.getServer().getUpdateFolderFile();
        }

        updateFile = new File(plugin.getServer().getUpdateFolderFile().getPath() + File.separator + "BattlegroundsCore.jar");
        currentFile = new File(plugin.getServer().getUpdateFolderFile().getParentFile().getPath() + File.separator + "BattlegroundsCore.jar");
    }

    @Override
    public void run() {
        if (updateFile == null || !updateFile.exists()) {
            for (BattleModule module : BattleModuleLoader.modules.keySet())
                if (moduleHasUpdate(module))
                    moduleUpdate(module);
            return;
        }
        runPreUpdate();
        // Begin updating
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            try {
                // Sync data with database
                BattlegroundsCore.syncGameProfiles();
                // Push updates after 3 seconds (to allow profiles to sync)
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    // Unload modules
                    for (BattleModule module : BattleModuleLoader.modules.keySet())
                        PluginUtil.unload(BattleModuleLoader.modules.get(module));
                    // Unload Core
                    PluginUtil.unload(plugin);
                    try {
                        Files.move(Paths.get(currentFile.getPath()), Paths.get(plugin.getDataFolder().getPath() + File.separator + "BattlegroundsCore.jar"),
                                StandardCopyOption.REPLACE_EXISTING);
                        currentFile = new File(plugin.getDataFolder().getPath() + File.separator + "BattlegroundsCore.jar");
                        Files.move(Paths.get(updateFile.getPath()), Paths.get(plugin.getServer().getUpdateFolderFile().getParentFile().getPath()
                                + File.separator + "BattlegroundsCore.jar"), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException exception) {
                        plugin.getLogger().severe("Unable to complete update!");
                        plugin.getLogger().severe(exception.getCause().toString());
                        deleteUpdateFile(updateFile);
                    }

                    // Check for per-module updates, run accordingly
                    for (BattleModule module : BattleModuleLoader.modules.keySet())
                        if (moduleHasUpdate(module))
                            updateModule(module);

                    try {
                        // Load Core
                        PluginUtil.load("BattlegroundsCore");
                    } catch (Throwable throwable) {
                        plugin.getLogger().severe("Unable to preform plugin update!");
                        throwable.printStackTrace();
                        revert();
                        return;
                    }
                    plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY
                            + "Update was a " + new ColorBuilder(ChatColor.GREEN).bold().create() + "success" + ChatColor.GRAY + "! Now go have fun!");
                }, 60L);
            } catch (Throwable throwable) {
                plugin.getLogger().severe("Error during pre-update, sticking with the current version");
                throwable.printStackTrace();
                plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY
                        + "Stuck with current version due to issues with the update.");
                deleteUpdateFile(updateFile);
            } finally {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                    player.setWalkSpeed(0.2F);
                    plugin.respawn(player);
                }
                updating = false;
                FreezeCommand.reloadFreeze = false;
            }
        }, 100);
    }

    private void revert() {
        plugin.getLogger().severe("Reverting to working jarfile, hang tight...");
        plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY
                + "Update was a " + new ColorBuilder(ChatColor.RED).bold().create() + "failure" + ChatColor.GRAY + "!");
        plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY
                + "Reverting to the previous version, hang tight...");
        try {
            Files.move(currentFile.toPath(), Paths.get(plugin.getServer().getUpdateFolderFile().getParentFile().getPath()
                            + File.separator + "BattlegroundsCore.jar"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            plugin.getLogger().severe("Unable to revert!");
            exception.printStackTrace();
        }
        try {
            PluginUtil.load("BattlegroundsCore");
        } catch (Throwable throwable) {
            plugin.getLogger().severe("Reverted jar has errors, but it'll have to do");
            throwable.printStackTrace();
        }
        plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY
                + "Successfully reverted to the previous version.");
    }

    private void deleteUpdateFile(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException exception) {
            plugin.getLogger().severe("Unable to delete queued jarfile!");
            exception.printStackTrace();
        }
    }

    private void runPreUpdate() {
        plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY + "Reloading in 5 seconds for an update. Hang in there!");
        plugin.getServer().broadcastMessage(ChatColor.GRAY + "(You'll get sent back to the spawn once the update is complete)");

        updating = true;
        FreezeCommand.reloadFreeze = true;
        for (Player player : plugin.getServer().getOnlinePlayers()) {

            for (Player players : BattlegroundsCore.getCrateOpening().keySet()) {
                GameProfile gameProfile = BattlegroundsCore.getInstance().getGameProfile(players.getUniqueId());
                gameProfile.getCratesData().addCrate(BattlegroundsCore.getCrateOpening().get(player), 1);
                player.sendMessage(" ");
                player.sendMessage(ChatColor.GRAY + "The " + BattlegroundsCore.getCrateOpening().get(player).getName()
                        + ChatColor.GRAY + " you were opening has been given back to you");
            }

            // Run Core pre-update
            EventSound.playSound(player, EventSound.CLICK);
            player.setGameMode(GameMode.ADVENTURE);
            player.closeInventory();
            for (PotionEffect potionEffect : player.getActivePotionEffects())
                player.removePotionEffect(potionEffect.getType());
            player.setWalkSpeed(0F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -100, true, false));
            player.setFoodLevel(6);
            player.setSaturation(0);
            player.sendMessage(" ");
            player.sendMessage(ChatColor.YELLOW + "To prevent data corruption, you've been frozen.");
            player.sendMessage(ChatColor.YELLOW + "You'll be unfrozen once the update is complete.\n ");

            // Run module pre-update
            for (BattleModule module : BattleModuleLoader.modules.keySet())
                module.onPreUpdate();
        }
    }

    private void updateModule(BattleModule module) {
        String jarName = "Module-" + module.getName() + ".jar";
        File newModuleFile = new File(plugin.getServer().getUpdateFolderFile().getPath() + File.separator + jarName);
        File currentModuleFile = new File(plugin.getDataFolder().getPath() + "/modules" + File.separator + jarName);
        try {
            Files.move(Paths.get(currentModuleFile.getPath()), Paths.get(plugin.getDataFolder().getPath() + "/modules/backup" + File.separator + jarName), StandardCopyOption.REPLACE_EXISTING);
            Files.move(Paths.get(newModuleFile.getPath()), Paths.get(plugin.getDataFolder().getPath() + "/modules" + File.separator + jarName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            plugin.getLogger().severe("Unable to complete module update!");
            plugin.getLogger().severe(exception.getCause().toString());
            deleteUpdateFile(newModuleFile);
        }
    }

    private boolean moduleHasUpdate(BattleModule module) {
        String jarName = "Module-" + module.getName() + ".jar";
        File update = new File(BattlegroundsCore.getInstance().getServer().getUpdateFolderFile().getPath() + File.separator + jarName);
        return update.exists();
    }

    private void moduleUpdate(BattleModule module) {
        runPreUpdate();
        // Push Updates
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            try {
                BattlegroundsCore.syncGameProfiles();
                for (BattleModule modules : BattleModuleLoader.modules.keySet())
                    PluginUtil.unload(BattleModuleLoader.modules.get(modules));
                PluginUtil.unload(plugin);
                updateModule(module);
                PluginUtil.load("BattlegroundsCore");
            } catch (Throwable throwable) {
                plugin.getLogger().severe("Error during pre-update, sticking with the current version");
                throwable.printStackTrace();
                plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY
                        + "Stuck with current version due to issues with the update.");
            }
            for (BattleModule modules : BattleModuleLoader.modules.keySet())
                if (moduleHasUpdate(modules))
                    return;

            plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY
                    + "Update was a " + new ColorBuilder(ChatColor.GREEN).bold().create() + "success" + ChatColor.GRAY + "! Now go have fun!");
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                player.setWalkSpeed(0.2F);
                plugin.respawn(player);
            }
            updating = false;
            FreezeCommand.reloadFreeze = false;
        }, 100);
    }
}
