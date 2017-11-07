package com.battlegroundspvp.administration.commands;
/* Created by GamerBah on 8/18/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.administration.donations.DonationMessages;
import com.battlegroundspvp.administration.donations.Essence;
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
            return true;
        }

        if (args[0].equals("rank")) {
            if (args.length != 3) {
                plugin.getLogger().severe("Error Processing Donation Request: \"Rank\" donation set incorrectly filled");
                return true;
            }
            GameProfile gameProfile = plugin.getGameProfile(UUID.fromString(args[1]));
            if (gameProfile == null) {
                plugin.getLogger().severe("Error Processing Donation Request: Requested UUID not found in database!");
                return true;
            }

            for (Rank rank : Rank.values()) {
                if (!args[2].equalsIgnoreCase(rank.getName())) {
                    plugin.getLogger().severe("Error Processing Donation Request: Requested rank change invalid");
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
                return true;
            }
            GameProfile gameProfile = plugin.getGameProfile(UUID.fromString(args[1]));
            if (gameProfile == null) {
                plugin.getLogger().severe("Error Processing Donation Request: Requested UUID not found in database!");
                return true;
            }
            if (!args[2].matches("[1,3,6]")) {
                plugin.getLogger().severe("Error Processing Donation Request: Time value invalid");
                return true;
            }

            if (Integer.parseInt(args[3]) != 50 && Integer.parseInt(args[3]) != 100 && Integer.parseInt(args[3]) != 150) {
                plugin.getLogger().severe("Error Processing Donation Request: Invalid increase");
                return true;
            }
            Essence.Type eType = null;
            for (Essence.Type type : Essence.Type.values()) {
                if (Integer.parseInt(args[2]) == type.getDuration() && Integer.parseInt(args[3]) == type.getPercent()) {
                    eType = type;
                }
            }
            if (eType == null) {
                plugin.getLogger().severe("Fatal Error Processing Donation Request! Essence EffectType invalid!");
                return true;
            }

            gameProfile.getEssenceData().addEssence(eType, 1);
            plugin.getLogger().info("Success! Donation for Essence registered!");
            Player player = plugin.getServer().getPlayer(gameProfile.getUuid());
            if (player.isOnline()) {
                DonationMessages donationMessages = new DonationMessages(plugin);
                donationMessages.sendEssensePurchaseMessage(player, eType);
            }
        }
        return true;
    }
}
