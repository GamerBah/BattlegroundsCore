package com.battlegroundspvp.gui.punishment;

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.command.ReportCommand;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.message.MessageBuilder;
import com.gamerbah.inventorytoolkit.GameInventory;
import com.gamerbah.inventorytoolkit.ItemBuilder;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ReportMenu extends GameInventory {

    private BattlegroundsCore plugin = BattlegroundsCore.getInstance();
    @Getter private String message;
    @Getter private List<ItemBuilder> selected;

    public ReportMenu(Player player, GameProfile targetProfile) {
        super("Reporting: " + targetProfile.getName(), null /*TODO: PLAYER OPTIONS MENU*/);

        ItemBuilder killAura = new ItemBuilder(Material.BOOK).name(ChatColor.RED + "Kill Aura / Forcefield")
                .lore(ChatColor.AQUA + "Click to add to your report!").lore(" ")
                .lore(ChatColor.GRAY + "If a player can hit you when they aren't")
                .lore(ChatColor.GRAY + "looking at you, select this.").lore(" ")
                .lore(ChatColor.WHITE + "Difficulty to Detect: " + ChatColor.YELLOW + "■■ □ □");

        //killAura.onClick(new ClickEvent(ClickEvent.ClickType.ANY, new Action(() -> CustomCall.addListItem(getSelected(), killAura))));

        ItemBuilder regen = new ItemBuilder(Material.BOOK).name(ChatColor.RED + "Regen Hacks")
                .lore(ChatColor.AQUA + "Click to add to your report!").lore(" ")
                .lore(ChatColor.GRAY + "A player with regeneration hacks can be")
                .lore(ChatColor.GRAY + "detected by looking at the hearts above")
                .lore(ChatColor.GRAY + "their head. If the number is constantly around")
                .lore(ChatColor.GRAY + "20, they're probably cheating!").lore(" ")
                .lore(ChatColor.WHITE + "Difficulty to Detect: " + ChatColor.GOLD + "■■■ □");

        ItemBuilder speed = new ItemBuilder(Material.BOOK).name(ChatColor.RED + "Speed Hacks")
                .lore(ChatColor.AQUA + "Click to add to your report!").lore(" ")
                .lore(ChatColor.GRAY + "If a player seems to be running")
                .lore(ChatColor.GRAY + "faster than they should, select this.").lore(" ")
                .lore(ChatColor.WHITE + "Difficulty to Detect: " + ChatColor.GOLD + "■■■ □");

        ItemBuilder autoClick = new ItemBuilder(Material.BOOK).name(ChatColor.RED + "Auto Click")
                .lore(ChatColor.AQUA + "Click to add to your report!").lore(" ")
                .lore(ChatColor.GRAY + "Players with an Auto Click will continue")
                .lore(ChatColor.GRAY + "to swing their sword even after they get a kill.").lore(" ")
                .lore(ChatColor.WHITE + "Difficulty to Detect: " + ChatColor.GOLD + "■■■ □");

        ItemBuilder fly = new ItemBuilder(Material.BOOK).name(ChatColor.RED + "Fly Hacks")
                .lore(ChatColor.AQUA + "Click to add to your report!").lore(" ")
                .lore(ChatColor.GRAY + "If a player is flying around like they")
                .lore(ChatColor.GRAY + "are in creative mode, select this.").lore(" ")
                .lore(ChatColor.WHITE + "Difficulty to Detect: " + ChatColor.GREEN + "■ □ □ □");

        ItemBuilder autoCrit = new ItemBuilder(Material.BOOK).name(ChatColor.RED + "Auto Critical Hit")
                .lore(ChatColor.AQUA + "Click to add to your report!").lore(" ")
                .lore(ChatColor.GRAY + "Very hard to detect! Players with an")
                .lore(ChatColor.GRAY + "Auto Crit can be seen jumping ever")
                .lore(ChatColor.GRAY + "so slightly when they swing!").lore(" ")
                .lore(ChatColor.WHITE + "Difficulty to Detect: " + ChatColor.RED + "■■■■");

        ItemBuilder antiKB = new ItemBuilder(Material.BOOK).name(ChatColor.RED + "Anti-Knockback")
                .lore(ChatColor.AQUA + "Click to add to your report!").lore(" ")
                .lore(ChatColor.GRAY + "This one is self-explanatory.").lore(" ")
                .lore(ChatColor.RED + "NOTE: Lag can sometimes cause issues with")
                .lore(ChatColor.RED + "knockback. Be sure to check the player's ping!").lore(" ")
                .lore(ChatColor.WHITE + "Difficulty to Detect: " + ChatColor.YELLOW + "■■ □ □");

        getInventory().setItem(32, new ItemBuilder(Material.WOOL)
                .name(ChatColor.RED + "" + ChatColor.BOLD + "CANCEL REPORT")
                .durability(14));
        //.onClick(new ClickEvent(ClickEvent.ClickType.ANY, new Action(this.getClass(), "cancel", player))));

        ItemBuilder wool = new ItemBuilder(Material.WOOL)
                .name(ChatColor.GREEN + "" + ChatColor.BOLD + "ACCEPT & SEND")
                .lore(ChatColor.GRAY + "Reporting: " + ChatColor.YELLOW + targetProfile.getName())
                .durability(5);

        if (message != null) {
            //wool.lore(ChatColor.GRAY + "For: " + ChatColor.GOLD + message).onClick(new ClickEvent(ClickEvent.ClickType.ANY, new Action(player, this.getClass(), "report", player, targetProfile, getMessage())));
        } else {
           // wool.lore(ChatColor.GRAY + "For: " + ChatColor.RED + "Nothing here yet! Click the books").lore(ChatColor.RED + "        to add reasons to your report!");
        }
        wool.lore(" ");
        wool.lore(ChatColor.DARK_RED + "" + ChatColor.BOLD + "NOTICE:");
        wool.lore(ChatColor.RED + "Please do not falsely report a player");
        wool.lore(ChatColor.RED + "with the intention of getting them banned.");
        wool.lore(ChatColor.RED + "BattlegroundsCore Staff do not take this lightly,");
        wool.lore(ChatColor.RED + "and will deal with the situation if needed.");

        getInventory().setItem(10, killAura);
        getInventory().setItem(11, regen);
        getInventory().setItem(12, speed);
        getInventory().setItem(13, autoClick);
        getInventory().setItem(14, fly);
        getInventory().setItem(15, autoCrit);
        getInventory().setItem(16, antiKB);
        getInventory().setItem(30, wool);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void cancel(Player player) {
        player.closeInventory();
        player.sendMessage(ChatColor.RED + "Report cancelled.");
        EventSound.playSound(player, EventSound.ACTION_FAIL);
        this.message = null;
        this.selected = null;
        this.plugin = null;
    }

    public void setSelected(Inventory inventory, ItemStack itemStack) {
        ItemBuilder item = new ItemBuilder(Material.ENCHANTED_BOOK).name(ChatColor.GREEN + ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()));
        for (String lore : itemStack.getItemMeta().getLore())
            item.lore(lore);
        item.lore(" ").lore(new MessageBuilder(ChatColor.GREEN).bold().create() + "SELECTED").lore(ChatColor.AQUA + "Click to remove!");
    }

    public void setUnselected(Inventory inventory, ItemStack itemStack) {
        String name = itemStack.getItemMeta().getDisplayName()
                .replace(ChatColor.GREEN + "", ChatColor.RED + "");
        List<String> lore = itemStack.getItemMeta().getLore();
        int size = lore.size();
        lore.remove(size - 1);
        lore.remove(size - 2);
        lore.set(0, ChatColor.AQUA + "Click to add to your report!");
        ItemStack newItem = new ItemBuilder(Material.BOOK).name(name);
        ItemMeta im = newItem.getItemMeta();
        im.setLore(lore);
        newItem.setItemMeta(im);
    }

    public void setWool(Inventory inventory, Player target, String message) {
    }

    public void report(Player player, GameProfile targetProfile, String message) {
        player.sendMessage(ChatColor.GREEN + "You have successfully reported "
                + ChatColor.DARK_AQUA + targetProfile.getName() + ChatColor.GREEN + "!");

        plugin.getServer().getOnlinePlayers().stream().filter(staff -> plugin.getGameProfile(staff.getUniqueId())
                .hasRank(Rank.HELPER)).forEach(staff -> staff.sendMessage(ChatColor.RED + player.getName()
                + " has reported " + ChatColor.BOLD + targetProfile.getName() + ChatColor.RED + " for: " + message + "!"));

        plugin.getServer().getOnlinePlayers().stream().filter(staff -> plugin.getGameProfile(staff.getUniqueId())
                .hasRank(Rank.HELPER)).forEach(staff -> staff.playSound(staff.getLocation(), Sound.BLOCK_NOTE_HARP, 2, 2));

        //plugin.slackReports.displayNMS(new SlackMessage(">>>*" + player.getName() + "* _has reported_ *" + targetProfile.getName() + "* _for:_ " + message));

        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);

        ReportCommand.getCooldown().put(player.getUniqueId(), 60);
        ReportCommand.getReportBuilders().remove(player.getUniqueId());
        ReportCommand.getReportArray().remove(player.getUniqueId());

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (ReportCommand.getCooldown().get(player.getUniqueId()) >= 0) {
                    ReportCommand.getCooldown().replace(player.getUniqueId(), ReportCommand.getCooldown().get(player.getUniqueId()) - 1);
                }

                if (ReportCommand.getCooldown().get(player.getUniqueId()) == 0) {
                    ReportCommand.getCooldown().remove(player.getUniqueId());
                    EventSound.playSound(player, EventSound.CLICK);
                    player.sendMessage(ChatColor.GREEN + "You are now able to report another player!");
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(plugin, 1L, 20L).getTaskId();
    }

}
