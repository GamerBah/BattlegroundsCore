package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 8/7/2016 */


import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankCommand implements CommandExecutor, TabCompleter {

    private BattlegroundsCore plugin;

    public RankCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());
            if (gameProfile != null) {
                if (!gameProfile.hasRank(Rank.OWNER) && !player.isOp()) {
                    plugin.sendNoPermission(player);
                    return true;
                }
            }
        }

        if (args.length != 2) {
            if (sender instanceof Player) plugin.sendIncorrectUsage((Player) sender, "/rank <player> <rank>");
            else sender.sendMessage("/rank <player> <rank>");
            return true;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);

            if (target == null) {
                if (sender instanceof Player) plugin.sendNoResults((Player) sender, "find that player");
                else sender.sendMessage(ChatColor.RED + "That player doesn't exist!");
                return;
            }

            for (Rank rank : Rank.values()) {
                if (rank.getName().equalsIgnoreCase(args[1])) {
                    GameProfile gameProfile = GameProfileManager.getGameProfile(target.getUniqueId());
                    //ScoreboardListener scoreboardListener = new ScoreboardListener(plugin);
                    if (gameProfile != null) {
                        if (gameProfile.getRank() == rank) {
                            sender.sendMessage(ChatColor.RED + gameProfile.getName() + " already has that rank");
                            if (sender instanceof Player) EventSound.playSound((Player) sender, EventSound.ACTION_FAIL);
                            return;
                        }
                        gameProfile.setRank(rank);
                        if (target.isOnline()) {
                            for (BattleModule module : BattleModuleLoader.modules.keySet())
                                module.updateScoreboardRank((Player) target, rank);
                            ((Player) target).setPlayerListName((gameProfile.hasRank(Rank.WARRIOR) ? gameProfile.getRank().getColor().create() + gameProfile.getRank().getName().toUpperCase() + " " : "")
                                    + (gameProfile.hasRank(Rank.WARRIOR) ? ChatColor.WHITE : ChatColor.GRAY) + target.getName());
                            ((Player) target).sendMessage(ChatColor.GRAY + "Your rank was changed to " + rank.getColor().create() + WordUtils.capitalizeFully(rank.toString())
                                    + (sender instanceof Player ? ChatColor.GRAY + " by " + sender.getName() : ""));
                            EventSound.playSound((Player) target, EventSound.ACTION_SUCCESS);
                        }
                        if (!gameProfile.hasRank(Rank.MODERATOR))
                            gameProfile.getPlayerSettings().setStealthyJoin(false);
                        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Success! " + ChatColor.GRAY + target.getName() + "'s rank was changed to "
                                + rank.getColor().create() + WordUtils.capitalizeFully(rank.toString()));
                        if (sender instanceof Player) EventSound.playSound((Player) sender, EventSound.ACTION_SUCCESS);
                        return;
                    } else {
                        if (sender instanceof Player)
                            plugin.sendNoResults((Player) sender, "find data for that player");
                        else sender.sendMessage(ChatColor.RED + "That player doesn't exist!");
                        return;
                    }
                }
            }

            List<String> ranks = new ArrayList<>();
            for (Rank rank : Rank.values()) {
                ranks.add(rank.getName());
            }
            sender.sendMessage(ChatColor.RED + "That rank doesn't exist! Try one of these: " + WordUtils.capitalizeFully(ranks.toString().replace("[", "").replace("]", "")));
            if (sender instanceof Player) EventSound.playSound((Player) sender, EventSound.ACTION_FAIL);
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rank")) {
            if (args.length == 2) {
                ArrayList<String> ranks = new ArrayList<>();
                if (!args[1].equals("")) {
                    for (Rank rank : Rank.values()) {
                        if (rank.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            ranks.add(rank.getName());
                        }
                    }
                } else {
                    for (Rank rank : Rank.values()) {
                        ranks.add(rank.getName());
                    }
                }
                Collections.sort(ranks);
                return ranks;
            }
        }
        return null;
    }

}
