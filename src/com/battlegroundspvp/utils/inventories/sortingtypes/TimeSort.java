package com.battlegroundspvp.utils.inventories.sortingtypes;
/* Created by GamerBah on 12/22/2017 */

import com.battlegroundspvp.utils.inventories.ItemBuilder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;

public class TimeSort implements Comparator<ItemBuilder> {

    @Override
    public int compare(ItemBuilder itemBuilder1, ItemBuilder itemBuilder2) {
        if (!itemBuilder1.getStoredObjects().containsKey(LocalDateTime.class))
            throw new IllegalArgumentException("itemBuilder1 doesn't have a stored LocalDateTime key!");
        if (!itemBuilder2.getStoredObjects().containsKey(LocalDateTime.class))
            throw new IllegalArgumentException("itemBuilder2 doesn't have a stored LocalDateTime key!");
        return Long.compare(((LocalDateTime) itemBuilder1.getStoredObjects().get(LocalDateTime.class)).toEpochSecond(ZoneOffset.UTC),
                ((LocalDateTime) itemBuilder2.getStoredObjects().get(LocalDateTime.class)).toEpochSecond(ZoneOffset.UTC));
    }
}
