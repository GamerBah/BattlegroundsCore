package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 8/22/2016 */

import com.battlegroundspvp.administration.data.sql.EssencesEntity;
import com.battlegroundspvp.administration.donations.Essence;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class EssenceData {

    @Getter
    private final EssencesEntity entity;
    @Getter
    private final int id;
    @Getter
    private int one50, one100, one150, three50, three100, three150, six50, six100, six150;

    EssenceData(EssencesEntity entity) {
        this.entity = entity;
        this.id = entity.getId();
        this.one50 = entity.getOne50();
        this.one100 = entity.getOne100();
        this.one150 = entity.getOne150();
        this.three50 = entity.getThree50();
        this.three100 = entity.getThree100();
        this.three150 = entity.getThree150();
        this.six50 = entity.getSix50();
        this.six100 = entity.getSix100();
        this.six150 = entity.getSix150();
    }

    public void addEssence(Essence.Type type, int amount) {
        if (type.equals(Essence.Type.ONE_HOUR_50_PERCENT))
            this.one50 += amount;
        if (type.equals(Essence.Type.ONE_HOUR_100_PERCENT))
            this.one100 += amount;
        if (type.equals(Essence.Type.ONE_HOUR_150_PERCENT))
            this.one150 += amount;
        if (type.equals(Essence.Type.THREE_HOUR_50_PERCENT))
            this.three50 += amount;
        if (type.equals(Essence.Type.THREE_HOUR_100_PERCENT))
            this.three100 += amount;
        if (type.equals(Essence.Type.THREE_HOUR_150_PERCENT))
            this.three150 += amount;
        if (type.equals(Essence.Type.SIX_HOUR_50_PERCENT))
            this.six50 += amount;
        if (type.equals(Essence.Type.SIX_HOUR_100_PERCENT))
            this.six100 += amount;
        if (type.equals(Essence.Type.SIX_HOUR_150_PERCENT))
            this.six150 += amount;
    }

    public void removeEssence(Essence.Type type) {
        if (type.equals(Essence.Type.ONE_HOUR_50_PERCENT))
            this.one50--;
        if (type.equals(Essence.Type.ONE_HOUR_100_PERCENT))
            this.one100--;
        if (type.equals(Essence.Type.ONE_HOUR_150_PERCENT))
            this.one150--;
        if (type.equals(Essence.Type.THREE_HOUR_50_PERCENT))
            this.three50--;
        if (type.equals(Essence.Type.THREE_HOUR_100_PERCENT))
            this.three100--;
        if (type.equals(Essence.Type.THREE_HOUR_150_PERCENT))
            this.three150--;
        if (type.equals(Essence.Type.SIX_HOUR_50_PERCENT))
            this.six50--;
        if (type.equals(Essence.Type.SIX_HOUR_100_PERCENT))
            this.six100--;
        if (type.equals(Essence.Type.SIX_HOUR_150_PERCENT))
            this.six150--;
    }

    void sync() {
        entity.setOne50(this.one50);
        entity.setOne100(this.one100);
        entity.setOne150(this.one150);
        entity.setThree50(this.three50);
        entity.setThree100(this.three100);
        entity.setThree150(this.three150);
        entity.setSix50(this.six50);
        entity.setSix100(this.six100);
        entity.setSix150(this.six150);
    }
}
