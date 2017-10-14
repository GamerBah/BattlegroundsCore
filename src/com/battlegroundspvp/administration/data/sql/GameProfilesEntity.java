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
    private int coins;
    private String cosmetics;
    private boolean dailyReward;
    private LocalDateTime dailyRewardLast;
    private String friends;
    @Generated(value = GenerationTime.INSERT)
    private String gore;
    private LocalDateTime lastOnline;
    private String name;
    private int playersRecruited;
    @Generated(value = GenerationTime.INSERT)
    private String rank;
    @Generated(value = GenerationTime.INSERT)
    private int recruitedBy;
    @Generated(value = GenerationTime.INSERT)
    private String trail;
    @Generated(value = GenerationTime.INSERT)
    private String warcry;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private SettingsEntity settings;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameProfilesEntity")
    private List<PunishmentsEntity> punishments;
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private KitPvpDataEntity kitPvpData = new KitPvpDataEntity();
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private EssencesEntity essences;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private CratesEntity crates;
}
