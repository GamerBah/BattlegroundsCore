package com.battlegroundspvp.utils.advancements;
/* Created by GamerBah on 10/8/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.advancements.api.AdvancementAPI;
import com.battlegroundspvp.utils.advancements.api.FrameType;
import com.battlegroundspvp.utils.advancements.api.Trigger;
import com.battlegroundspvp.utils.enums.Advancements;
import com.battlegroundspvp.utils.enums.TexturePath;
import com.sun.istack.internal.Nullable;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

@Getter
@Setter
public class CustomAdvancement {

    private final String name;
    private final String description;
    private final ItemStack icon;
    private final boolean hidden;
    private final boolean toast;
    private final String background;
    private final String keyName;
    private final FrameType frameType;
    private final AdvancementAPI parent;
    private final NamespacedKey key;
    private AdvancementAPI api;

    public CustomAdvancement(String name, String description, ItemStack icon, boolean hidden, boolean toast, TexturePath background, FrameType frameType, @Nullable Advancements parent) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.hidden = hidden;
        this.toast = toast;
        this.background = background.getPath();
        this.frameType = frameType;
        this.keyName = ChatColor.stripColor(name).replace(" ", "").toLowerCase();
        this.parent = (parent != null ? parent.getCustomAdvancement().getApi() : null);
        this.key = new NamespacedKey(BattlegroundsCore.getInstance(), "core/" + keyName);
    }

    public void register() {
        if (parent != null) {
            AdvancementAPI api = AdvancementAPI.builder(key)
                    .title(name)
                    .description(description)
                    .icon("minecraft:" + icon.getType().toString().toLowerCase())
                    .data(icon.getDurability())
                    .trigger(Trigger.builder(Trigger.TriggerType.IMPOSSIBLE, "custom-trigger"))
                    .hidden(hidden)
                    .toast(toast)
                    .background("minecraft:textures/blocks/" + background + ".png")
                    .frame(frameType)
                    .parent(parent.getId().toString())
                    .build();
            api.add();
            this.api = api;
        } else {
            AdvancementAPI api = AdvancementAPI.builder(key)
                    .title(name)
                    .description(description)
                    .icon("minecraft:" + icon.getType().toString().toLowerCase())
                    .data(icon.getDurability())
                    .trigger(Trigger.builder(Trigger.TriggerType.IMPOSSIBLE, "custom-trigger"))
                    .hidden(hidden)
                    .toast(toast)
                    .background("minecraft:textures/blocks/" + background + ".png")
                    .frame(frameType)
                    .build();
            api.add();
            this.api = api;
        }
    }

    public void awardCriteria(Player player, String criteria) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).awardCriteria("custom-trigger");
    }

    public void reward(Player player, Runnable reward) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).awardCriteria("custom-trigger");
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
