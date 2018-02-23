package com.battlegroundspvp.utils.inventories.sortingtypes;
/* Created by GamerBah on 12/22/2017 */

import com.battlegroundspvp.utils.enums.Rarity;
import com.battlegroundspvp.utils.inventories.ItemBuilder;

import java.util.Comparator;

public class RaritySort implements Comparator<ItemBuilder> {

    @Override
    public int compare(ItemBuilder itemBuilder1, ItemBuilder itemBuilder2) {
        if (!itemBuilder1.getStoredObjects().containsKey(Rarity.class))
            throw new IllegalArgumentException("itemBuilder1 doesn't have a stored Rarity key!");
        if (!itemBuilder2.getStoredObjects().containsKey(Rarity.class))
            throw new IllegalArgumentException("itemBuilder2 doesn't have a stored Rarity key!");
        return (((Rarity) itemBuilder1.getStoredObjects().get(Rarity.class)).compareTo(((Rarity) itemBuilder2.getStoredObjects().get(Rarity.class))));
    }

}
