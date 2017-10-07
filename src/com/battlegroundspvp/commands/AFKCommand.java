package com.battlegroundspvp.commands;
/* Created by GamerBah on 8/15/2016 */


import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.enums.ColorBuilder;
import com.battlegroundspvp.utils.enums.EventSound;
import de.Herbystar.TTA.TTA_Methods;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AFKCommand implements CommandExecutor {
    private BattlegroundsCore plugin;

    public AFKCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (BattlegroundsCore.getAfk().contains(player.getUniqueId())) {
            BattlegroundsCore.getAfk().remove(player.getUniqueId());
            plugin.respawn(player);
            player.sendMessage(ChatColor.GRAY + "You are no longer AFK");
            EventSound.playSound(player, EventSound.CLICK);
            TTA_Methods.sendTitle(player, null, 0, 0, 0, null, 0, 0, 0);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        } else {
            plugin.respawn(player, player.getWorld().getSpawnLocation().add(0.5, 8, 0.5));
            player.getInventory().clear();
            player.sendMessage(ChatColor.GRAY + "You are now AFK");
            EventSound.playSound(player, EventSound.CLICK);
            TTA_Methods.sendTitle(player, ColorBuilder.AQUA.bold().create() + "You are AFK!",10, Integer.MAX_VALUE, 20,
                    ChatColor.YELLOW + "Move to start playing again!", 10, Integer.MAX_VALUE, 20);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false));
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> BattlegroundsCore.getAfk().add(player.getUniqueId()), 5L);
        }
        return false;
    }

}
