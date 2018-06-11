package com.battlegroundspvp.administration.data.util;
/* Created by GamerBah on 6/11/2018 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Data
@AllArgsConstructor
public class Update {

    //.minusHours(9).format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(PST)'"))

    private final int id;
    private final LocalDateTime date;
    private final Result result;
    private final ArrayList<String> notes;

    public String getDateString() {
        return date.minusHours(9).format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(PST)'"));
    }

    @Getter
    @AllArgsConstructor
    public enum Result {

        SUCCESS(ChatColor.GREEN + "Success"),
        FAIL_ROLLBACK(ChatColor.GOLD + "Failed - Rollback"),
        FAIL_FATAL(ChatColor.RED + "Failed - Fatal");

        private String displayName;

    }

}
