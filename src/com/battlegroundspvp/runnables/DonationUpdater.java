package com.battlegroundspvp.runnables;
/* Created by GamerBah on 8/19/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.donations.Essence;
import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.enums.Time;
import de.Herbystar.TTA.TTA_Methods;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public class DonationUpdater implements Runnable {

    private BattlegroundsCore plugin;

    public DonationUpdater(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Essence essence = new Essence();
        if (!plugin.getConfig().getBoolean("essenceActive")) {
            return;
        }
        int timeRemaining = plugin.getConfig().getInt("essenceTimeRemaining");

        if (timeRemaining > 0) {
            if (!plugin.getConfig().getBoolean("developmentMode")) {
                if (!UpdateRunnable.updating) {
                    plugin.getConfig().set("essenceTimeRemaining", timeRemaining - 1);
                    plugin.saveConfig();
                }
                long milliseconds = timeRemaining * 1000;
                double completion = ((double) milliseconds / (plugin.getConfig().getInt("essenceTime") * 60 * 60 * 1000));
                Essence.Type essenceType = Essence.Type.ONE_HOUR_50_PERCENT;
                for (Essence.Type type : Essence.Type.values()) {
                    if (plugin.getConfig().getInt("essenceTime") == type.getDuration() && plugin.getConfig().getInt("essenceIncrease") == type.getPercent()) {
                        essenceType = type;
                    }
                }
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    TTA_Methods.removeBossBar(player);
                    TTA_Methods.createBossBar(player, ChatColor.RED + Time.toString(milliseconds, true) + ChatColor.GRAY + " remaining in "
                                    + ChatColor.RED + plugin.getConfig().getString("essenceOwner") + ChatColor.GRAY + "'s Battle Essence "
                                    + essenceType.getChatColor() + "(+" + essenceType.getPercent() + "%)",
                            completion, BarStyle.SOLID, essenceType.getBarColor(), BarFlag.CREATE_FOG, true);
                }
            }
        } else {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                TTA_Methods.removeBossBar(player);
                player.sendMessage(new ColorBuilder(ChatColor.GOLD).bold().create() + "" + (plugin.getConfig().get("essenceOwner").equals(player.getName())
                        ? "Your" : plugin.getConfig().get("essenceOwner") + "'s") + " Battle Essence has ended!");
                if (!player.getName().equals(plugin.getConfig().get("essenceOwner"))) {
                    player.sendMessage(ChatColor.GREEN + "Buy your own! " + ChatColor.YELLOW + "battlegroundspvp.com/store");
                }
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.3F, 0.9F);
            }
            essence.removeActiveEssence();
        }
    }

}
