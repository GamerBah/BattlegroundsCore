package com.battlegroundspvp.utils.enums;
/* Created by GamerBah on 9/8/2016 */

import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.inventories.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;


public enum Cosmetic {
    PARTICLE_PACK, KILL_SOUND, KILL_EFFECT;

    public static Item typeFromName(String name, Cosmetic group) {
        for (Item item : Item.values()) {
            if (item.getName().equals(name) && item.getGroup().equals(group)) {
                return item;
            }
        }
        return null;
    }

    @AllArgsConstructor
    @Getter
    public enum Item {
        /**
         * PARTICLE PACKS
         */
        // Rare


        // Epic
        TRAIL_RAIN_STORM(20, PARTICLE_PACK, "Rain Storm", new ItemBuilder(Material.WATER_BUCKET)
                .name(new ColorBuilder(ChatColor.GOLD).bold().create() + "Rain Storm")
                .lore(ChatColor.YELLOW + "Idle Effect: " + ChatColor.GRAY + "A rain cloud over your head")
                .lore(ChatColor.YELLOW + "Moving Effect: " + ChatColor.GRAY + "Water splashes and rain drops"),
                Rarity.EPIC, null, 0, 0),
        TRAIL_LAVA_RAIN(21, PARTICLE_PACK, "Lava Rain", new ItemBuilder(Material.LAVA_BUCKET)
                .name(new ColorBuilder(ChatColor.GOLD).bold().create() + "Lava Rain")
                .lore(ChatColor.YELLOW + "Idle Effect: " + ChatColor.GRAY + "A smoke cloud raining lava over your head")
                .lore(ChatColor.YELLOW + "Moving Effect: " + ChatColor.GRAY + "Lava bubbles and lava drops"),
                Rarity.EPIC, null, 0, 0),

        // Legendary
        TRAIL_FLAME_WARRIOR(40, PARTICLE_PACK, "Flame Warrior", new ItemBuilder(Material.FLINT_AND_STEEL)
                .name(new ColorBuilder(ChatColor.LIGHT_PURPLE).bold().create() + "Flame Warrior")
                .lore(ChatColor.YELLOW + "Idle Effect: " + ChatColor.GRAY + "A flame helix rotating around you")
                .lore(ChatColor.YELLOW + "Moving Effect: " + ChatColor.GRAY + "Flames and smoke"),
                Rarity.LEGENDARY, null, 0, 0),


        /**
         * KILL SOUNDS
         */
        // Rare
        SOUND_PIG_DEATH(100, KILL_SOUND, "Pig Death", new ItemBuilder(Material.PORK)
                .name(ChatColor.BLUE + "Pig Death")
                .lore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Gruesome yet satisfying!"),
                Rarity.RARE, Sound.ENTITY_PIG_DEATH, 1, 1),
        SOUND_LEVEL_UP(101, KILL_SOUND, "Level Up", new ItemBuilder(Material.EXP_BOTTLE)
                .name(ChatColor.BLUE + "Level Up")
                .lore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Something doesn't quite add up..."),
                Rarity.RARE, Sound.ENTITY_PLAYER_LEVELUP, 1.1F, 1),
        SOUND_ANVIL(102, KILL_SOUND, "Anvil", new ItemBuilder(Material.ANVIL)
                .name(ChatColor.BLUE + "Anvil")
                .lore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Crushed by pure iron."),
                Rarity.RARE, Sound.BLOCK_ANVIL_LAND, 1, 0.9F),

        // Epic
        SOUND_EXPLODE(130, KILL_SOUND, "Explosion", new ItemBuilder(Material.TNT)
                .name(new ColorBuilder(ChatColor.GOLD).bold().create() + "Explosion")
                .lore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Kaboom!"),
                Rarity.EPIC, Sound.ENTITY_GENERIC_EXPLODE, 1, 1),

        //Legendary
        SOUND_CAT_MEOW(160, KILL_SOUND, "Meow", new ItemBuilder(Material.DIAMOND)
                .name(new ColorBuilder(ChatColor.LIGHT_PURPLE).bold().create() + "Meow")
                .lore(ChatColor.GRAY + "" + ChatColor.ITALIC + "A cute and cuddly death!"),
                Rarity.LEGENDARY, Sound.ENTITY_CAT_AMBIENT, 1.3F, 1),


        /**
         * NONE
         */
        TRAIL_NONE(1000, PARTICLE_PACK, "None", new ItemBuilder(Material.BARRIER).name(ChatColor.RED + "None").lore(ChatColor.GRAY + "Removes your active Particle Pack"),
                Rarity.COMMON, null, 0, 0),
        WARCRY_NONE(1001, KILL_SOUND, "None", new ItemBuilder(Material.BARRIER).name(ChatColor.RED + "None").lore(ChatColor.GRAY + "Removes your active Warcry"),
                Rarity.COMMON, null, 0, 0),
        GORE_NONE(1002, KILL_EFFECT, "None", new ItemBuilder(Material.BARRIER).name(ChatColor.RED + "None").lore(ChatColor.GRAY + "Removes your active Gore"),
                Rarity.COMMON, null, 0, 0);


        private int id;
        private Cosmetic group;
        private String name;
        private ItemBuilder item;
        private Rarity rarity;
        private Sound killSound;
        private float volume;
        private float pitch;

        public static Item fromString(String name) {
            Item i = null;
            for (Item item : Item.values())
                if (name.trim().toUpperCase().equals(item.toString()))
                    i = item;
            if (i == null)
                throw new IllegalArgumentException("Name doesn't match an Item");
            return i;
        }
    }
}
