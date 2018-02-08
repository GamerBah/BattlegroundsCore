package com.battlegroundspvp.utils.inventories.sortingtypes;
/* Created by GamerBah on 12/22/2017 */

import com.battlegroundspvp.utils.inventories.ItemBuilder;

import java.util.Comparator;

public class AlphabeticalSort implements Comparator<ItemBuilder> {

    @Override
    public int compare(ItemBuilder itemBuilder1, ItemBuilder itemBuilder2) {
        if (!itemBuilder1.getStoredObjects().containsKey(String.class))
            throw new IllegalArgumentException("itemBuilder1 doesn't have a stored String key!");
        if (!itemBuilder2.getStoredObjects().containsKey(String.class))
            throw new IllegalArgumentException("itemBuilder2 doesn't have a stored String key!");
        return ((String) itemBuilder1.getStoredObjects().get(String.class)).compareTo((String) itemBuilder2.getStoredObjects().get(String.class));
    }

}
