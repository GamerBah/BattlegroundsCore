package com.battlegroundspvp.utils;
/* Created by GamerBah on 8/15/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.utils.enums.ColorBuilder;
import com.battlegroundspvp.utils.enums.EventSound;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ChatFilter implements Listener {
    private BattlegroundsCore plugin;
    private HashMap<Player, Integer> attempts = new HashMap<>();

    public ChatFilter(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    private boolean isClean(String message) {
        String[] words = message.toLowerCase().split(" ");
        ArrayList<String> wordsArray = new ArrayList<>();
        wordsArray.addAll(Arrays.asList(words));
        String joined = null;
        if (words.length == 2) joined = String.join("", words[0], words[1]);
        if (words.length == 3) joined = String.join("", words[0], words[1], words[2]);
        if (words.length == 4) joined = String.join("", words[0], words[1], words[2], words[3]);
        if (words.length == 5) joined = String.join("", words[0], words[1], words[2], words[3], words[4]);
        if (words.length == 6) joined = String.join("", words[0], words[1], words[2], words[3], words[4], words[5]);
        if (words.length > 6) {
            for (int i = 0; i < words.length - 6; i++) {
                joined = String.join(",", words[0], words[i + 1], words[i + 2], words[i + 3], words[i + 4], words[i + 5]);
            }
        }
        if (joined != null) {
            joined = joined.replace("1", "i").replace("7", "t").replace("4", "a")
                    .replace("!", "i").replace("0", "o").replace("(", "c").replace("|", "i")
                    .replace("3", "e").replace("8", "b");
            for (String bad : plugin.getFilteredWords()) {
                if (joined.equals(bad)) {
                    //plugin.getServer().broadcastMessage("1. Joined-Equals");
                    return false;
                }
                if (joined.contains(bad)) {
                    //plugin.getServer().broadcastMessage("2. Joined-Contains, not safe");
                    return false;
                }
            }
        } else {
            for (String bad : plugin.getFilteredWords()) {
                for (String word : wordsArray) {
                    word = word.replace("1", "i").replace("7", "t").replace("4", "a")
                            .replace("!", "i").replace("0", "o").replace("(", "c").replace("|", "i")
                            .replace("3", "e").replace("8", "b");
                    if (word.equals(bad)) {
                        //plugin.getServer().broadcastMessage("3. Standard-Equals");
                        return false;
                    }
                    if (word.contains(bad)) {
                        for (String safe : plugin.getSafeWords()) {
                            if (word.contains(safe)) {
                                return true;
                            }
                        }
                        //plugin.getServer().broadcastMessage("2. Standard-Contains, not safe");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        //if (gameProfile.isMuted(event)) return;

        if (!isClean(event.getMessage())) {
            event.setCancelled(true);
            player.sendMessage(new ColorBuilder(ChatColor.RED).bold().create() + "Please refrain from using profane language!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            if (!attempts.containsKey(player)) {
                if (!plugin.getGameProfile(player.getUniqueId()).hasRank(Rank.HELPER)) {
                    attempts.put(player, 1);
                }
            } else {
                attempts.put(player, attempts.get(player) + 1);
                if (attempts.get(player) == 10) {
                    attempts.remove(player);
                    //plugin.warnPlayer(null, gameProfile, Punishment.Reason.ATTEMPT_SWEARING);
                }
            }
        }
    }
}
