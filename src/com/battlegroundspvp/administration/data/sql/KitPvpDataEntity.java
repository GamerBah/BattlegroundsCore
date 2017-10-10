package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "KitPvpData", schema = "mc2162")
public class KitPvpDataEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private GameProfilesEntity gameProfile;

    @Id
    @Column
    @GenericGenerator(name = "generator", strategy = "foreign",
            parameters = @org.hibernate.annotations.Parameter(name = "property", value = "gameProfile"))
    @GeneratedValue(generator = "generator")
    private int id;
    @Column(insertable = false)
    private int combatLevel;
    @Column(insertable = false)
    private int deaths;
    @Column(insertable = false)
    private int highestKillstreak;
    @Column(insertable = false)
    private int kills;
    @Column(insertable = false)
    private int killstreaksEnded;
    @Column(insertable = false)
    private int lastKilledBy;
    @Column(insertable = false)
    private int combatLogLevel;
    @Column(insertable = false)
    private int combatLogs;
    @Column(insertable = false)
    private String ownedKits;
    @Column(insertable = false)
    private String playersRated;
    @Column(insertable = false)
    private int revengeKills;
    @Column(insertable = false)
    private int souls;
    @Column(insertable = false)
    private String title;
}
