package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 8/29/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public SkullCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (!gameProfile.hasRank(Rank.MODERATOR)) {
            plugin.sendNoPermission(player);
            return true;
        }

        if (player.getGameMode() != GameMode.CREATIVE) {
            player.sendMessage(ChatColor.RED + "You must be in Gamemode 1 to use this!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (args.length == 0) {
            ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
            itemStack.setDurability((short) 3);
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            meta.setOwningPlayer(player);
            itemStack.setItemMeta(meta);

            player.getInventory().addItem(itemStack);
        }

        if (args.length == 1) {
            ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
            itemStack.setDurability((short) 3);
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            meta.setOwningPlayer(plugin.getServer().getPlayer(args[0]));
            itemStack.setItemMeta(meta);

            player.getInventory().addItem(itemStack);
        }

        if (args.length > 1) {
            plugin.sendIncorrectUsage(player, "/skull [name]");
            return true;
        }

        return false;
    }

}