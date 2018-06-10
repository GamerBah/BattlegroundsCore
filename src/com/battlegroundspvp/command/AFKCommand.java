package com.battlegroundspvp.command;
/* Created by GamerBah on 8/15/2016 */


import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.message.MessageBuilder;
import de.Herbystar.TTA.TTA_Methods;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
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
            EventSound.playSound(player, EventSound.CLICK);
            BattlegroundsCore.clearTitle(player);
        } else {
            plugin.respawn(player, (Location) plugin.getConfig().get("locations.afk"));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false));
            player.getInventory().clear();
            player.getInventory().setHeldItemSlot(4);
            EventSound.playSound(player, EventSound.CLICK);
            TTA_Methods.sendTitle(player, new MessageBuilder(ChatColor.AQUA).bold().create() + "You are AFK!", 10, 9999999, 20,
                    ChatColor.YELLOW + "Move to start playing again!", 10, 9999999, 20);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> BattlegroundsCore.getAfk().add(player.getUniqueId()), 5L);
        }
        return false;
    }

}
