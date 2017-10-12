package com.battlegroundspvp.utils.inventories;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * This is a chainable builder for {@link ItemStack}s in {@link Bukkit}
 * <br>
 * Example Usage:<br>
 * {@code ItemStack is = new ItemBuilder(Material.LEATHER_HELMET).amount(2).data(4).durability(4).enchantment(Enchantment.ARROW_INFINITE).enchantment(Enchantment.LUCK, 2).name(ChatColor.RED + "the name").lore(ChatColor.GREEN + "line 1").lore(ChatColor.BLUE + "line 2").color(Color.MAROON);
 *
 * @author MiniDigger, computerwizjared, GamerBah
 * @version 1.2
 */
public class ItemBuilder extends ItemStack {

    @Getter
    private HashSet<ClickEvent> clickEvents = new HashSet<>();
    @Getter
    private HashMap<Class, Object> storedObjects = new HashMap<>();
    /**
     * Initializes the builder with the given {@link Material}
     *
     * @param mat the {@link Material} to start the builder from
     * @since 1.0
     */
    public ItemBuilder(final Material mat) {
        super(mat);
    }

    /**
     * Inits the builder with the given {@link ItemStack}
     *
     * @param is the {@link ItemStack} to start the builder from
     * @since 1.0
     */
    public ItemBuilder(final ItemStack is) {
        super(is);
    }

    public ItemBuilder(final ItemBuilder ib) {
        setType(ib.getType());
        setData(ib.getData());
        setAmount(ib.getAmount());
        setItemMeta(ib.getItemMeta());
        setDurability(ib.getDurability());
        for (ClickEvent clickEvent : ib.clickEvents)
            clickEvent(clickEvent);
        storedObjects.putAll(ib.getStoredObjects());
    }

    public ItemBuilder amount(final int amount) {
        setAmount(amount);
        return this;
    }

    public ItemBuilder name(final String name) {
        final ItemMeta meta = getItemMeta();
        meta.setDisplayName(name);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(final String text) {
        final ItemMeta meta = getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(text);
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder durability(final int durability) {
        setDurability((short) durability);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder data(final int data) {
        setData(new MaterialData(getType(), (byte) data));
        return this;
    }

    public ItemBuilder enchantment(final Enchantment enchantment, final int level) {
        addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(final Enchantment enchantment) {
        addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder type(final Material material) {
        setType(material);
        return this;
    }

    public ItemBuilder clearLore() {
        final ItemMeta meta = getItemMeta();
        meta.setLore(new ArrayList<>());
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        getEnchantments().keySet().forEach(this::removeEnchantment);
        return this;
    }

    public ItemBuilder color(Color color) {
        if (getType() == Material.LEATHER_BOOTS || getType() == Material.LEATHER_CHESTPLATE || getType() == Material.LEATHER_HELMET
                || getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();
            meta.setColor(color);
            setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        }
    }

    public ItemBuilder flag(ItemFlag flag) {
        final ItemMeta meta = getItemMeta();
        meta.addItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearFlags() {
        final ItemMeta meta = getItemMeta();
        meta.getItemFlags().forEach(meta::removeItemFlags);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder unbreakable() {
        final ItemMeta meta = getItemMeta();
        meta.setUnbreakable(true);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder clickEvent(ClickEvent event) {
        getClickEvents().add(event);
        return this;
    }

    public ItemBuilder storeObject(Class holdingClass, Object object) {
        this.storedObjects.put(holdingClass, object);
        return this;
    }

    public ItemBuilder clone() {
        return new ItemBuilder(this);
    }
}
