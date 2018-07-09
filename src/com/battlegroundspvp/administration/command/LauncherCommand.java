package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 5/19/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.Launcher;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.message.MessageBuilder;
import com.battlegroundspvp.util.message.TextComponentMessages;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LauncherCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public LauncherCommand(BattlegroundsCore plugin) {
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
                return true;
            }

            if (args.length == 0) {
                plugin.sendIncorrectUsage(player, "/launcher <add/remove/list/strength>");
                return true;
            }

            Block block = player.getLocation().getBlock();

            if (args[0].equalsIgnoreCase("add")) {
                if (args.length != 3) {
                    plugin.sendIncorrectUsage(player, "/launcher add <-u/-f> <strength>");
                    return true;
                }

                if (block.getRelative(BlockFace.DOWN).getType().equals(Material.WATER)
                        || block.getRelative(BlockFace.DOWN).getType().equals(Material.STATIONARY_WATER)
                        || block.getRelative(BlockFace.DOWN).getType().equals(Material.LAVA)
                        || block.getRelative(BlockFace.DOWN).getType().equals(Material.STATIONARY_LAVA)
                        || block.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)
                        || !block.getType().isSolid()) {
                    player.sendMessage(ChatColor.RED + "Launchers can only be placed in safe places!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return true;
                }

                for (Launcher launcher : BattlegroundsCore.getLaunchers()) {
                    if (launcher.getLocation().hashCode() == block.getLocation().hashCode()) {
                        player.sendMessage(ChatColor.RED + "A launcher already exists here!");
                        EventSound.playSound(player, EventSound.ACTION_FAIL);
                        return true;
                    }
                }

                if (!args[2].matches("[0-9]+")) {
                    player.sendMessage(ChatColor.RED + "The strength can only be a number!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return true;
                }

                if (args[1].equalsIgnoreCase("-u")) {
                    Location location = block.getLocation();
                    location.setPitch(player.getLocation().getPitch());
                    location.setYaw(player.getLocation().getYaw());
                    Launcher launcher = new Launcher(location, Launcher.Type.UPWARD, Integer.parseInt(args[2]));
                    BattlegroundsCore.getLaunchers().add(launcher);
                    player.sendMessage(ChatColor.GREEN + "Added an upwards launcher" + ChatColor.GRAY
                            + " at " + ChatColor.AQUA + block.getX() + ", " + block.getY() + ", " + block.getZ() + ChatColor.GRAY + " with an ID of " + ChatColor.AQUA + launcher.getId());
                    return true;
                } else if (args[1].equalsIgnoreCase("-f")) {
                    Location location = block.getLocation();
                    location.setPitch(player.getLocation().getPitch());
                    location.setYaw(player.getLocation().getYaw());
                    Launcher launcher = new Launcher(location, Launcher.Type.FORWARD, Integer.parseInt(args[2]));
                    BattlegroundsCore.getLaunchers().add(launcher);
                    player.sendMessage(ChatColor.GREEN + "Added a forwards launcher" + ChatColor.GRAY
                            + " at " + ChatColor.AQUA + block.getX() + ", " + block.getY() + ", " + block.getZ() + ChatColor.GRAY + " with an ID of " + ChatColor.AQUA + launcher.getId());
                    return true;
                } else {
                    plugin.sendIncorrectUsage(player, "/launcher add <-u/-f> <strength>");
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("remove")) {
                if (args.length > 2) {
                    plugin.sendIncorrectUsage(player, "/launcher remove [id]");
                    return true;
                }

                if (args.length == 1) {
                    Launcher removal = null;
                    for (Launcher launcher : BattlegroundsCore.getLaunchers())
                        if (launcher.getLocation().hashCode() == block.getLocation().hashCode())
                            removal = launcher;

                    if (removal == null) {
                        plugin.sendNoResults(player, "find a launcher at this location!");
                        return true;
                    }
                    BattlegroundsCore.getLaunchers().remove(removal);
                    player.sendMessage(ChatColor.GREEN + "Successfully removed that launcher " + ChatColor.GRAY + "(ID: " + ChatColor.AQUA + removal.getId() + ChatColor.GRAY + ")");
                    return true;
                }

                if (!args[1].matches("[0-9]+")) {
                    player.sendMessage(ChatColor.RED + "The ID can only be a number!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return true;
                }

                Launcher removal = null;
                for (Launcher launcher : BattlegroundsCore.getLaunchers())
                    if (launcher.getId() == Integer.parseInt(args[1]))
                        removal = launcher;

                if (removal == null) {
                    plugin.sendNoResults(player, "find a launcher with that ID!");
                    player.sendMessage(ChatColor.GRAY + "(Hint: Use " + ChatColor.RED + "/launcher list" + ChatColor.GRAY + " to easily find launcher ID's)");
                    return true;
                }

                BattlegroundsCore.getLaunchers().remove(removal);
                player.sendMessage(ChatColor.GREEN + "Successfully removed that launcher " + ChatColor.GRAY + "(ID: " + ChatColor.AQUA + removal.getId() + ChatColor.GRAY + ")");
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (args.length != 2) {
                    plugin.sendIncorrectUsage(player, "/launcher list [-u/-f]");
                    return true;
                }
                if (args[1].equalsIgnoreCase("-f")) {
                    player.sendMessage(new MessageBuilder(ChatColor.AQUA).bold().create() + "Forwards Launchers:");
                    if (BattlegroundsCore.getLaunchers().size() == 0)
                        player.sendMessage(ChatColor.GRAY + "   None");
                    BattlegroundsCore.getLaunchers().stream().filter(launcher -> launcher.getType() == Launcher.Type.FORWARD).forEach(launcher ->
                            player.spigot().sendMessage(TextComponentMessages.launcherLocation(player, launcher)));
                } else if (args[1].equalsIgnoreCase("-u")) {
                    player.sendMessage(new MessageBuilder(ChatColor.AQUA).bold().create() + "Upwards Launchers:");
                    if (BattlegroundsCore.getLaunchers().size() == 0)
                        player.sendMessage(ChatColor.GRAY + "   None");
                    BattlegroundsCore.getLaunchers().stream().filter(launcher -> launcher.getType() == Launcher.Type.UPWARD).forEach(launcher ->
                            player.spigot().sendMessage(TextComponentMessages.launcherLocation(player, launcher)));
                    return true;
                } else {
                    plugin.sendIncorrectUsage(player, "/launcher list [-u/-f]");
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("strength")) {
                if (args.length > 3 || args.length == 1) {
                    plugin.sendIncorrectUsage(player, "/launcher strength <id> [number]");
                    return true;
                }

                if (args.length == 2) {
                    if (!args[1].matches("[0-9]+")) {
                        player.sendMessage(ChatColor.RED + "The ID can only be a number!");
                        EventSound.playSound(player, EventSound.ACTION_FAIL);
                        return true;
                    }
                    Launcher find = null;
                    for (Launcher launcher : BattlegroundsCore.getLaunchers())
                        if (launcher.getId() == Integer.parseInt(args[1]))
                            find = launcher;

                    if (find == null) {
                        plugin.sendNoResults(player, "find a launcher with that ID!");
                        player.sendMessage(ChatColor.GRAY + "(Hint: Use " + ChatColor.RED + "/launcher list" + ChatColor.GRAY + " to easily find launcher ID's)");
                        return true;
                    }
                    player.sendMessage(ChatColor.GRAY + "The " + find.getType().toString().toLowerCase() + " strength of launcher " + ChatColor.AQUA + find.getId()
                            + ChatColor.GRAY + " is " + ChatColor.AQUA + find.getStrength());
                    return true;
                }

                if (!args[1].matches("[0-9]+")) {
                    player.sendMessage(ChatColor.RED + "The ID can only be a number!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return true;
                }
                Launcher edit = null;
                for (Launcher launcher : BattlegroundsCore.getLaunchers())
                    if (launcher.getId() == Integer.parseInt(args[1]))
                        edit = launcher;

                if (edit == null) {
                    plugin.sendNoResults(player, "find a launcher with that ID!");
                    player.sendMessage(ChatColor.GRAY + "(Hint: Use " + ChatColor.RED + "/launcher list" + ChatColor.GRAY + " to easily find launcher ID's)");
                    return true;
                }

                if (!args[2].matches("[0-9]+")) {
                    player.sendMessage(ChatColor.RED + "The strength can only be a number!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return true;
                }

                edit.setStrength(Integer.parseInt(args[2]));
                player.sendMessage(ChatColor.GREEN + "Changed the " + edit.getType().toString().toLowerCase() + " strength of launcher " + ChatColor.AQUA + edit.getId()
                        + ChatColor.GRAY + " to " + ChatColor.AQUA + edit.getStrength());
                return true;
            }

            return true;
        }
        return false;
    }

}