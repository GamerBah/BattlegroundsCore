package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 1/3/2017 */


import com.battlegroundspvp.BattlegroundsCore;
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
    private int kills, deaths, souls, combatLevel, killstreaksEnded, revengeKills, highestKillstreak, lastKilledBy, combatLogLevel, combatLogs;
    @Getter
    @Setter
    private String playersRated, ownedKits, title;

    KitPvpData(KitPvpDataEntity entity) {
        this.entity = entity;
        this.id = entity.getId();
        this.kills = entity.getKills();
        this.deaths = entity.getDeaths();
        this.souls = entity.getSouls();
        this.combatLevel = entity.getCombatLevel();
        this.killstreaksEnded = entity.getKillstreaksEnded();
        this.revengeKills = entity.getRevengeKills();
        this.highestKillstreak = entity.getHighestKillstreak();
        this.lastKilledBy = entity.getLastKilledBy();
        this.combatLogs = entity.getCombatLogs();
        this.combatLogLevel = entity.getCombatLogLevel();
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

    public void addCombatLog() {
        setCombatLogs(getCombatLogs() + 1);
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

    public void addCombatRating() {
        setCombatLevel(getCombatLevel() + 1);
    }

    void sync() {
        Session session = BattlegroundsCore.getSessionFactory().openSession();
        session.beginTransaction();
        entity.setKills(this.kills);
        entity.setDeaths(this.deaths);
        entity.setSouls(this.souls);
        entity.setCombatLevel(this.combatLevel);
        entity.setKillstreaksEnded(this.killstreaksEnded);
        entity.setRevengeKills(this.revengeKills);
        entity.setHighestKillstreak(this.highestKillstreak);
        entity.setLastKilledBy(this.lastKilledBy);
        entity.setPlayersRated(this.playersRated);
        entity.setOwnedKits(this.ownedKits);
        entity.setTitle(this.title);
        session.getTransaction().commit();
        session.close();
    }
}
