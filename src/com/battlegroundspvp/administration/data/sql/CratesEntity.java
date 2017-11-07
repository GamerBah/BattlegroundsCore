package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 10/12/2017 */

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Crates", schema = "mc2162")
public class CratesEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private GameProfilesEntity gameProfile;

    @Id
    @Column
    @GenericGenerator(name = "generator", strategy = "foreign",
            parameters = @org.hibernate.annotations.Parameter(name = "property", value = "gameProfile"))
    @GeneratedValue(generator = "generator")
    private int id;
    @Column(name = "common", insertable = false)
    private int common;
    @Column(name = "rare", insertable = false)
    private int rare;
    @Column(name = "epic", insertable = false)
    private int epic;
    @Column(name = "legendary", insertable = false)
    private int legendary;
    @Column(name = "seasonal", insertable = false)
    private int seasonal;
    @Column(name = "gift", insertable = false)
    private int gift;

}
