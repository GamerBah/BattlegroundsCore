package com.battlegroundspvp.util;
/* Created by GamerBah on 11/11/2017 */

import com.battlegroundspvp.util.message.MessageBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.time.LocalDateTime;

public class BugReport {

    @Getter
    private final int id;
    @Getter
    private final Type type;
    @Getter
    @Setter
    private Status status = Status.WAITING;
    @Getter
    @Setter
    private Priority priority = Priority.NORMAL;
    @Getter
    @Setter
    private LocalDateTime submissionDate;
    @Getter
    @Setter
    private LocalDateTime dateResolved;
    @Getter
    @Setter
    private String bugMessage;
    @Getter
    @Setter
    private String recreationMessage;

    public BugReport(final int id, final Type type) {
        this.id = id;
        this.type = type;
    }

    @AllArgsConstructor
    @Getter
    public enum Type {
        GENERAL(0, ChatColor.GREEN + "General", "If you're unsure what category><your bug falls into, choose this", Material.CHEST),
        GAMEPLAY(1, ChatColor.YELLOW + "Gameplay", "For bugs that relate to><kits, abilities, etc", Material.DIAMOND_SWORD),
        DONATION(2, ChatColor.AQUA + "Donation", "For issues with donations", Material.DIAMOND);

        private final int id;
        private final String name;
        private final String description;
        private final Material material;

        public static Type ofId(final int id) {
            for (Type p : Type.values())
                if (p.id == id)
                    return p;
            return GENERAL;
        }
    }

    @AllArgsConstructor
    @Getter
    public enum Status {
        WAITING(0, "Waiting", new MessageBuilder(ChatColor.YELLOW).bold().create()),
        CONFIRMED(1, "Confirmed", new MessageBuilder(ChatColor.AQUA).bold().create()),
        RESOLVED(2, "Resolved", new MessageBuilder(ChatColor.GREEN).bold().create()),
        UNREPRODUCIBLE(3, "Unable to Reproduce", new MessageBuilder(ChatColor.RED).bold().create()),
        NEEDS_INFO(4, "Needs More Information", new MessageBuilder(ChatColor.GOLD).bold().create());

        private final int id;
        private final String name;
        private final String color;

        public String getName(final boolean uppercase) {
            return color + (uppercase ? name.toUpperCase() : name);
        }

        public static Status ofId(final int id) {
            for (Status p : Status.values())
                if (p.id == id)
                    return p;
            return WAITING;
        }
    }

    @AllArgsConstructor
    @Getter
    public enum Priority {
        LOW(0, "Low", new MessageBuilder(ChatColor.GREEN).bold().create()),
        NORMAL(1, "Normal", new MessageBuilder(ChatColor.YELLOW).bold().create()),
        MEDIUM(2, "Medium", new MessageBuilder(ChatColor.GOLD).bold().create()),
        HIGH(3, "High", new MessageBuilder(ChatColor.RED).bold().create());

        private final int id;
        private final String name;
        private final String color;

        public String getName(final boolean uppercase) {
            return color + (uppercase ? name.toUpperCase() : name);
        }

        public static Priority ofId(final int id) {
            for (Priority p : Priority.values())
                if (p.id == id)
                    return p;
            return LOW;
        }
    }

}
