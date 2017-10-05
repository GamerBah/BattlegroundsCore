package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 1/3/2017 */


import com.battlegroundspvp.Core;
import com.battlegroundspvp.administration.data.sql.KitPvpDataEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;

public class KitPvpData {

    @Getter
    private final KitPvpDataEntity entity;
    @Getter
    private final int id;
    @Getter
    @Setter
    private int kills, deaths, souls, combatRating, killstreaksEnded, revengeKills, highestKillstreak, lastKilledBy;
    @Getter
    @Setter
    private String playersRated, ownedKits, title;

    public KitPvpData(KitPvpDataEntity entity) {
        this.entity = entity;
        this.id = entity.getId();
        this.kills = entity.getKills();
        this.deaths = entity.getDeaths();
        this.souls = entity.getSouls();
        this.combatRating = entity.getCombatRating();
        this.killstreaksEnded = entity.getKillstreaksEnded();
        this.revengeKills = entity.getRevengeKills();
        this.highestKillstreak = entity.getHighestKillstreak();
        this.lastKilledBy = entity.getLastKilledBy();
        this.playersRated = entity.getPlayersRated();
        this.ownedKits = entity.getOwnedKits();
        this.title = entity.getTitle();
    }

    public void addKill(int amount) {
        setKills(getKills() + amount);
    }

    public void addDeath(int amount) {
        setDeaths(getDeaths() + amount);
    }

    public void addSouls(int amount) {
        setSouls(getSouls() + amount);
    }

    public void addCombatRating() {
        setCombatRating(getCombatRating() + 1);
    }

    public void addPlayerRated(int id) {
        setPlayersRated(this.playersRated + id + ",");
    }

    public void addOwnedKit(int kitId) {
        setOwnedKits(getOwnedKits() + kitId + ",");
    }

    public void addKillstreakEnded() {
        setKillstreaksEnded(getKillstreaksEnded() + 1);
    }

    public void addRevengeKill() {
        setRevengeKills(getRevengeKills() + 1);
    }

    public void sync() {
        KitPvpDataEntity entity = null;
        Session session = Core.getSessionFactory().openSession();
        session.beginTransaction();
        if (!session.createQuery("from KitPvpDataEntity where id = :id", KitPvpDataEntity.class)
                .setParameter("id", this.id).getResultList().isEmpty())
            entity = session.createQuery("from KitPvpDataEntity where id = :id", KitPvpDataEntity.class)
                    .setParameter("id", this.id).getSingleResult();
        if (entity != null) {
            entity.setKills(this.kills);
            entity.setDeaths(this.deaths);
            entity.setSouls(this.souls);
            entity.setCombatRating(this.combatRating);
            entity.setKillstreaksEnded(this.killstreaksEnded);
            entity.setRevengeKills(this.revengeKills);
            entity.setHighestKillstreak(this.highestKillstreak);
            entity.setLastKilledBy(this.lastKilledBy);
            entity.setPlayersRated(this.playersRated);
            entity.setOwnedKits(this.ownedKits);
            entity.setTitle(this.title);
        }
        session.getTransaction().commit();
        session.close();
    }
}
