package com.battlegroundspvp.util.manager;
/* Created by GamerBah on 7/1/2018 */

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.command.FreezeCommand;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.PluginUtil;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.message.MessageBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.logging.Level;

public final class UpdateManager {

    private static BattlegroundsCore plugin = BattlegroundsCore.getInstance();

    @Getter
    @Setter
    private static boolean updating;
    @Getter
    @Setter
    private static boolean developmentMode;
    @Getter
    @Setter
    private static boolean awaitingUpdate;

    @Getter
    private static File updateFile;
    @Getter
    private static File currentFile = new File(plugin.getServer().getUpdateFolderFile().getParentFile().getPath() + File.separator + "BattlegroundsCore.jar");

    public static void update(final UpdateType type) {
        updateFile = new File(plugin.getServer().getUpdateFolderFile().getPath() + File.separator + "BattlegroundsCore.jar");
        Throwable preUpdate = runPreUpdate();
        if (preUpdate == null) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                try {
                    GameProfileManager.sync();
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        try {
                            SessionManager.shutdown();
                        } catch (Throwable throwable) {
                            sendLocalizedLog(Level.WARNING, throwable, "Unable to shutdown SessionManager!");
                        }
                        Result result = Result.SUCCESS;
                        BattleModuleLoader.getModules().values().forEach(PluginUtil::unload);
                        switch (type) {
                            case PLUGIN: {
                                PluginUtil.unload(plugin);
                                if (updateFile.exists()) {
                                    try {
                                        Files.move(Paths.get(currentFile.getPath()),
                                                Paths.get(plugin.getDataFolder().getPath() + File.separator + "BattlegroundsCore.jar"),
                                                StandardCopyOption.REPLACE_EXISTING);
                                        currentFile = new File(plugin.getDataFolder().getPath() + File.separator + "BattlegroundsCore.jar");
                                        Files.move(Paths.get(updateFile.getPath()),
                                                Paths.get(plugin.getServer().getUpdateFolderFile().getParentFile().getPath() + File.separator + "BattlegroundsCore.jar"),
                                                StandardCopyOption.REPLACE_EXISTING);
                                        setAwaitingUpdate(false);
                                    } catch (IOException exception) {
                                        sendLocalizedLog(Level.SEVERE, exception, "Unable to move update file!");
                                        result = deleteQueuedUpdate(updateFile);
                                    }
                                }

                                BattleModuleLoader.getModules().keySet().stream().filter(BattleModule::isAwaitingUpdate).forEach(UpdateManager::updateModule);

                                try {
                                    PluginUtil.load("BattlegroundsCore");
                                } catch (Throwable throwable) {
                                    sendLocalizedLog(Level.SEVERE, throwable, "Unable to load update file!", "Performing rollback, hang tight...");
                                    result = rollback();
                                }
                                break;
                            }
                            case SERVER: {
                                try {
                                    PluginUtil.unload(plugin);
                                    plugin.getServer().reload();
                                } catch (Throwable throwable) {
                                    sendLocalizedLog(Level.SEVERE, throwable, "Unable to perform global reload!", "Entering Maintenance Mode...");
                                    result = enterMaintenance();
                                }
                                break;
                            }
                        }
                        runPostUpdate(result);
                    }, 60L);
                } catch (Throwable throwable) {
                    sendLocalizedLog(Level.WARNING, throwable, "Unable to perform full-sync!", "Entering Maintenance Mode...");
                    runPostUpdate(enterMaintenance());
                }
            }, 100);
        } else {
            sendLocalizedLog(Level.SEVERE, preUpdate, "Unable to complete pre-update!", "Reload cancelled.");
            runPostUpdate(Result.FAIL);
        }
    }

    private static Result updateModule(BattleModule module) {
        String jarName = "Module-" + module.getName() + ".jar";
        File newModuleFile = new File(plugin.getServer().getUpdateFolderFile().getPath() + File.separator + jarName);
        File currentModuleFile = new File(plugin.getDataFolder().getPath() + "/modules" + File.separator + jarName);
        try {
            Files.move(Paths.get(currentModuleFile.getPath()), Paths.get(plugin.getDataFolder().getPath() + "/modules/backup" + File.separator + jarName), StandardCopyOption.REPLACE_EXISTING);
            Files.move(Paths.get(newModuleFile.getPath()), Paths.get(plugin.getDataFolder().getPath() + "/modules" + File.separator + jarName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            sendLocalizedLog(Level.SEVERE, exception, "Unable to complete module update!");
            deleteQueuedUpdate(newModuleFile);
            return Result.FAIL;
        }
        return Result.SUCCESS;
    }

    private static Result deleteQueuedUpdate(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException exception) {
            plugin.getLogger().severe("Unable to delete queued jarfile!");
            exception.printStackTrace();
        }
        return Result.FAIL;
    }

    private static Throwable runPreUpdate() {
        plugin.getServer().broadcastMessage(new MessageBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY + "Reloading in 5 seconds for an update. Hang in there!");
        plugin.getServer().broadcastMessage(ChatColor.GRAY + "(You'll get sent back to the spawn once the update is complete)");
        updating = true;
        FreezeCommand.reloadFreeze = true;
        try {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                if (BattleCrateManager.isUsing(player)) {
                    GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());
                    if (gameProfile != null) {
                        gameProfile.getCratesData().addCrate(BattleCrateManager.getPlayersUsing().get(player.getUniqueId()), 1);
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.GRAY + "The " + BattleCrateManager.getPlayersUsing().get(player.getUniqueId()).getName()
                                + ChatColor.GRAY + " you were opening has been given back to you");
                    }
                }

                // Run Core pre-update
                EventSound.playSound(player, EventSound.CLICK);
                player.setGameMode(GameMode.ADVENTURE);
                player.closeInventory();
                player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -100, true, false));
                player.setWalkSpeed(0F);
                player.setFoodLevel(6);
                player.setSaturation(0);

                player.sendMessage(" ");
                player.sendMessage(ChatColor.YELLOW + "To prevent data corruption, you've been frozen.");
                player.sendMessage(ChatColor.YELLOW + "You'll be unfrozen once the update is complete.\n ");

                BattleModuleLoader.getModules().keySet().forEach(BattleModule::onPreUpdate);
            });
        } catch (Throwable e) {
            return e;
        }
        return null;
    }

    private static void runPostUpdate(final Result result) {
        if (!SessionManager.isStarted())
            SessionManager.start();
        plugin.getServer().broadcastMessage(new MessageBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY + "Update was a " + result.getString() + ChatColor.GRAY + "!");
        updating = false;
        FreezeCommand.reloadFreeze = false;
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            switch (result) {
                case SUCCESS:
                    EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                    break;
                default:
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    break;
            }
            player.setWalkSpeed(0.2F);
            plugin.respawn(player);
        });
    }

    public static Result enterMaintenance() {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());
            if (gameProfile != null && !gameProfile.hasRank(Rank.HELPER)) {
                player.kickPlayer(ChatColor.RED + "You were kicked because the server was put into\n" + new MessageBuilder(ChatColor.GOLD).bold().create()
                        + "MAINTENANCE MODE\n\n" + ChatColor.AQUA + "This means that we are fixing bugs, or found another issue we needed to take care of\n\n"
                        + ChatColor.GRAY + "We put the server into Maintenance Mode in order to reduce the risk of\n§7corrupting player data, etc. Check back soon!");
            }
            EventSound.playSound(player, EventSound.ACTION_FAIL);
        });
        setDevelopmentMode(true);
        plugin.getServer().broadcastMessage(new MessageBuilder(ChatColor.RED).bold().create() + "\nSERVER HAS BEEN PUT INTO " + new MessageBuilder(ChatColor.GOLD).bold().create() + "MAINTENANCE MODE\n ");
        return Result.MAINTENANCE;
    }

    private static Result rollback() {
        try {
            Files.move(currentFile.toPath(),
                    Paths.get(plugin.getServer().getUpdateFolderFile().getParentFile().getPath() + File.separator + "BattlegroundsCore.jar"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            sendLocalizedLog(Level.SEVERE, exception, "Unable to load rollback jar!", "Entering Maintenance Mode...");
            return enterMaintenance();
        }
        try {
            PluginUtil.load("BattlegroundsCore");
        } catch (Throwable throwable) {
            sendLocalizedLog(Level.WARNING, throwable, "Rollback jar has errors, but it'll have to do...");
        }
        return Result.ROLLBACK;
    }

    public static void sendLocalizedLog(final Level level, final Throwable throwable, final String... messages) {
        plugin.getLogger().log(level, "----------------------------------------");
        plugin.getLogger().log(level, throwable.getCause().getLocalizedMessage());
        Arrays.asList(messages).forEach(message -> plugin.getLogger().log(level, message));
        plugin.getLogger().log(level, "----------------------------------------");
    }

    public enum UpdateType {
        SERVER, PLUGIN, MODULES
    }

    @AllArgsConstructor
    @Getter
    public enum Result {
        SUCCESS(new MessageBuilder(ChatColor.GREEN).bold().create() + "success"),
        FAIL(new MessageBuilder(ChatColor.RED).bold().create() + "failure"),
        ROLLBACK(FAIL.getString()),
        MAINTENANCE(FAIL.getString());

        private String string;
    }

}