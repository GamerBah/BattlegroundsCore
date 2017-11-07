package com.battlegroundspvp.utils.messages;
// AUTHOR: gamer_000 (12/28/2015)

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.Launcher;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

public class TextComponentMessages {

    private BattlegroundsCore plugin;

    public TextComponentMessages(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    public static BaseComponent launcherLocation(Player player, Launcher launcher) {
        int x = launcher.getLocation().getBlockX();
        int y = launcher.getLocation().getBlockY();
        int z = launcher.getLocation().getBlockZ();

        TextComponent textComponent = new TextComponent(ChatColor.GRAY + "- " + ChatColor.RED + x + ", " + y + ", " + z);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "ID: "
                + ChatColor.AQUA + launcher.getId() + "\n" + ChatColor.GRAY + "Strength: " + ChatColor.RED + launcher.getStrength()
                + "\n" + ChatColor.GREEN + "Click to teleport!").create()));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleport " + player.getName() + " " + x + " " + y + " " + z));

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

}
