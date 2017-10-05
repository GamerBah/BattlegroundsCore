package com.battlegroundspvp.utils.inventories;
/* Created by GamerBah on 9/22/2017 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.inventory.ClickType;

public class ClickEvent {

    @Getter
    private Runnable action;
    @Getter private Type type;

    public ClickEvent(Type type, Runnable action) {
        this.type = type;
        this.action = action;
    }

    public void run() {
        this.getAction().run();
    }

    @AllArgsConstructor
    @Getter
    public enum Type {
        RIGHT(ClickType.RIGHT),
        LEFT(ClickType.LEFT),
        MIDDLE(ClickType.MIDDLE),
        SHIFT_RIGHT(ClickType.SHIFT_RIGHT),
        SHIFT_LEFT(ClickType.SHIFT_LEFT),
        DROP(ClickType.DROP),
        ANY(ClickType.UNKNOWN);

        private ClickType clickType;
    }

}
