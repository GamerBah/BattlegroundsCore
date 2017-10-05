package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import javax.persistence.*;

@Entity
@Table(name = "GlobalStats", schema = "mc2162")
public class GlobalStatsEntity {


    private int id;
    private int totalArrowsFired;
    private int totalBans;
    private int totalCoinsEarned;
    private int totalDeaths;
    private int totalEnderpearlsThrown;
    private int totalKicks;
    private int totalKills;
    private int totalKillstreaksEnded;
    private int totalMutes;
    private int totalSoulsEarned;
    private int totalSuicides;
    private int totalUniqueJoins;
    private int totalUsedEssences;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GlobalStatsEntity that = (GlobalStatsEntity) o;

        if (id != that.id) return false;
        if (totalKills != that.totalKills) return false;
        if (totalDeaths != that.totalDeaths) return false;
        if (totalSoulsEarned != that.totalSoulsEarned) return false;
        if (totalCoinsEarned != that.totalCoinsEarned) return false;
        if (totalUniqueJoins != that.totalUniqueJoins) return false;
        if (totalMutes != that.totalMutes) return false;
        if (totalKicks != that.totalKicks) return false;
        if (totalBans != that.totalBans) return false;
        if (totalSuicides != that.totalSuicides) return false;
        if (totalUsedEssences != that.totalUsedEssences) return false;
        if (totalArrowsFired != that.totalArrowsFired) return false;
        if (totalEnderpearlsThrown != that.totalEnderpearlsThrown) return false;
        if (totalKillstreaksEnded != that.totalKillstreaksEnded) return false;

        return true;
    }

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "totalArrowsFired")
    public int getTotalArrowsFired() {
        return totalArrowsFired;
    }

    public void setTotalArrowsFired(int totalArrowsFired) {
        this.totalArrowsFired = totalArrowsFired;
    }

    @Basic
    @Column(name = "totalBans")
    public int getTotalBans() {
        return totalBans;
    }

    public void setTotalBans(int totalBans) {
        this.totalBans = totalBans;
    }

    @Basic
    @Column(name = "totalCoinsEarned")
    public int getTotalCoinsEarned() {
        return totalCoinsEarned;
    }

    public void setTotalCoinsEarned(int totalCoinsEarned) {
        this.totalCoinsEarned = totalCoinsEarned;
    }

    @Basic
    @Column(name = "totalDeaths")
    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    @Basic
    @Column(name = "totalEnderpearlsThrown")
    public int getTotalEnderpearlsThrown() {
        return totalEnderpearlsThrown;
    }

    public void setTotalEnderpearlsThrown(int totalEnderpearlsThrown) {
        this.totalEnderpearlsThrown = totalEnderpearlsThrown;
    }

    @Basic
    @Column(name = "totalKicks")
    public int getTotalKicks() {
        return totalKicks;
    }

    public void setTotalKicks(int totalKicks) {
        this.totalKicks = totalKicks;
    }

    @Basic
    @Column(name = "totalKills")
    public int getTotalKills() {
        return totalKills;
    }

    public void setTotalKills(int totalKills) {
        this.totalKills = totalKills;
    }

    @Basic
    @Column(name = "totalKillstreaksEnded")
    public int getTotalKillstreaksEnded() {
        return totalKillstreaksEnded;
    }

    public void setTotalKillstreaksEnded(int totalKillstreaksEnded) {
        this.totalKillstreaksEnded = totalKillstreaksEnded;
    }

    @Basic
    @Column(name = "totalMutes")
    public int getTotalMutes() {
        return totalMutes;
    }

    public void setTotalMutes(int totalMutes) {
        this.totalMutes = totalMutes;
    }

    @Basic
    @Column(name = "totalSoulsEarned")
    public int getTotalSoulsEarned() {
        return totalSoulsEarned;
    }

    public void setTotalSoulsEarned(int totalSoulsEarned) {
        this.totalSoulsEarned = totalSoulsEarned;
    }

    @Basic
    @Column(name = "totalSuicides")
    public int getTotalSuicides() {
        return totalSuicides;
    }

    public void setTotalSuicides(int totalSuicides) {
        this.totalSuicides = totalSuicides;
    }

    @Basic
    @Column(name = "totalUniqueJoins")
    public int getTotalUniqueJoins() {
        return totalUniqueJoins;
    }

    public void setTotalUniqueJoins(int totalUniqueJoins) {
        this.totalUniqueJoins = totalUniqueJoins;
    }

    @Basic
    @Column(name = "totalUsedEssences")
    public int getTotalUsedEssences() {
        return totalUsedEssences;
    }

    public void setTotalUsedEssences(int totalUsedEssences) {
        this.totalUsedEssences = totalUsedEssences;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + totalKills;
        result = 31 * result + totalDeaths;
        result = 31 * result + totalSoulsEarned;
        result = 31 * result + totalCoinsEarned;
        result = 31 * result + totalUniqueJoins;
        result = 31 * result + totalMutes;
        result = 31 * result + totalKicks;
        result = 31 * result + totalBans;
        result = 31 * result + totalSuicides;
        result = 31 * result + totalUsedEssences;
        result = 31 * result + totalArrowsFired;
        result = 31 * result + totalEnderpearlsThrown;
        result = 31 * result + totalKillstreaksEnded;
        return result;
    }
}
