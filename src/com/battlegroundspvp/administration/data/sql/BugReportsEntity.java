package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 11/11/2017 */

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "BugReports", schema = "mc2162")
public class BugReportsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private int id;
    private int type;
    private int status;
    private int priority;
    private LocalDateTime submissionDate;
    private LocalDateTime dateResolved;
    private String bugMessage;
    private String recreationMessage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reportee")
    private GameProfilesEntity gameProfilesEntity;

}
