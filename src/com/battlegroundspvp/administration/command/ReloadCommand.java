package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 9/7/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.manager.UpdateManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public ReloadCommand(BattlegroundsCore plugin) {
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
            if (!gameProfile.hasRank(Rank.OWNER)) {
                plugin.sendNoPermission(player);
            } else {
                if (args.length != 1) {
                    plugin.sendIncorrectUsage(player, "/reload <server | plugin>");
                    return true;
                }
                if (args[0].equalsIgnoreCase("server")) {
                    UpdateManager.update(UpdateManager.UpdateType.SERVER);
                }
                if (args[0].equalsIgnoreCase("plugin")) {
                    UpdateManager.update(UpdateManager.UpdateType.PLUGIN);
                }
            }
            return true;
        }
        return false;
    }

}
