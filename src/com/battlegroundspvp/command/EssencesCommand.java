package com.battlegroundspvp.command;
/* Created by GamerBah on 8/18/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.manager.GameProfileManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EssencesCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public EssencesCommand(BattlegroundsCore plugin) {
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
            if (args.length != 0) {
                plugin.sendIncorrectUsage(player, "/essences");
                return true;
            }

            int amount = gameProfile.getTotalEssenceAmount();
            if (amount == 0) {
                player.sendMessage(ChatColor.RED + "You don't have any Battle Essences!");
                player.sendMessage(ChatColor.YELLOW + "Buy one from our store! " + ChatColor.GOLD + "battlegroundspvp.com/store");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            //EssenceMenu essenceMenu = new EssenceMenu(plugin);
            //essenceMenu.openInventory(player);

            return true;
        }
        return false;
    }

}
