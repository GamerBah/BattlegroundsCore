package com.battlegroundspvp.utils.messages;
/* Created by GamerBah on 8/15/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ChatFilter implements Listener {

    @Getter
    private static HashMap<Player, Integer> attempts = new HashMap<>();

    public static boolean isClean(String message) {
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
                    .replace("3", "e").replace("8", "b").replaceAll("[^a-zA-Z0-9]+", "");
            for (String bad : BattlegroundsCore.getInstance().getFilteredWords()) {
                if (joined.equals(bad)) {
                    BattlegroundsCore.getInstance().getServer().broadcastMessage("1. Joined-Equals");
                    return false;
                }
                if (joined.contains(bad)) {
                    BattlegroundsCore.getInstance().getServer().broadcastMessage("2. Joined-Contains, not safe");
                    return false;
                }
            }
        } else {
            for (String bad : BattlegroundsCore.getInstance().getFilteredWords()) {
                for (String word : wordsArray) {
                    word = word.replace("1", "i").replace("7", "t").replace("4", "a")
                            .replace("!", "i").replace("0", "o").replace("(", "c").replace("|", "i")
                            .replace("3", "e").replace("8", "b");
                    if (word.equals(bad)) {
                        BattlegroundsCore.getInstance().getServer().broadcastMessage("3. Standard-Equals");
                        return false;
                    }
                    if (word.contains(bad)) {
                        for (String safe : BattlegroundsCore.getInstance().getSafeWords()) {
                            if (word.contains(safe)) {
                                return true;
                            }
                        }
                        BattlegroundsCore.getInstance().getServer().broadcastMessage("2. Standard-Contains, not safe");
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
