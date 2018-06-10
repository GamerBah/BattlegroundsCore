package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 2/23/2018 */

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "GameProfile_Statistics", schema = "mc2162")
public class GameProfileStatisticsEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private GameProfilesEntity gameProfile;

    @Id
    @GenericGenerator(name = "generator", strategy = "foreign",
            parameters = @org.hibernate.annotations.Parameter(name = "property", value = "gameProfile"))
    @GeneratedValue(generator = "generator")
    private int id;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String dailyKills;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[{0=0}, {1=0}, {2=0}, {3=0}, {4=0}, {5=0}, {6=0}, {7=0}, {8=0}, {9=0}, {10=0}, {11=0}]")
    private String monthlyKills;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String yearlyKills;
    private long alltimeKills;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String dailyDeaths;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[{0=0}, {1=0}, {2=0}, {3=0}, {4=0}, {5=0}, {6=0}, {7=0}, {8=0}, {9=0}, {10=0}, {11=0}]")
    private String monthlyDeaths;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String yearlyDeaths;
    private long alltimeDeaths;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String dailySouls;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[{0=0}, {1=0}, {2=0}, {3=0}, {4=0}, {5=0}, {6=0}, {7=0}, {8=0}, {9=0}, {10=0}, {11=0}]")
    private String monthlySouls;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String yearlySouls;
    private long alltimeSouls;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String dailyCoins;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[{0=0}, {1=0}, {2=0}, {3=0}, {4=0}, {5=0}, {6=0}, {7=0}, {8=0}, {9=0}, {10=0}, {11=0}]")
    private String monthlyCoins;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String yearlyCoins;
    private long alltimeCoins;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String dailyHours;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[{0=0}, {1=0}, {2=0}, {3=0}, {4=0}, {5=0}, {6=0}, {7=0}, {8=0}, {9=0}, {10=0}, {11=0}]")
    private String monthlyHours;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String yearlyHours;
    private long alltimeHours;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String dailyKillstreaksEnded;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[{0=0}, {1=0}, {2=0}, {3=0}, {4=0}, {5=0}, {6=0}, {7=0}, {8=0}, {9=0}, {10=0}, {11=0}]")
    private String monthlyKillstreaksEnded;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String yearlyKillstreaksEnded;
    private long alltimeKillstreaksEnded;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String dailyRevengeKills;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[{0=0}, {1=0}, {2=0}, {3=0}, {4=0}, {5=0}, {6=0}, {7=0}, {8=0}, {9=0}, {10=0}, {11=0}]")
    private String monthlyRevengeKills;
    @Generated(value = GenerationTime.INSERT)
    @ColumnDefault(value = "[]")
    private String yearlyRevengeKills;
    private long alltimeRevengeKills;
    private long alltimeDuplicateKits;

}
