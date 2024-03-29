package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 1/3/2017 */


import com.battlegroundspvp.administration.data.sql.KitPvpDataEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class KitPvpData {


    @Getter
    private final KitPvpDataEntity entity;
    @Getter
    private final int id;
    @Getter
    private final GameProfile gameProfile;

    @Getter
    @Setter
    private int kills, deaths, souls, combatLevel, killstreaksEnded, revengeKills,
            highestKillstreak, lastKilledBy, combatLogLevel, combatLogs, activeTrail, activeWarcry, activeGore;
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private ArrayList<Integer> ownedKits, duplicateKits, quickSelectKits;

    KitPvpData(KitPvpDataEntity entity, GameProfile gameProfile) {
        this.entity = entity;
        this.id = entity.getId();
        this.gameProfile = gameProfile;
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
        this.activeTrail = entity.getActiveTrail();
        this.activeWarcry = entity.getActiveWarcry();
        this.activeGore = entity.getActiveGore();
        this.title = entity.getTitle();
        ArrayList<Integer> ownedKits = new ArrayList<>();
        if (entity.getOwnedKits() != null) {
            String owned = entity.getOwnedKits().replace("[", "").replace("]", "").replace(" ", "");
            if (!owned.equals(""))
                for (String id : owned.split(","))
                    ownedKits.add(Integer.parseInt(id));
            else ownedKits.add(1);
        }
        this.ownedKits = ownedKits;
        ArrayList<Integer> duplicateKits = new ArrayList<>();
        if (entity.getDuplicateKits() != null) {
            String duplicate = entity.getDuplicateKits().replace("[", "").replace("]", "").replace(" ", "");
            if (!duplicate.equals(""))
                for (String id : duplicate.split(","))
                    duplicateKits.add(Integer.parseInt(id));
        }
        this.duplicateKits = duplicateKits;
        ArrayList<Integer> quickSelectKits = new ArrayList<>();
        if (entity.getQuickSelectKits() != null) {
            String quickSelect = entity.getQuickSelectKits().replace("[", "").replace("]", "").replace(" ", "");
            if (!quickSelect.equals(""))
                for (String id : quickSelect.split(","))
                    quickSelectKits.add(Integer.parseInt(id));
        }
        this.quickSelectKits = quickSelectKits;
    }

    public KitPvpData addKills(int amount) {
        setKills(getKills() + amount);
        //getGameProfile().getStatistics().updateKills(amount);
        return this;
    }

    public KitPvpData addKill() {
        addKills(1);
        return this;
    }

    public KitPvpData addDeaths(int amount) {
        setDeaths(getDeaths() + amount);
        //getGameProfile().getStatistics().updateDeaths(amount);
        return this;
    }

    public KitPvpData addDeath() {
        addDeaths(1);
        return this;
    }

    public KitPvpData addSouls(int amount) {
        setSouls(getSouls() + amount);
        //getGameProfile().getStatistics().updateSouls(amount);
        return this;
    }

    public KitPvpData addCombatLog() {
        setCombatLogs(getCombatLogs() + 1);
        return this;
    }

    public KitPvpData addOwnedKit(int kitId) {
        ownedKits.add(kitId);
        return this;
    }

    public KitPvpData addDuplicateKit(int kitId) {
        duplicateKits.add(kitId);
        //getGameProfile().getStatistics().setAlltimeDuplicateKits(getGameProfile().getStatistics().getAlltimeDuplicateKits() + 1);
        return this;
    }

    public KitPvpData addKillstreakEnded() {
        setKillstreaksEnded(getKillstreaksEnded() + 1);
        //getGameProfile().getStatistics().updateKillstreaksEnded(1);
        return this;
    }

    public KitPvpData addRevengeKill() {
        setRevengeKills(getRevengeKills() + 1);
        //getGameProfile().getStatistics().updateRevengeKills(1);
        return this;
    }

    public KitPvpData addCombatRating() {
        setCombatLevel(getCombatLevel() + 1);
        return this;
    }

    void fullSync() {
        entity.setKills(this.kills);
        entity.setDeaths(this.deaths);
        entity.setSouls(this.souls);
        entity.setCombatLevel(this.combatLevel);
        entity.setKillstreaksEnded(this.killstreaksEnded);
        entity.setRevengeKills(this.revengeKills);
        entity.setHighestKillstreak(this.highestKillstreak);
        entity.setLastKilledBy(this.lastKilledBy);
        entity.setActiveTrail(this.activeTrail);
        entity.setActiveWarcry(this.activeWarcry);
        entity.setActiveGore(this.activeGore);
        entity.setTitle(this.title);
        entity.setOwnedKits(this.ownedKits.toString());
        entity.setDuplicateKits(this.duplicateKits.toString());
        entity.setQuickSelectKits(this.quickSelectKits.toString());
    }

    void partialSync() {
        entity.setKills(this.kills);
        entity.setDeaths(this.deaths);
        entity.setSouls(this.souls);
        entity.setCombatLevel(this.combatLevel);
        entity.setKillstreaksEnded(this.killstreaksEnded);
        entity.setRevengeKills(this.revengeKills);
        entity.setHighestKillstreak(this.highestKillstreak);
        entity.setTitle(this.title);
        entity.setOwnedKits(this.ownedKits.toString());
        entity.setDuplicateKits(this.duplicateKits.toString());
        entity.setQuickSelectKits(this.quickSelectKits.toString());
    }
}
