package com.battlegroundspvp.runnables;

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.commands.FreezeCommand;
import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.DiscordBot;
import com.battlegroundspvp.utils.PluginUtil;
import com.battlegroundspvp.utils.enums.EventSound;
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
            return;
        }
        plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY + "Reloading in 5 seconds for an update. Hang in there!");
        plugin.getServer().broadcastMessage(ChatColor.GRAY + "(You'll get sent back to the spawn once the update is complete)");
        updating = true;
        // TODO: TRIGGER SUB-PLUGIN UPDATE CORRUPTION PREVENTION
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            EventSound.playSound(player, EventSound.CLICK);
            player.setGameMode(GameMode.ADVENTURE);
            player.closeInventory();
            plugin.getServer().getScheduler().runTaskLater(plugin, player::closeInventory, 10L);
            FreezeCommand.reloadFreeze = true;
            for (PotionEffect potionEffect : player.getActivePotionEffects())
                player.removePotionEffect(potionEffect.getType());
            player.setWalkSpeed(0F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -100, true, false));
            player.setFoodLevel(6);
            player.setSaturation(0);
            player.sendMessage(" ");
            player.sendMessage(ChatColor.YELLOW + "To prevent data corruption, you've been frozen.");
            player.sendMessage(ChatColor.YELLOW + "You'll be unfrozen once the update is complete.\n ");
        }
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            try {
                BattlegroundsCore.syncGameProfiles();
                PluginUtil.unload(plugin);
                try {
                    Files.move(Paths.get(currentFile.getPath()), Paths.get(plugin.getDataFolder().getPath() + File.separator + "BattlegroundsCore.jar"),
                            StandardCopyOption.REPLACE_EXISTING);
                    currentFile = new File(plugin.getDataFolder().getPath() + File.separator + "BattlegroundsCore.jar");
                    Files.move(Paths.get(updateFile.getPath()), Paths.get(plugin.getServer().getUpdateFolderFile().getParentFile().getPath()
                            + File.separator + "BattlegroundsCore.jar"), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException exception) {
                    plugin.getLogger().severe("Unable to complete update!");
                    DiscordBot.errorLoggingChannel.sendMessageFormat("%s There was an error trying to update BattlegroundsCore! (Code: BC-UpdRun:TrCa.67)",
                            BattlegroundsCore.getAresDiscordBot().getRolesByName("staff", true).get(0)).queue();
                    plugin.getLogger().severe(exception.getCause().toString());
                    deleteUpdateFile();
                }

                try {
                    PluginUtil.load("BattlegroundsCore");
                } catch (Throwable throwable) {
                    plugin.getLogger().severe("Unable to preform plugin update!");
                    DiscordBot.errorLoggingChannel.sendMessageFormat("%s There was an error while loading the update for BattlegroundsCore! (Code: BC-UpdRun:TrCa.80)",
                            BattlegroundsCore.getAresDiscordBot().getRolesByName("staff", true).get(0)).queue();
                    plugin.getLogger().severe(throwable.getCause().toString());
                    revert();
                    return;
                }
                plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY
                        + "Update was a " + new ColorBuilder(ChatColor.GREEN).bold().create() + "success" + ChatColor.GRAY + "! Now go have fun!");
            } catch (Throwable throwable) {
                plugin.getLogger().severe("Error during pre-update, sticking with the current version");
                DiscordBot.errorLoggingChannel.sendMessage("@Staff There was an error beginning the update for BattlegroundsCore! (Code: BC-UpdRun:TrCa.64)").queue();
                plugin.getLogger().severe(throwable.getMessage());
                throwable.printStackTrace();
                plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY
                        + "Stuck with current version due to issues with the update.");
                deleteUpdateFile();
            } finally {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                    player.setWalkSpeed(0.2F);
                    player.spigot().respawn();
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
            plugin.getLogger().severe(exception.getCause().toString());
            DiscordBot.errorLoggingChannel.sendMessageFormat("%s There was an error reverting the update! (Code: BC-UpdRun:TrCa.117)",
                    BattlegroundsCore.getAresDiscordBot().getRolesByName("staff", true).get(0)).queue();
        }
        try {
            PluginUtil.load("BattlegroundsCore");
        } catch (Throwable throwable) {
            plugin.getLogger().severe("Reverted jar has errors, but it'll have to do");
            plugin.getLogger().severe(throwable.getCause().toString());
            DiscordBot.errorLoggingChannel.sendMessageFormat("%s Careful! The update was reverted but there are still errors! (Code: BC-UpdRun:TrCa.126)",
                    BattlegroundsCore.getAresDiscordBot().getRolesByName("staff", true).get(0)).queue();
        }
        plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY
                + "Successfully reverted to the previous version.");
    }

    private void deleteUpdateFile() {
        try {
            Files.delete(updateFile.toPath());
        } catch (IOException exception) {
            plugin.getLogger().severe("Unable to delete queued jarfile!");
            plugin.getLogger().severe(exception.getCause().toString());
            DiscordBot.errorLoggingChannel.sendMessageFormat("%s The queued jarfile was unable to be deleted! (Code: BC-UpdRun:TrCa.138)",
                    BattlegroundsCore.getAresDiscordBot().getRolesByName("staff", true).get(0)).queue();
        }
    }
}
