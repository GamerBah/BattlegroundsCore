package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "GlobalStats", schema = "mc2162")
public class GlobalStatsEntity {

    @Id
    @Column
    private int id;
    @Column(insertable = false)
    private int totalArrowsFired;
    @Column(insertable = false)
    private int totalBans;
    @Column(insertable = false)
    private int totalCoinsEarned;
    @Column(insertable = false)
    private int totalDeaths;
    @Column(insertable = false)
    private int totalEnderpearlsThrown;
    @Column(insertable = false)
    private int totalKicks;
    @Column(insertable = false)
    private int totalKills;
    @Column(insertable = false)
    private int totalKillstreaksEnded;
    @Column(insertable = false)
    private int totalMutes;
    @Column(insertable = false)
    private int totalSoulsEarned;
    @Column(insertable = false)
    private int totalSuicides;
    @Column(insertable = false)
    private int totalUniqueJoins;
    @Column(insertable = false)
    private int totalUsedEssences;

}
