package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Settings", schema = "mc2162")
public class SettingsEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private GameProfilesEntity gameProfile;

    @Id
    @Column
    @GenericGenerator(name = "generator", strategy = "foreign",
            parameters = @Parameter(name = "property", value = "gameProfile"))
    @GeneratedValue(generator = "generator")
    private int id;
    @Column(insertable = false)
    @Generated(value = GenerationTime.INSERT)
    private String particleQuality;
    @Column(insertable = false)
    private boolean privateMessaging;
    @Column(insertable = false)
    private boolean stealthyJoin;
    @Column(insertable = false)
    private boolean teamRequests;

}
