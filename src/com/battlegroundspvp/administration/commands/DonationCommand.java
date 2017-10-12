package com.battlegroundspvp.administration.commands;
/* Created by GamerBah on 8/18/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.administration.donations.DonationMessages;
import com.battlegroundspvp.administration.donations.Essence;
import com.battlegroundspvp.utils.DiscordBot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DonationCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public DonationCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            plugin.sendNoPermission((Player) sender);
            return true;
        }

        if (args.length == 0) {
            plugin.getLogger().severe("Error Processing Donation Request: Invalid Arguments!");
            DiscordBot.errorLoggingChannel.sendMessage("Invalid Arguments while attempting to process new donation!\nIssue: 0 arguments").queue();
            return true;
        }

        if (args[0].equals("rank")) {
            if (args.length != 3) {
                plugin.getLogger().severe("Error Processing Donation Request: \"Rank\" donation set incorrectly filled");
                DiscordBot.errorLoggingChannel.sendMessage("Invalid Arguments while attempting to process new donation!\nIssue: \"Rank\" info not filled out correctly.").queue();
                return true;
            }
            GameProfile gameProfile = plugin.getGameProfile(UUID.fromString(args[1]));
            if (gameProfile == null) {
                plugin.getLogger().severe("Error Processing Donation Request: Requested UUID not found in database!");
                DiscordBot.errorLoggingChannel.sendMessage("Error while attempting to process new donation!\nIssue: Requested UUID was not found in server database.").queue();
                return true;
            }

            for (Rank rank : Rank.values()) {
                if (!args[2].equalsIgnoreCase(rank.getName())) {
                    plugin.getLogger().severe("Error Processing Donation Request: Requested rank change invalid");
                    DiscordBot.errorLoggingChannel.sendMessage("Error while attempting to process new donation!\nIssue: Requested Rank was invalid.").queue();
                    return true;
                }
            }
            plugin.getLogger().info("Success! Donation for Rank registered!");
            Rank rank = Rank.valueOf(args[2]);
            gameProfile.setRank(rank);
            //plugin.slackDonations.call(new SlackMessage(">>> _*" + gameProfile.getName() + "* purchased the *" + rank.getName() + "* rank!_"));
            Player player = plugin.getServer().getPlayer(gameProfile.getUuid());
            if (player.isOnline()) {
                DonationMessages donationMessages = new DonationMessages(plugin);
                donationMessages.sendRankPurchaseMessage(player, rank);
            }
            return true;
        }
        if (args[0].equals("essence")) {
            if (args.length != 4) {
                plugin.getLogger().severe("Error Processing Donation Request: \"Essence\" donation set incorrectly filled");
                DiscordBot.errorLoggingChannel.sendMessage("Invalid Arguments while attempting to process new donation!\nIssue: \"Essence\" info not filled out correctly.").queue();
                return true;
            }
            GameProfile gameProfile = plugin.getGameProfile(UUID.fromString(args[1]));
            if (gameProfile == null) {
                plugin.getLogger().severe("Error Processing Donation Request: Requested UUID not found in database!");
                DiscordBot.errorLoggingChannel.sendMessage("Error while attempting to process new donation!\nIssue: Requested UUID was not found in server database.").queue();
                return true;
            }
            if (!args[2].matches("[1,3,6]")) {
                plugin.getLogger().severe("Error Processing Donation Request: Time value invalid");
                DiscordBot.errorLoggingChannel.sendMessage("Error while attempting to process new donation!\nIssue: Requested Essence time was invalid.").queue();
                return true;
            }

            if (Integer.parseInt(args[3]) != 50 && Integer.parseInt(args[3]) != 100 && Integer.parseInt(args[3]) != 150) {
                plugin.getLogger().severe("Error Processing Donation Request: Invalid increase");
                DiscordBot.errorLoggingChannel.sendMessage("Error while attempting to process new donation!\nIssue: Requested Essence increase was invalid.").queue();
                return true;
            }
            Essence.Type eType = null;
            for (Essence.Type type : Essence.Type.values()) {
                if (Integer.parseInt(args[2]) == type.getDuration() && Integer.parseInt(args[3]) == type.getPercent()) {
                    eType = type;
                }
            }
            if (eType == null) {
                plugin.getLogger().severe("Fatal Error Processing Donation Request! Essence Type invalid!");
                DiscordBot.errorLoggingChannel.sendMessage("Error while attempting to process new donation!\nIssue: Essence Type was invalid.").queue();
                return true;
            }

            gameProfile.getEssenceData().addEssence(eType, 1);
            plugin.getLogger().info("Success! Donation for Essence registered!");
            Player player = plugin.getServer().getPlayer(gameProfile.getUuid());
            DiscordBot.staffChannel.sendMessage("@Staff ***" + player.getName() + "*** *purchased a* ***" + eType.getDuration() + " hour (+" + eType.getPercent() + "%) Battle Essence!***").queue();
            if (player.isOnline()) {
                DonationMessages donationMessages = new DonationMessages(plugin);
                donationMessages.sendEssensePurchaseMessage(player, eType);
            }
        }
        return true;
    }
}
