package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
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
    @GenericGenerator(name = "generator", strategy = "foreign",
            parameters = @org.hibernate.annotations.Parameter(name = "property", value = "gameProfile"))
    @GeneratedValue(generator = "generator")
    private int id;
    private int combatLevel;
    private int deaths;
    private int highestKillstreak;
    private int kills;
    private int killstreaksEnded;
    private int lastKilledBy;
    private int combatLogLevel;
    private int combatLogs;
    private int activeTrail;
    private int activeWarcry;
    private int activeGore;
    private int revengeKills;
    private int souls;
    @Generated(value = GenerationTime.INSERT)
    private String title;
    private String ownedKits;
    private String duplicateKits;
    private String quickSelectKits;
}
