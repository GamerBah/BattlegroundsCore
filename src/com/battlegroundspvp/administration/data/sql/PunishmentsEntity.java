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
    @JoinColumn(name = "id", nullable = false)
    private GameProfilesEntity gameProfilesEntity;

    public int hashCode() {
        int code = 0;
        code += punishmentId;
        code += date.hashCode();
        code += duration;
        code += enforcerId;
        code += expiration.hashCode();
        code += (pardoned ? 1 : 0);
        code += reason.hashCode();
        code += type.hashCode();
        return code;
    }
}
