package com.battlegroundspvp.utils.inventories;
/* Created by GamerBah on 9/22/2017 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.HashSet;

public class ClickEvent {

    @Getter
    private HashSet<Type> clickTypes;
    @Getter
    private Runnable action;

    /**
     * Creates a new Click Event with the given {@link Type} and {@link Runnable}
     *
     * @param types  Types of clicks that will run this event
     * @param action {@link Runnable} to be executed if types are satisfied
     */
    public ClickEvent(final Type[] types, final Runnable action) {
        HashSet<Type> set = new HashSet<>(types.length);
        set.addAll(Arrays.asList(types));
        this.clickTypes = set;
        this.action = action;
    }

    /**
     * Creates a new Click Event with a single {@link Type}
     *
     * @param type   the Type of click that runs this event
     * @param action the Runnable to be executed if type is satisfied
     */
    public ClickEvent(final Type type, final Runnable action) {
        this(new Type[]{type}, action);
    }

    /**
     * Creates a new Click Event using the default {@link Type} (ANY) and a {@link Runnable}
     *
     * @param action {@link Runnable} to be executed
     */
    public ClickEvent(final Runnable action) {
        this(Type.ANY, action);
    }

    /**
     * Enum class to simplify Bukkit's own {@link ClickType} enum
     * <p>
     * Since Bukkit's own ClickType has many values that aren't very useful for
     * {@link org.bukkit.inventory.Inventory} management, this enum helps simply
     * it a little bit. The basic ClickTypes are there, but the added "ANY"
     * value provides an all-encompassing value for instances when the type
     * shouldn't matter
     */
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
