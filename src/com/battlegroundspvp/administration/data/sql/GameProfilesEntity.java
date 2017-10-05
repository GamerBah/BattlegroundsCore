package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "GameProfiles", schema = "mc2162")
public class GameProfilesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID uuid;
    @Column(insertable = false)
    private int coins;
    @Column(insertable = false)
    private String cosmetics;
    @Column(insertable = false)
    private boolean dailyReward;
    @Column(insertable = false)
    private LocalDateTime dailyRewardLast;
    @Column(insertable = false)
    private String friends;
    @Column(insertable = false)
    @Generated(value = GenerationTime.INSERT)
    private String gore;
    @Column(insertable = false)
    private LocalDateTime lastOnline;
    @Column
    private String name;
    @Column(insertable = false)
    private int playersRecruited;
    @Column(insertable = false)
    @Generated(value = GenerationTime.INSERT)
    private String rank;
    @Column(insertable = false)
    @Generated(value = GenerationTime.INSERT)
    private int recruitedBy;
    @Column(insertable = false)
    @Generated(value = GenerationTime.INSERT)
    private String trail;
    @Column(insertable = false)
    @Generated(value = GenerationTime.INSERT)
    private String warcry;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private SettingsEntity settings;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private List<PunishmentsEntity> punishments;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private KitPvpDataEntity kitPvpData = new KitPvpDataEntity();
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private EssencesEntity essences;
}
