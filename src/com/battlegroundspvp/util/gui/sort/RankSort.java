package com.battlegroundspvp.util.gui.sort;
/* Created by GamerBah on 12/22/2017 */

import com.battlegroundspvp.administration.data.Rank;
import com.gamerbah.inventorytoolkit.ItemBuilder;

import java.util.Comparator;

public class RankSort implements Comparator<ItemBuilder> {

    @Override
    public int compare(ItemBuilder itemBuilder1, ItemBuilder itemBuilder2) {
        if (!itemBuilder1.getStoredObjects().containsKey(Rank.class))
            throw new IllegalArgumentException("itemBuilder1 doesn't have a stored Rank key!");
        if (!itemBuilder2.getStoredObjects().containsKey(Rank.class))
            throw new IllegalArgumentException("itemBuilder2 doesn't have a stored Rank key!");
        return Integer.compare(((Rank) itemBuilder1.getStoredObjects().get(Rank.class)).getLevel(), ((Rank) itemBuilder2.getStoredObjects().get(Rank.class)).getLevel());
    }
}
