package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 10/24/2017 */

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Cosmetics", schema = "mc2162")
public class CosmeticsEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private GameProfilesEntity gameProfile;

    @Id
    @Column
    @GenericGenerator(name = "generator", strategy = "foreign",
            parameters = @org.hibernate.annotations.Parameter(name = "property", value = "gameProfile"))
    @GeneratedValue(generator = "generator")
    private int id;
    @Generated(value = GenerationTime.INSERT)
    private String lobby;
    @Generated(value = GenerationTime.INSERT)
    private String kitPvp;

}
