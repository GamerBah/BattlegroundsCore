package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 2/12/2018 */

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "ServerData", schema = "mc2162")
public class ServerDataEntity {

    @Id
    private int id;
    private int playersOnline;
    private int status;
    private int ramUsed;
    private int ramFree;
    private int ramTotal;
    private double cpuUsage;
    private double curTps;
    private double avgTps;
    @Generated(value = GenerationTime.INSERT)
    private LocalDate snapshotDay;
    @Generated(value = GenerationTime.INSERT)
    private LocalDate snapshotMonth;
    @Generated(value = GenerationTime.INSERT)
    private LocalDate snapshotYear;

}
