package com.battlegroundspvp.event;
/* Created by GamerBah on 8/9/2016 */

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.command.FreezeCommand;
import com.battlegroundspvp.util.message.MessageBuilder;
import com.gamerbah.inventorytoolkit.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerRespawn implements Listener {

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED)
            event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.teleport(event.getRespawnLocation());
        player.setHealth(20F);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setFlying(false);
        player.setAllowFlight(false);

        BattlegroundsCore.getFallDmg().add(player);

        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());

        for (BattleModule module : BattleModuleLoader.modules.keySet())
            module.onPlayerRespawn(event);

        for (Player players : Bukkit.getServer().getOnlinePlayers())
            players.showPlayer(BattlegroundsCore.getInstance(), player);

        if (FreezeCommand.frozenPlayers.contains(player) || FreezeCommand.frozen) {
            player.setWalkSpeed(0F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -100, true, false));
            player.setFoodLevel(6);
            player.setSaturation(0);
        }

        ItemStack head = new ItemBuilder(Material.SKULL_ITEM)
                .name(new MessageBuilder(ChatColor.YELLOW).bold().create() + "Player Profile" + ChatColor.GRAY + " (Right-Click)")
                .lore(ChatColor.GRAY + "View your unlocked cosmetics,").lore(ChatColor.GRAY + "achievements, and more!")
                .durability(3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        head.setItemMeta(meta);

        player.getInventory().setItem(7, head);

        player.getInventory().setItem(8, new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .name(new MessageBuilder(ChatColor.RED).bold().create() + "Settings" + ChatColor.GRAY + " (Right-Click)")
                .lore(ChatColor.GRAY + "Change your personal settings!"));

    }
}
