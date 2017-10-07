package com.battlegroundspvp.utils.messages;
// AUTHOR: gamer_000 (12/28/2015)

import com.battlegroundspvp.BattlegroundsCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TextComponentMessages {

    private BattlegroundsCore plugin;

    public TextComponentMessages(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    public static BaseComponent launcherLocation(Player player, Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        TextComponent textComponent = new TextComponent(ChatColor.GRAY + "- " + ChatColor.RED + x + ", " + y + ", " + z);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to teleport!").create()));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleport " + player.getName() + " " + x + " " + (y + 1) + " " + z));

        BaseComponent baseComponent = new TextComponent("   ");
        baseComponent.addExtra(textComponent);
        return baseComponent;
    }

    public TextComponent centerTextSpacesLeft() {
        return new TextComponent("                           ");
    }

    public TextComponent centerTextSpacesMiddle() {
        return new TextComponent("         ");
    }

    public TextComponent teamAcceptButton() {
        TextComponent message = new TextComponent("[ACCEPT]");
        message.setBold(true);
        message.setColor(ChatColor.GREEN);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team accept"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY
                + "Click to" + ChatColor.GREEN + " accept " + ChatColor.GRAY + "the request!").create()));
        return message;
    }

    public TextComponent teamDenyButton() {
        TextComponent message = new TextComponent("[DENY]");
        message.setBold(true);
        message.setColor(ChatColor.RED);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team deny"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY
                + "Click to" + ChatColor.RED + " deny " + ChatColor.GRAY + "the request!").create()));
        return message;
    }

    public TextComponent friendAcceptButton() {
        TextComponent message = new TextComponent("[ACCEPT]");
        message.setBold(true);
        message.setColor(ChatColor.GREEN);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY
                + "Click to" + ChatColor.GREEN + " accept " + ChatColor.GRAY + "the request!").create()));
        return message;
    }

    public TextComponent friendDeclineButton() {
        TextComponent message = new TextComponent("[DECLINE]");
        message.setBold(true);
        message.setColor(ChatColor.RED);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend decline"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY
                + "Click to" + ChatColor.RED + " decline " + ChatColor.GRAY + "the request!").create()));
        return message;
    }

    /*public BaseComponent[] playerStats(Player player) {
        GameProfile playerData = plugin.getGameProfile(player.getUniqueId());
        KDRatio kdRatio = new KDRatio(plugin);
        ChatColor ratioColor = kdRatio.getRatioColor(player);
        double ratio = ((double) playerData.getKitPvpData().getKills() / (double) playerData.getKitPvpData().getDeaths());
        ratio = Math.round(ratio * 100.00D) / 100.00D;
        if (playerData.getKitPvpData().getDeaths() == 0) {
            ratio = playerData.getKitPvpData().getKills();
        }

        return new ComponentBuilder(
                playerData.getRank().getColor() + "" + (playerData.hasRank(Rank.WARRIOR) ? ChatColor.BOLD + playerData.getRank().getName().toUpperCase() + " " : "")
                        + (playerData.hasRank(Rank.WARRIOR) ? ChatColor.WHITE : ChatColor.GRAY) + playerData.getName() + "\n\n"
                        + ChatColor.GRAY + "Kills: " + ChatColor.GREEN + playerData.getKitPvpData().getKills() + "\n"
                        + ChatColor.GRAY + "Deaths: " + ChatColor.RED + playerData.getKitPvpData().getDeaths() + "\n"
                        + ChatColor.GRAY + "K/D Ratio: " + ratioColor + ratio
                        + "\n\n" + ChatColor.YELLOW + "Click to open player options....").create();
    }

    public BaseComponent[] playerStats(OfflinePlayer player) {
        GameProfile playerData = plugin.getGameProfile(player.getUniqueId());
        KDRatio kdRatio = new KDRatio(plugin);
        ChatColor ratioColor = kdRatio.getRatioColor(player);
        double ratio = ((double) playerData.getKitPvpData().getKills() / (double) playerData.getKitPvpData().getDeaths());
        ratio = Math.round(ratio * 100.00D) / 100.00D;
        if (playerData.getKitPvpData().getDeaths() == 0) {
            ratio = playerData.getKitPvpData().getKills();
        }
        return new ComponentBuilder(
                playerData.getRank().getColor() + "" + (playerData.hasRank(Rank.WARRIOR) ? ChatColor.BOLD + playerData.getRank().getName().toUpperCase() + " " : "")
                        + (playerData.hasRank(Rank.WARRIOR) ? ChatColor.WHITE : ChatColor.GRAY) + playerData.getName() + "\n"
                        + ChatColor.GRAY + "Was last online " + Time.toString(Time.timeDifference(playerData.getLastOnline()), true) + " ago\n\n"
                        + ChatColor.GRAY + "Kills: " + ChatColor.GREEN + playerData.getKitPvpData().getKills() + "\n"
                        + ChatColor.GRAY + "Deaths: " + ChatColor.RED + playerData.getKitPvpData().getDeaths() + "\n"
                        + ChatColor.GRAY + "K/D Ratio: " + ratioColor + ratio).create();
    }*/

}
