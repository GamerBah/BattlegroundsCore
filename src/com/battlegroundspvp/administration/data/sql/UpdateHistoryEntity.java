package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 6/11/2018 */

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Update_History", schema = "mc2162")
public class UpdateHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private int id;
    private LocalDateTime date;
    private int result;
    private String notes;

}
