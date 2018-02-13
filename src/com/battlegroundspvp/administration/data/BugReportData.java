package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 11/11/2017 */

import com.battlegroundspvp.administration.data.sql.BugReportsEntity;
import com.battlegroundspvp.administration.data.sql.GameProfilesEntity;
import com.battlegroundspvp.utils.BugReport;
import lombok.Getter;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Set;

public class BugReportData {

    @Getter
    private final int id;
    @Getter
    private ArrayList<BugReport> bugReports;
    private ArrayList<BugReport> removed = new ArrayList<>();
    @Getter
    private Set<BugReportsEntity> entities;
    @Getter
    private GameProfilesEntity gameProfilesEntity;

    BugReportData(Set<BugReportsEntity> list, GameProfilesEntity gameProfilesEntity) {
        this.id = gameProfilesEntity.getId();
        ArrayList<BugReport> bugReports = new ArrayList<>();
        if (list != null) {
            list.forEach(entity -> {
                BugReport bugReport = new BugReport(entity.getId(), BugReport.Type.ofId(entity.getType()));
                bugReport.setStatus(BugReport.Status.ofId(entity.getStatus()));
                bugReport.setPriority(BugReport.Priority.ofId(entity.getPriority()));
                bugReport.setSubmissionDate(entity.getSubmissionDate());
                bugReport.setDateResolved(entity.getDateResolved());
                bugReport.setBugMessage(entity.getBugMessage());
                bugReport.setRecreationMessage(entity.getRecreationMessage());
                bugReports.add(bugReport);
            });
        }
        this.bugReports = bugReports;
        this.entities = list;
        this.gameProfilesEntity = gameProfilesEntity;
    }

    public void sync(Session session) {
        for (BugReport bugReport : removed)
            this.entities.forEach(entity -> {
                if (entity.getId() == bugReport.getId()) {
                    this.entities.remove(entity);
                    this.gameProfilesEntity.getBugReports().remove(entity);
                    entity.setGameProfilesEntity(null);
                    session.delete(entity);
                }
            });
        for (BugReport bugReport : bugReports) {
            boolean registered = false;
            for (BugReportsEntity entity : this.entities) {
                if (entity.getId() == bugReport.getId()) {
                    entity.setType(bugReport.getType().getId());
                    entity.setStatus(bugReport.getStatus().getId());
                    entity.setPriority(bugReport.getPriority().getId());
                    entity.setSubmissionDate(bugReport.getSubmissionDate());
                    entity.setDateResolved(bugReport.getDateResolved());
                    entity.setBugMessage(bugReport.getBugMessage());
                    entity.setRecreationMessage(bugReport.getRecreationMessage());
                    session.merge(entity);
                    registered = true;
                }
            }
            if (!registered) {
                BugReportsEntity entity = new BugReportsEntity();
                entity.setType(bugReport.getType().getId());
                entity.setStatus(bugReport.getStatus().getId());
                entity.setPriority(bugReport.getPriority().getId());
                entity.setSubmissionDate(bugReport.getSubmissionDate());
                entity.setDateResolved(bugReport.getDateResolved());
                entity.setBugMessage(bugReport.getBugMessage());
                entity.setRecreationMessage(bugReport.getRecreationMessage());
                entity.setGameProfilesEntity(this.gameProfilesEntity);
                this.gameProfilesEntity.getBugReports().add(entity);
                session.merge(entity);
            }
        }
    }

    public void delete(BugReport bugReport) {
        if (bugReports.contains(bugReport)) {
            this.bugReports.remove(bugReport);
            this.removed.add(bugReport);
        }
    }

}
