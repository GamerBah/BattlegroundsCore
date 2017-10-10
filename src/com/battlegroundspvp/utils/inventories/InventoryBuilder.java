package com.battlegroundspvp.utils.inventories;
/* Created by GamerBah on 7/17/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.utils.enums.EventSound;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class InventoryBuilder {

    private BattlegroundsCore plugin = BattlegroundsCore.getInstance();

    @Getter
    private static HashMap<Player, InventoryBuilder> inventoryUsers = new HashMap<>();

    @Getter
    private Player player;
    @Getter
    private GameInventory gameInventory;
    @Getter
    private Inventory inventory;
    @Getter
    private int page = 0;
    @Getter
    private int maxPage = 0;
    @Getter
    private SortType sortType = SortType.ALPHABETICALLY;
    @Getter
    private String search = "";
    @Getter
    private boolean onlineOnly = false;
    @Getter
    private ArrayList<ItemBuilder> items = new ArrayList<>();
    private int itemsPerPage;

    public InventoryBuilder(Player player, GameInventory gameInventory) {
        this.player = player;
        this.gameInventory = gameInventory;
        this.inventory = gameInventory.getInventory();
        this.items = gameInventory.getSortables();
        this.itemsPerPage = (gameInventory.isBiDirectionalOffset() ? 36 - (2 * gameInventory.getSlotOffset()) : 36 - gameInventory.getSlotOffset());
        this.maxPage = (items.size() <= itemsPerPage ? 0 : (int) Math.ceil(items.size() / itemsPerPage));
        if (!gameInventory.getType().equals(GameInventory.Type.STANDARD))
            for (int i = 36; i < 45; i++)
                inventory.setItem(i, InventoryItems.border);

        inventoryUsers.put(player, this);
    }

    public InventoryBuilder sort(SortType type) {
        if (gameInventory.getType() != GameInventory.Type.PUNISH_SEARCH) {
            switch (type) {
                case ALPHABETICALLY:
                    items.sort((ItemStack is1, ItemStack is2) -> ChatColor.stripColor(is1.getItemMeta().getDisplayName()).compareTo(ChatColor.stripColor(is2.getItemMeta().getDisplayName())));
                    sortType = SortType.ALPHABETICALLY;

                case REVERSE_ALPHABETICALLY:
                    items.sort((ItemStack is1, ItemStack is2) -> ChatColor.stripColor(is2.getItemMeta().getDisplayName()).compareTo(ChatColor.stripColor(is1.getItemMeta().getDisplayName())));
                    sortType = SortType.REVERSE_ALPHABETICALLY;

                case RANK_HIGH_LOW:
                    items.sort((ItemStack is1, ItemStack is2) -> Rank.valueOf(ChatColor.stripColor(BattlegroundsCore.getLore(is1, 1)).replace("Rank: ", "").toUpperCase())
                            .compareTo(Rank.valueOf(ChatColor.stripColor(BattlegroundsCore.getLore(is2, 1)).replace("Rank: ", "").toUpperCase())));
                    sortType = SortType.RANK_HIGH_LOW;

                case RANK_LOW_HIGH:
                    items.sort((ItemStack is1, ItemStack is2) -> Rank.valueOf(ChatColor.stripColor(BattlegroundsCore.getLore(is2, 1)).replace("Rank: ", "").toUpperCase())
                            .compareTo(Rank.valueOf(ChatColor.stripColor(BattlegroundsCore.getLore(is1, 1)).replace("Rank: ", "").toUpperCase())));
                    sortType = SortType.RANK_LOW_HIGH;
            }
        } else {
            switch (type) {
                case NEWEST:
                    items.sort((ItemStack is1, ItemStack is2) -> LocalDateTime.parse(ChatColor.stripColor(is1.getItemMeta().getDisplayName()), DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(PST)'"))
                            .isAfter(LocalDateTime.parse(ChatColor.stripColor(is2.getItemMeta().getDisplayName()), DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(PST)'"))) ? 1 : 0);
                    sortType = SortType.NEWEST;

                case OLDEST:
                    items.sort((ItemStack is1, ItemStack is2) -> LocalDateTime.parse(ChatColor.stripColor(is1.getItemMeta().getDisplayName()), DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(PST)'"))
                            .isBefore(LocalDateTime.parse(ChatColor.stripColor(is2.getItemMeta().getDisplayName()), DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(PST)'"))) ? 1 : 0);
                    sortType = SortType.OLDEST;
            }
        }
        return this;
    }

    public InventoryBuilder page(int number) {
        if (gameInventory.getType() != GameInventory.Type.STANDARD) {
            if (number > maxPage || number < 0)
                throw new IllegalArgumentException("Number must be <= maxPage and >= 0");
            this.page = number;
            int index = number * itemsPerPage;
            if (this.items.size() > 0) {
                for (int i = gameInventory.getSlotOffset(); i < itemsPerPage; i++) {
                    if (index >= this.items.size()) {
                        inventory.setItem(i, null);
                    } else {
                        inventory.setItem(i, this.items.get(index++));
                    }
                }
            }

            if (page < maxPage)
                gameInventory.addClickableItem(53, InventoryItems.nextPage.clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                    this.nextPage().open();
                    EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                })));
            if (page > 0)
                gameInventory.addClickableItem(45, InventoryItems.previousPage.clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                    this.previousPage().open();
                    EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                })));
            if (page == 0)
                gameInventory.addClickableItem(45, InventoryItems.back.clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> this.gameInventory.openPreviousInventory(player))));
            online(onlineOnly);
        }
        return this;
    }

    public InventoryBuilder search(String search) {
        if (search.equals("")) {
            this.search = "";
            items = gameInventory.getSortables();
        }
        this.search = search;
        for (ItemBuilder item : items)
            if (!StringUtils.containsIgnoreCase(ChatColor.stripColor(item.getItemMeta().getDisplayName()), search))
                items.remove(item);
        maxPage = (int) Math.ceil(items.size() / itemsPerPage);
        page(0);
        return this;
    }

    public InventoryBuilder online(boolean online) {
        if (gameInventory.getType().equals(GameInventory.Type.PUNISH_SEARCH))
            return this;
        onlineOnly = online;
        if (online) {
            for (ItemBuilder item : items)
                if (!plugin.getServer().getPlayer(ChatColor.stripColor(item.getItemMeta().getDisplayName())).isOnline())
                    items.remove(item);
            maxPage = (int) Math.ceil(items.size() / itemsPerPage);
            sort(sortType);
        } else {
            items = gameInventory.getSortables();
            sort(sortType);
        }
        return this;
    }

    private InventoryBuilder nextPage() {
        return this.page(this.page + 1);
    }

    private InventoryBuilder previousPage() {
        return this.page(this.page - 1);
    }

    private InventoryBuilder previousInventory() {
        return new InventoryBuilder(this.player, this.gameInventory.getPreviousInventory());
    }

    public void open() {
        page(page);
        player.openInventory(this.inventory);
        inventoryUsers.put(player, this);
    }

    @AllArgsConstructor
    @Getter
    public enum SortType {
        ALPHABETICALLY("Alphabetically"),
        REVERSE_ALPHABETICALLY("Reverse Alphabetically"),
        RANK_LOW_HIGH("Rank: Low to High"),
        RANK_HIGH_LOW("Rank: High to Low"),
        NEWEST("Newest First"),
        OLDEST("Oldest First");


        private String name;
    }

}
