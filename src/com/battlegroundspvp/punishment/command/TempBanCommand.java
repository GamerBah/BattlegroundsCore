package com.battlegroundspvp.punishment.command;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.gui.punishment.PunishmentMenus;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.gamerbah.inventorytoolkit.InventoryBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TempBanCommand implements CommandExecutor {
    private BattlegroundsCore plugin;

    public TempBanCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());

        if (gameProfile != null) {
            if (!gameProfile.hasRank(Rank.MODERATOR)) {
                plugin.sendNoPermission(player);
                return true;
            }

            if (args.length != 1) {
                plugin.sendIncorrectUsage(player, ChatColor.RED + "/tempban <player>");
                return true;
            }

            GameProfile targetProfile = GameProfileManager.getGameProfile(args[0]);

            if (targetProfile == null) {
                player.sendMessage(ChatColor.RED + "That player hasn't joined before!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            if (targetProfile == gameProfile) {
                player.sendMessage(ChatColor.RED + "You can't temp-ban yourself!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            if (targetProfile.hasRank(Rank.MODERATOR)) {
                player.sendMessage(ChatColor.RED + "You can't temp-ban that player!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            if (targetProfile.isBanned()) {
                BanCommand.sendErrorMessage(gameProfile, targetProfile);
                return true;
            }

            new InventoryBuilder(player, new PunishmentMenus().new TempBanMenu(player, targetProfile)).open();
            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);

            return true;
        }
        return false;
    }

}
