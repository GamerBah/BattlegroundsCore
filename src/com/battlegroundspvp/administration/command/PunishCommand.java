package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 8/25/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.gui.punishment.PunishmentMenus;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.gamerbah.inventorytoolkit.InventoryBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public PunishCommand(BattlegroundsCore plugin) {
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
            if (!gameProfile.hasRank(Rank.HELPER)) {
                plugin.sendNoPermission(player);
                return true;
            }

            if (args.length == 0) {
                new InventoryBuilder(player, new PunishmentMenus().new PunishMenu(player)).open();
                EventSound.playSound(player, EventSound.INVENTORY_OPEN_MENU);
                return true;
            }

            if (args.length > 1) {
                plugin.sendIncorrectUsage(player, "/punish [player]");
                return true;
            }

            GameProfile targetProfile = GameProfileManager.getGameProfile(args[0]);

            if (targetProfile == null) {
                player.sendMessage(ChatColor.RED + "That player has never joined before!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            OfflinePlayer target = plugin.getServer().getOfflinePlayer(targetProfile.getUuid());
            if (target == null) {
                player.sendMessage(ChatColor.RED + "That player isn't online!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }
            EventSound.playSound(player, EventSound.INVENTORY_OPEN_MENU);
            new InventoryBuilder(player, new PunishmentMenus().new PunishMenu(player)).open();

            return true;
        }
        return false;
    }

}