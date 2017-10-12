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
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());
        KDRatio kdRatio = new KDRatio(plugin);
        ChatColor ratioColor = kdRatio.getRatioColor(player);
        double ratio = ((double) gameProfile.getKitPvpData().getKills() / (double) gameProfile.getKitPvpData().getDeaths());
        ratio = Math.round(ratio * 100.00D) / 100.00D;
        if (gameProfile.getKitPvpData().getDeaths() == 0) {
            ratio = gameProfile.getKitPvpData().getKills();
        }

        return new ComponentBuilder(
                gameProfile.getRank().getColor().create() + "" + (gameProfile.hasRank(Rank.WARRIOR) ? ChatColor.BOLD + gameProfile.getRank().getName().toUpperCase() + " " : "")
                        + (gameProfile.hasRank(Rank.WARRIOR) ? ChatColor.WHITE : ChatColor.GRAY) + gameProfile.getName() + "\n\n"
                        + ChatColor.GRAY + "Kills: " + ChatColor.GREEN + gameProfile.getKitPvpData().getKills() + "\n"
                        + ChatColor.GRAY + "Deaths: " + ChatColor.RED + gameProfile.getKitPvpData().getDeaths() + "\n"
                        + ChatColor.GRAY + "K/D Ratio: " + ratioColor + ratio
                        + "\n\n" + ChatColor.YELLOW + "Click to open player options....").create();
    }

    public BaseComponent[] playerStats(OfflinePlayer player) {
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());
        KDRatio kdRatio = new KDRatio(plugin);
        ChatColor ratioColor = kdRatio.getRatioColor(player);
        double ratio = ((double) gameProfile.getKitPvpData().getKills() / (double) gameProfile.getKitPvpData().getDeaths());
        ratio = Math.round(ratio * 100.00D) / 100.00D;
        if (gameProfile.getKitPvpData().getDeaths() == 0) {
            ratio = gameProfile.getKitPvpData().getKills();
        }
        return new ComponentBuilder(
                gameProfile.getRank().getColor().create() + "" + (gameProfile.hasRank(Rank.WARRIOR) ? ChatColor.BOLD + gameProfile.getRank().getName().toUpperCase() + " " : "")
                        + (gameProfile.hasRank(Rank.WARRIOR) ? ChatColor.WHITE : ChatColor.GRAY) + gameProfile.getName() + "\n"
                        + ChatColor.GRAY + "Was last online " + Time.toString(Time.timeDifference(gameProfile.getLastOnline()), true) + " ago\n\n"
                        + ChatColor.GRAY + "Kills: " + ChatColor.GREEN + gameProfile.getKitPvpData().getKills() + "\n"
                        + ChatColor.GRAY + "Deaths: " + ChatColor.RED + gameProfile.getKitPvpData().getDeaths() + "\n"
                        + ChatColor.GRAY + "K/D Ratio: " + ratioColor + ratio).create();
    }*/

}
