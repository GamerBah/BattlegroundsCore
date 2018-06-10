package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 8/22/2016 */

import com.battlegroundspvp.administration.data.sql.CratesEntity;
import com.battlegroundspvp.administration.data.sql.GameProfilesEntity;
import com.battlegroundspvp.util.enums.Rarity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CratesData {

    @Getter
    private final GameProfilesEntity gameProfilesEntity;
    @Getter
    private final CratesEntity entity;
    @Getter
    private final int id;
    @Getter
    private int common, rare, epic, legendary, seasonal, gift;

    CratesData(CratesEntity entity) {
        this.entity = entity;
        this.gameProfilesEntity = entity.getGameProfile();
        this.id = entity.getId();
        this.common = entity.getCommon();
        this.rare = entity.getRare();
        this.epic = entity.getEpic();
        this.legendary = entity.getLegendary();
        this.seasonal = entity.getSeasonal();
        this.gift = entity.getGift();
    }

    public void addCrate(Rarity rarity, int amount) {
        if (rarity.equals(Rarity.COMMON))
            this.common += amount;
        if (rarity.equals(Rarity.RARE))
            this.rare += amount;
        if (rarity.equals(Rarity.EPIC))
            this.epic += amount;
        if (rarity.equals(Rarity.LEGENDARY))
            this.legendary += amount;
        if (rarity.equals(Rarity.SEASONAL))
            this.seasonal += amount;
        if (rarity.equals(Rarity.GIFT))
            this.gift += amount;
    }

    public void removeCrate(Rarity rarity) {
        if (rarity.equals(Rarity.COMMON))
            this.common--;
        if (rarity.equals(Rarity.RARE))
            this.rare--;
        if (rarity.equals(Rarity.EPIC))
            this.epic--;
        if (rarity.equals(Rarity.LEGENDARY))
            this.legendary--;
        if (rarity.equals(Rarity.SEASONAL))
            this.seasonal--;
        if (rarity.equals(Rarity.GIFT))
            this.gift--;
    }

    public int getTotal() {
        return common + rare + epic + legendary + seasonal + gift;
    }

    void sync() {
        entity.setCommon(this.common);
        entity.setRare(this.rare);
        entity.setEpic(this.epic);
        entity.setLegendary(this.legendary);
        entity.setSeasonal(this.seasonal);
        entity.setGift(this.gift);
    }
}
