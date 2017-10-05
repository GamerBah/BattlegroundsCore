package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "Punishments", schema = "mc2162")
public class PunishmentsEntity {

    @ManyToOne
    @JoinColumn(name = "id", insertable = false, updatable = false)
    private GameProfilesEntity gameProfile;

    @Id
    @Column
    private int id;
    @Column(insertable = false)
    private LocalDateTime date;
    @Column(insertable = false)
    private int duration;
    @Column(insertable = false)
    private UUID enforcer;
    @Column(insertable = false)
    private LocalDateTime expiration;
    @Column(insertable = false)
    private boolean pardoned;
    @Column(insertable = false)
    private String reason;
    @Column(insertable = false)
    private String type;
}
