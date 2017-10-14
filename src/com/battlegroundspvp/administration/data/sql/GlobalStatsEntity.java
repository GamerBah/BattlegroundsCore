package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "GlobalStats", schema = "mc2162")
public class GlobalStatsEntity {

    @Id
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

}
