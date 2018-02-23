package com.battlegroundspvp.administration.data.sql;
/* Created by GamerBah on 2/12/2018 */

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ServerData", schema = "mc2162")
public class ServerDataEntity {

    @Id
    private int id;
    private int playersOnline;
    @ColumnDefault(value = "[]")
    private String onlineStaff;
    private int status;
    private int ramUsed;
    private int ramFree;
    private int ramTotal;
    private double cpuUsage;
    private double curTps;
    private double avgTps;

}
