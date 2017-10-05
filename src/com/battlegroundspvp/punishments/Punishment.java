package com.battlegroundspvp.punishments;
/* Created by GamerBah on 8/8/2016 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.battlegroundspvp.punishments.Punishment.Type.*;

@Data
@AllArgsConstructor
public class Punishment {
    private final int id;
    private final Type type;
    private final LocalDateTime date;
    private final int duration;
    private final LocalDateTime expiration;
    private final UUID enforcer;
    private final Reason reason;
    private boolean pardoned;

    public static Reason getReasonFromName(String name) {
        for (Reason reason : Reason.values()) {
            if (reason.getName().equals(name)) {
                return reason;
            }
        }
        return null;
    }

    public static Type getTypeFromName(String name) {
        for (Type type : Type.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }

        return null;
    }

    @AllArgsConstructor
    @Getter
    public enum Type {
        BAN("Ban"),
        TEMP_BAN("Temp-Ban"),
        MUTE("Mute"),
        KICK("Kick"),
        AUTO("Auto"),
        KICK_BAN(null),
        ALL(null);

        private String name;
    }

    @AllArgsConstructor
    @Getter
    public enum Reason {
        SPAM_CAPS(MUTE, "Spam (Caps)", "Player is spamming the chat,with fully capitalized words", "Please don't spam the chat with capitalized words!", 1500),
        SPAM_LETTERS(MUTE, "Spam (Letters)", "Player is spamming the,chat with random letters", "Please don't spam the chat with random letters!", 900),
        SPAM_GENERIC(MUTE, "Spam (Generic)", "Player is spamming the,chat in some sort of way", "Please don't spam the chat! We want to keep it clean!", 1200),
        SWEARING(ALL, "Swearing", "Player is using profane words in,either public chat or private messages", "Please don't swear! There are kids that play on the server!", 1500),
        ATTEMPT_SWEARING(AUTO, "Attempted Swearing", "Player is attempting to swear,in the chat but is being blocked", "Attempting to swear can get you in trouble too!", 500),
        HARASSMENT(ALL, "Player Harassment", "Player is verbally harassing others,and creating a toxic environment", "Harassment of other players is not tolerated.", 1800),
        GLITCH_ABUSE(KICK_BAN, "Glitch Exploiting", "Player was caught exploiting,a glitch with the plugin or arena", "If you find a bug, please report it! Don't use it to your advantage!", 1500),
        DISRESPECT(ALL, "Player Disrespect", "Player was disrespecting a,Staff member or other player", "Please treat the Staff and our players with respect.", 1800),
        MODDED_CLIENT(BAN, "Modded Client", "Player was found using a modded,client to gain an advantage", "Hacked Clients are not allowed. Please read our rules before joining.", -1),
        MODDED_CLIENT_SUSPECTED(BAN, "Suspected Modded Client", "Player was suspected of,using a modded client", "You were suspected of having a hacked client!", 3600);

        private Type type;
        private String name;
        private String description;
        private String message;
        private int length;
    }
}
