package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import lombok.Data;
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
    @Column(name = "1_50", insertable = false)
    private int one50;
    @Column(name = "1_100", insertable = false)
    private int one100;
    @Column(name = "1_150", insertable = false)
    private int one150;
    @Column(name = "3_50", insertable = false)
    private int three50;
    @Column(name = "3_100", insertable = false)
    private int three100;
    @Column(name = "3_150", insertable = false)
    private int three150;
    @Column(name = "6_50", insertable = false)
    private int six50;
    @Column(name = "6_100", insertable = false)
    private int six100;
    @Column(name = "6_150", insertable = false)
    private int six150;

}
