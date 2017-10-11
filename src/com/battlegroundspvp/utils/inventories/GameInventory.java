package com.battlegroundspvp.utils.inventories;
/* Created by GamerBah on 7/22/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.enums.EventSound;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;

public class GameInventory {

    private Inventory inventory;
    private int itemCount;
    private ArrayList<ItemBuilder> sortables = new ArrayList<>();
    private HashMap<Integer, ItemBuilder> clickables = new HashMap<>();
    private Type type;
    private GameInventory previousInventory;
    @Getter
    private int slotOffset;
    @Getter
    private boolean biDirectionalOffset;

    public GameInventory(String name, int itemCount, Type type, GameInventory previousInventory) {
        this.inventory = BattlegroundsCore.getInstance().getServer().createInventory(null, 54, name);
        this.itemCount = itemCount;
        this.type = type;
        this.previousInventory = previousInventory;
        this.slotOffset = 0;
        this.biDirectionalOffset = false;
    }

    public GameInventory(String name, int itemCount, Type type, GameInventory previousInventory, int slotOffest) {
        this.inventory = BattlegroundsCore.getInstance().getServer().createInventory(null, 54, name);
        this.itemCount = itemCount;
        this.type = type;
        this.previousInventory = previousInventory;
        this.slotOffset = slotOffest;
        this.biDirectionalOffset = false;
    }

    public GameInventory(String name, int itemCount, Type type, GameInventory previousInventory, int slotOffest, boolean biDirectionalOffset) {
        this.inventory = BattlegroundsCore.getInstance().getServer().createInventory(null, 54, name);
        this.itemCount = itemCount;
        this.type = type;
        this.previousInventory = previousInventory;
        this.slotOffset = slotOffest;
        this.biDirectionalOffset = biDirectionalOffset;
    }

    public GameInventory(String name, GameInventory previousInventory) {
        this.inventory = BattlegroundsCore.getInstance().getServer().createInventory(null, 54, name);
        this.itemCount = 0;
        this.type = Type.STANDARD;
        this.previousInventory = previousInventory;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    protected int getItemCount() {
        return itemCount;
    }

    protected void addSortableItem(ItemBuilder itemBuilder) {
        sortables.add(itemBuilder.clone());
    }

    public ArrayList<ItemBuilder> getSortables() {
        ArrayList<ItemBuilder> items = new ArrayList<>();
        for (ItemBuilder item : this.sortables)
            items.add(item.clone());
        return items;
    }

    protected void addClickableItem(final int slot, final ItemBuilder itemBuilder) {
        this.clickables.put(slot, itemBuilder);
        this.inventory.setItem(slot, itemBuilder);
    }

    public HashMap<Integer, ItemBuilder> getClickables() {
        HashMap<Integer, ItemBuilder> hashMap = new HashMap<>();
        for (int key : this.clickables.keySet())
            hashMap.put(key, this.clickables.get(key).clone());
        return hashMap;
    }

    public Type getType() {
        return type;
    }

    protected GameInventory getPreviousInventory() {
        return previousInventory;
    }

    public void openPreviousInventory(final Player player) {
        if (this.getPreviousInventory() != null) {
            new InventoryBuilder(player, this.getPreviousInventory()).open();
        } else {
            player.closeInventory();
        }
        EventSound.playSound(player, EventSound.INVENTORY_GO_BACK);
    }

    protected void setItemCount(int amount) {
        itemCount = amount;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public enum Type {
        PLAYER_SEARCH, PUNISH_SEARCH, FRIEND_SEARCH, STANDARD
    }

}
