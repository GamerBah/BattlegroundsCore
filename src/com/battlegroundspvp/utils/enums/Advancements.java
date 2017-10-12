package com.battlegroundspvp.utils.enums;/* Created by GamerBah on 10/8/2017 */

import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.advancements.CustomAdvancement;
import com.battlegroundspvp.utils.advancements.api.AdvancementBuilder;
import com.battlegroundspvp.utils.inventories.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum Advancements {
    //String name, String description, ItemStack icon, boolean hidden, boolean toast, String background, FrameType frameType, String key, Advancements parent

    BASE(new CustomAdvancement("Battlegrounds", "Welcome to Battlegrounds!", new ItemBuilder(Material.SHIELD), TexturePath.CONCRETE_GRAY)),

    RECRUITER_I(new CustomAdvancement(ChatColor.YELLOW + "Recruiter I", "Recruit a friend", new ItemBuilder(Material.SKULL).durability(3), false, true, AdvancementBuilder.FrameType.TASK, BASE)),
    RECRUITER_II(new CustomAdvancement(ChatColor.YELLOW + "Recruiter II", "Recruit 5 friends", new ItemBuilder(Material.SKULL).durability(0), false, true, AdvancementBuilder.FrameType.TASK, RECRUITER_I)),
    RECRUITER_III(new CustomAdvancement(ChatColor.YELLOW + "Recruiter III", "Recruit 10 friends", new ItemBuilder(Material.SKULL).durability(1), false, true, AdvancementBuilder.FrameType.TASK, RECRUITER_II)),
    RECRUITER_IV(new CustomAdvancement(ChatColor.YELLOW + "Recruiter IV", "Recruit 15 friends", new ItemBuilder(Material.SKULL).durability(2), false, true, AdvancementBuilder.FrameType.TASK, RECRUITER_III)),
    RECRUITER_V(new CustomAdvancement(ChatColor.YELLOW + "Recruiter V", "Recruit 20 friends", new ItemBuilder(Material.SKULL).durability(4), false, true, AdvancementBuilder.FrameType.TASK, RECRUITER_IV)),
    RECRUITER_MASTERY(new CustomAdvancement(new ColorBuilder(ChatColor.YELLOW).bold().create() + "Recruiter Master", "Recruit 30 friends", new ItemBuilder(Material.SKULL).durability(5), false, true, AdvancementBuilder.FrameType.GOAL, RECRUITER_V)),
    RECRUITER_WIZARD(new CustomAdvancement(new ColorBuilder(ChatColor.LIGHT_PURPLE).bold().create() + "Recruiter Wizard", ChatColor.ITALIC + "\"You're a wizard, Harry!\"\n" + "Recruit 50 friends!", new ItemBuilder(Material.NETHER_STAR).durability(5), true, true, AdvancementBuilder.FrameType.GOAL, RECRUITER_MASTERY)),

    SHOWMANSHIP_I(new CustomAdvancement(ChatColor.YELLOW + "Showmanship I", "Unlock a particle pack", new ItemBuilder(Material.DIAMOND), false, true, AdvancementBuilder.FrameType.TASK, BASE)),
    SHOWMANSHIP_II(new CustomAdvancement(ChatColor.YELLOW + "Showmanship II", "Unlock 3 particle packs", new ItemBuilder(Material.DIAMOND), false, true, AdvancementBuilder.FrameType.TASK, SHOWMANSHIP_I)),
    SHOWMANSHIP_III(new CustomAdvancement(ChatColor.YELLOW + "Showmanship III", "Unlock 6 particle packs", new ItemBuilder(Material.DIAMOND), false, true, AdvancementBuilder.FrameType.TASK, SHOWMANSHIP_II)),
    SHOWMANSHIP_IV(new CustomAdvancement(ChatColor.YELLOW + "Showmanship IV", "Unlock 9 particle packs", new ItemBuilder(Material.DIAMOND), false, true, AdvancementBuilder.FrameType.TASK, SHOWMANSHIP_III)),
    SHOWMANSHIP_V(new CustomAdvancement(ChatColor.YELLOW + "Showmanship V", "Unlock 12 particle packs", new ItemBuilder(Material.DIAMOND), false, true, AdvancementBuilder.FrameType.TASK, SHOWMANSHIP_IV)),
    SHOWMANSHIP_MASTERY(new CustomAdvancement(new ColorBuilder(ChatColor.YELLOW).bold().create() + "Showmanship Mastery", "Unlock all particle packs", new ItemBuilder(Material.DIAMOND), false, true, AdvancementBuilder.FrameType.GOAL, SHOWMANSHIP_V));

    private CustomAdvancement customAdvancement;

}
