package com.battlegroundspvp.runnables;
/* Created by GamerBah on 8/19/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.donations.Essence;
import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.enums.Time;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class DonationUpdater implements Runnable {

    private BattlegroundsCore plugin;
    public static BossBar essenceBar = null;

    public DonationUpdater(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        Essence essence = new Essence();
        if (!plugin.getConfig().getBoolean("essence.active")) {
            return;
        }

        int timeRemaining = plugin.getConfig().getInt("essence.timeRemaining");
        if (timeRemaining > 0) {
            if (!plugin.getConfig().getBoolean("developmentMode")) {
                if (!UpdateRunnable.updating) {
                    plugin.getConfig().set("essence.timeRemaining", timeRemaining - 1);
                    plugin.saveConfig();
                }
            }

            if (essenceBar != null) {
                long milliseconds = timeRemaining * 1000;
                double completion = (double) milliseconds / (timeRemaining * 60 * 60 * 1000);
                Essence.Type essenceType = Essence.fromId(plugin.getConfig().getInt("essence.id"));
                essenceBar.setProgress(completion);
                essenceBar.setTitle(essenceType.getChatColor() + Time.toString(timeRemaining * 1000, true) + ChatColor.GRAY + " remaining in "
                        + essenceType.getChatColor() + plugin.getConfig().getString("essenceOwner") + ChatColor.GRAY + "'s Battle Essence "
                        + essenceType.getChatColor() + "(+" + essenceType.getPercent() + "%)");
            }
        } else {
            essenceBar.setVisible(false);
            essenceBar.removeAll();
            essenceBar = null;
            for (Player player : plugin.getServer().getOnlinePlayers()) {
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
