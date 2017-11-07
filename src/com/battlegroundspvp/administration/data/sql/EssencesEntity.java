package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Essences", schema = "mc2162")
public class EssencesEntity {

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
    @Column(name = "1_50")
    private int one50;
    @Generated(value = GenerationTime.INSERT)
    @Column(name = "1_100")
    private int one100;
    @Generated(value = GenerationTime.INSERT)
    @Column(name = "1_150")
    private int one150;
    @Generated(value = GenerationTime.INSERT)
    @Column(name = "3_50")
    private int three50;
    @Generated(value = GenerationTime.INSERT)
    @Column(name = "3_100")
    private int three100;
    @Generated(value = GenerationTime.INSERT)
    @Column(name = "3_150")
    private int three150;
    @Generated(value = GenerationTime.INSERT)
    @Column(name = "6_50")
    private int six50;
    @Generated(value = GenerationTime.INSERT)
    @Column(name = "6_100")
    private int six100;
    @Generated(value = GenerationTime.INSERT)
    @Column(name = "6_150")
    private int six150;

}
