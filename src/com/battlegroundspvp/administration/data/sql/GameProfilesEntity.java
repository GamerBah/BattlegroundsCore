package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "GameProfiles", schema = "mc2162")
public class GameProfilesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;
    @Generated(value = GenerationTime.INSERT)
    @Column(columnDefinition = "VARCHAR(36)")
    private String uuid;
    private int coins;
    private boolean dailyReward;
    @Generated(value = GenerationTime.INSERT)
    private LocalDateTime dailyRewardLast;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String friends;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String friendRequests;
    @Generated(value = GenerationTime.INSERT)
    private LocalDateTime lastOnline;
    private String name;
    private int playersRecruited;
    @Generated(value = GenerationTime.INSERT)
    private String rank;
    private int recruitedBy;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String cosmetics;
    @Generated(value = GenerationTime.INSERT)
    private String token;
    private String password;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private SettingsEntity settings;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameProfilesEntity")
    private List<PunishmentsEntity> punishments;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameProfilesEntity")
    private List<BugReportsEntity> bugReports;
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private KitPvpDataEntity kitPvpData = new KitPvpDataEntity();
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private EssencesEntity essences;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private CratesEntity crates;
}
