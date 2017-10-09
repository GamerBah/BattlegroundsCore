package com.battlegroundspvp.utils.advancements;
/* Created by GamerBah on 10/8/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.advancements.api.AdvancementBuilder;
import com.battlegroundspvp.utils.enums.Advancements;
import com.battlegroundspvp.utils.enums.TexturePath;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

@Getter
public class CustomAdvancement {

    private final String name;
    private final String description;
    private final NamespacedKey icon;
    private final int data;
    private final boolean hidden;
    private final boolean toast;
    private final NamespacedKey background;
    private final String keyName;
    private final AdvancementBuilder.FrameType frameType;
    private final String parent;
    private final NamespacedKey key;
    private Advancement advancement;

    public CustomAdvancement(String name, String description, ItemStack icon, TexturePath background) {
        this.name = name;
        this.description = description;
        this.icon = NamespacedKey.minecraft(icon.getType().toString().toLowerCase());
        this.data = icon.getDurability();
        this.hidden = false;
        this.toast = false;
        this.background = NamespacedKey.minecraft("textures/blocks/" + background.getPath() + ".png");
        this.frameType = AdvancementBuilder.FrameType.TASK;
        this.keyName = ChatColor.stripColor(name).replace(" ", "").toLowerCase();
        this.parent = null;
        this.key = new NamespacedKey(BattlegroundsCore.getInstance(), keyName);
        this.advancement = new AdvancementBuilder(this.key)
                .withTitle(this.name)
                .withDescription(this.description)
                .withIcon(this.icon, this.data)
                .withBackground(this.background)
                .setAnnounceToChat(false)
                .save();
    }

    public CustomAdvancement(String name, String description, ItemStack icon, boolean hidden, boolean toast, AdvancementBuilder.FrameType frameType, Advancements parent) {
        this.name = name;
        this.description = description;
        this.icon = NamespacedKey.minecraft(icon.getType().toString().toLowerCase());
        this.data = icon.getDurability();
        this.hidden = hidden;
        this.toast = toast;
        this.background = NamespacedKey.minecraft("textures/blocks/stone.png");
        this.frameType = frameType;
        this.keyName = ChatColor.stripColor(name).replace(" ", "").toLowerCase();
        this.key = new NamespacedKey(BattlegroundsCore.getInstance(), keyName);
        this.parent = parent.getCustomAdvancement().getKey().toString();
        this.advancement = new AdvancementBuilder(this.key)
                .withTitle(this.name)
                .withDescription(this.description)
                .withIcon(this.icon, this.data)
                .withFrame(this.frameType)
                .withParent(this.parent)
                .setHidden(this.hidden)
                .setShowToast(this.toast)
                .setAnnounceToChat(false)
                .save();
    }

    public void reward(Player player, Runnable reward) {
        if (reward == null) {
            return;
        }
        if (!player.getAdvancementProgress(Bukkit.getAdvancement(key)).isDone()) {
            BattlegroundsCore.getInstance().getLogger().log(Level.WARNING, player.getName() + " has not completed the criteria for " + name + ". Reward not given.");
            return;
        }
        reward.run();
    }

}
