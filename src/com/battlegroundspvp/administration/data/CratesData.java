package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 8/22/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.sql.CratesEntity;
import com.battlegroundspvp.administration.data.sql.GameProfilesEntity;
import com.battlegroundspvp.administration.donations.Crate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.Session;

@AllArgsConstructor
public class CratesData {

    @Getter
    private final GameProfilesEntity gameProfilesEntity;
    @Getter
    private final CratesEntity entity;
    @Getter
    private final int id;
    @Getter
    private int common, rare, epic, legendary, limited, gift;

    CratesData(CratesEntity entity) {
        this.entity = entity;
        this.gameProfilesEntity = entity.getGameProfile();
        this.id = entity.getId();
        this.common = entity.getCommon();
        this.rare = entity.getRare();
        this.epic = entity.getEpic();
        this.legendary = entity.getLegendary();
        this.limited = entity.getLimited();
        this.gift = entity.getGift();
    }

    public void addCrate(Crate.Type type, int amount) {
        if (type.equals(Crate.Type.COMMON))
            this.common += amount;
        if (type.equals(Crate.Type.RARE))
            this.rare += amount;
        if (type.equals(Crate.Type.EPIC))
            this.epic += amount;
        if (type.equals(Crate.Type.LEGENDARY))
            this.legendary += amount;
        if (type.equals(Crate.Type.LIMITED))
            this.limited += amount;
        if (type.equals(Crate.Type.GIFT))
            this.gift += amount;
    }

    public void removeEssence(Crate.Type type) {
        if (type.equals(Crate.Type.COMMON))
            this.common--;
        if (type.equals(Crate.Type.RARE))
            this.rare--;
        if (type.equals(Crate.Type.EPIC))
            this.epic--;
        if (type.equals(Crate.Type.LEGENDARY))
            this.legendary--;
        if (type.equals(Crate.Type.LIMITED))
            this.limited--;
        if (type.equals(Crate.Type.GIFT))
            this.gift--;
    }

    public int getTotal() {
        return common + rare + epic + legendary + limited + gift;
    }

    void sync() {
        Session session = BattlegroundsCore.getSessionFactory().openSession();
        session.beginTransaction();
        entity.setCommon(this.common);
        entity.setRare(this.rare);
        entity.setEpic(this.epic);
        entity.setLegendary(this.legendary);
        entity.setLimited(this.limited);
        entity.setGift(this.gift);
        session.getTransaction().commit();
        session.close();
    }
}
