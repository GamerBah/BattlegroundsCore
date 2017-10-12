package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 9/28/2017 */

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Punishments", schema = "mc2162")
public class PunishmentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private int punishmentId;
    private LocalDateTime date;
    private int duration;
    private int enforcerId;
    private LocalDateTime expiration;
    private boolean pardoned;
    private String reason;
    private String type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private GameProfilesEntity gameProfilesEntity;
}
