package com.battlegroundspvp.utils.inventories;
/* Created by GamerBah on 7/17/2017 */

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class InventoryBuilder {

    @Getter
    private static final HashMap<Player, InventoryBuilder> inventoryUsers = new HashMap<>();

    @Getter
    private Player player;
    @Getter
    private GameInventory gameInventory;
    @Getter
    private int page = 0, maxPage = 0, pageRow = 6;
    @Getter
    private String search = "";
    @Getter
    private boolean onlineOnly = false;
    @Getter
    private ArrayList<ItemBuilder> items = new ArrayList<>();
    @Getter
    @Setter
    private ItemBuilder nextPageItem = InventoryItems.nextPage, previousPageItem = InventoryItems.previousPage, goBackItem = InventoryItems.back;
    private int itemsPerPage;

    /**
     * Creates a new chainable builder for the specified {@link Player} and {@link GameInventory}
     *
     * @param player        the {@link Player} this {@link GameInventory} will be displayed to upon calling {@code open()}
     * @param gameInventory the {@link GameInventory} to build from
     * @see GameInventory
     */
    public InventoryBuilder(Player player, GameInventory gameInventory) {
        this.player = player;
        this.gameInventory = gameInventory;
        this.items = gameInventory.getItems();
        this.itemsPerPage = (gameInventory.getSearchRows() * 9) - gameInventory.getTopOffset() - gameInventory.getBottomOffset();
        this.maxPage = (items.size() <= itemsPerPage ? 0 : (int) Math.floor(items.size() / itemsPerPage));
        inventoryUsers.put(player, this);
    }


    /**
     * Sorts the items using the given {@link Comparator}
     *
     * @param comparator the {@link Comparator} to use for comparing items
     * @return this class for chaining
     */
    public InventoryBuilder sortItems(final Comparator<ItemBuilder> comparator) {
        items.sort(comparator);
        return this;
    }

    /**
     * Sets this builder instance to the specified page without opening it to the {@link Player}
     * <p>
     * Next Page, Previous Page, and Back button items are automatically added accordingly, so
     * there's no need to manually add them per-page
     *
     * @param number the page to go to
     * @return this class for chaining
     * @throws IllegalArgumentException when number is {@literal >} the max page and {@literal <} 0
     */
    public InventoryBuilder page(final int number) {
        if (number > maxPage || number < 0)
            throw new IllegalArgumentException("Number must be <= maxPage and >= 0");
        this.page = number;
        int index = number * itemsPerPage;
        if (this.items.size() > 0) {
            for (int i = (gameInventory.getSearchStart() * 9) + gameInventory.getTopOffset(); i < itemsPerPage - (gameInventory.getSearchEnd() * 9) - gameInventory.getBottomOffset(); i++) {
                if (index < this.items.size()) {
                    gameInventory.getInventory().setItem(i, this.items.get(index++));
                } else {
                    gameInventory.getInventory().setItem(i, null);
                }
            }
        }

        if (page < maxPage)
            gameInventory.addButton((gameInventory.isInlineNavigation() ? (gameInventory.getSearchRows() * 9) - 1 : (9 * pageRow) - 1),
                    getNextPageItem().clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> this.nextPage().open())));
        if (page > 0)
            gameInventory.addButton((gameInventory.isInlineNavigation() ? (gameInventory.getSearchRows() * 9) - 1 : (9 * pageRow) - 1),
                    getPreviousPageItem().clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> this.previousPage().open())));
        if (page == 0)
            if (!gameInventory.isNoBackButton())
                gameInventory.addButton(9 * (pageRow - 1),
                        getGoBackItem().clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> this.gameInventory.openPreviousInventory(player))));
        if (page == maxPage)
            gameInventory.getInventory().clear((9 * pageRow) - 1);
        return this;
    }

    /**
     * Searches the names of the items for the given keyword
     * <p>
     * Calling this will reset the page to 0. This is to prevent
     * exceptions that would occur if the current page didn't
     * exist after the search keyword changed.<br>
     * Setting the value to "" will clear the current search keyword.
     *
     * @param search the keyword to search item names for
     * @return this class for chaining
     */
    public InventoryBuilder search(final String search) {
        if (search.equals("")) {
            this.search = "";
            items = gameInventory.getItems();
        }
        this.search = search;
        for (ItemBuilder item : items)
            if (!StringUtils.containsIgnoreCase(ChatColor.stripColor(item.getItemMeta().getDisplayName()), search))
                items.remove(item);
        maxPage = (int) Math.floor(items.size() / itemsPerPage);
        page(0);
        return this;
    }


    /**
     * Makes a cleaner call to <code>page(int number + 1)</code>
     *
     * @return this class for chaining
     */
    public InventoryBuilder nextPage() {
        return this.page(this.page + 1);
    }

    /**
     * Makes a cleaner call to <code>page(int number - 1)</code>
     *
     * @return this class for chaining
     */
    public InventoryBuilder previousPage() {
        return this.page(this.page - 1);
    }

    /**
     * Opens this {@link GameInventory} for the current {@link Player}
     * <p>
     * Uses the current InventoryBuilder instance
     * Opens to the page set by <code>page(int number)</code>, or the first page if unset
     */
    public void open() {
        player.openInventory(this.gameInventory.getInventory());
        inventoryUsers.put(player, this);
    }

}
