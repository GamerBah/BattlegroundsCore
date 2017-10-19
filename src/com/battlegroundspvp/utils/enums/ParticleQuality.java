package com.battlegroundspvp.utils.enums;/* Created by GamerBah on 12/23/2016 */

import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.inventories.ItemBuilder;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

@AllArgsConstructor
public enum ParticleQuality {

    LOW("Low", new ItemBuilder(Material.INK_SACK).durability(8)
            .name(ChatColor.AQUA + "Particle Quality")
            .lore(new ColorBuilder(ChatColor.GREEN).bold().create() + "Low").lore(" ")
            .lore(ChatColor.GRAY + "Changes the amount of")
            .lore(ChatColor.GRAY + "particles that display")
            .lore(ChatColor.GRAY + "in some effects").lore(" ")
            .lore(ChatColor.YELLOW + "Click to change!")),
    MEDIUM("Medium", new ItemBuilder(Material.INK_SACK).durability(5)
            .name(ChatColor.AQUA + "Particle Quality")
            .lore(new ColorBuilder(ChatColor.GOLD).bold().create() + "Medium").lore(" ")
            .lore(ChatColor.GRAY + "Changes the amount of")
            .lore(ChatColor.GRAY + "particles that display")
            .lore(ChatColor.GRAY + "in some effects").lore(" ")
            .lore(ChatColor.YELLOW + "Click to change!")),
    HIGH("High", new ItemBuilder(Material.INK_SACK).durability(10)
            .name(ChatColor.AQUA + "Particle Quality")
            .lore(new ColorBuilder(ChatColor.RED).bold().create() + "High " + ChatColor.GRAY + "(Default)").lore(" ")
            .lore(ChatColor.GRAY + "Changes the amount of")
            .lore(ChatColor.GRAY + "particles that display")
            .lore(ChatColor.GRAY + "in some effects").lore(" ")
            .lore(ChatColor.YELLOW + "Click to change!"));

    public String name;
    public ItemBuilder item;

    public static ParticleQuality fromString(String name) {
        ParticleQuality q = null;
        for (ParticleQuality quality : ParticleQuality.values())
            if (name.trim().toUpperCase().equals(quality.toString()))
                q = quality;
        if (q == null)
            throw new IllegalArgumentException("Name doesn't match a ParticleQuality");
        return q;
    }
}
