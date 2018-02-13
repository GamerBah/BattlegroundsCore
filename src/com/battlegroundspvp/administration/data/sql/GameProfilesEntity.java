package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import lombok.Data;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "GameProfiles", schema = "mc2162")
public class GameProfilesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;
    private String uuid;
    private int rank;
    private boolean online;
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
    private int recruitedBy;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String cosmetics;
    @Generated(value = GenerationTime.INSERT)
    private String token;
    private String password;
    private String email;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private SettingsEntity settings;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "gameProfilesEntity")
    private Set<PunishmentsEntity> punishments;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "gameProfilesEntity")
    private Set<BugReportsEntity> bugReports;
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private KitPvpDataEntity kitPvpData = new KitPvpDataEntity();
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private EssencesEntity essences;
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "gameProfile", cascade = CascadeType.ALL)
    private CratesEntity crates;

    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode += id;
        hashCode += uuid.hashCode();
        hashCode += getRank();
        hashCode += coins;
        hashCode += playersRecruited;
        hashCode += recruitedBy;
        hashCode += token.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
