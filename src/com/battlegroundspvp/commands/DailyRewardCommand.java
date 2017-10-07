package com.battlegroundspvp.commands;
/* Created by GamerBah on 8/28/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.utils.enums.ColorBuilder;
import com.battlegroundspvp.utils.enums.EventSound;
import de.Herbystar.TTA.TTA_Methods;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class DailyRewardCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public DailyRewardCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 0) {
            plugin.sendIncorrectUsage(player, "/dailyreward");
            return true;
        }

        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (gameProfile.isDailyReward()) {
            player.sendMessage(ChatColor.RED + "You've already claimed your reward! Come back tomorrow for another one!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        gameProfile.setDailyReward(true);
        gameProfile.setDailyRewardLast(LocalDateTime.now());
        /*ScoreboardListener scoreboardListener = new ScoreboardListener(plugin);
        scoreboardListener.updateScoreboardSouls(player, 50);
        scoreboardListener.updateScoreboardCoins(player, 10);*/

        TTA_Methods.sendTitle(player, ColorBuilder.GREEN.bold().create() + "Daily Reward Claimed!",5, 60, 20,
                ChatColor.AQUA + "+50 Souls   " + ChatColor.LIGHT_PURPLE + "+10 Battle Coins", 5, 60, 20);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.2F, 1F);

        return false;
    }

}