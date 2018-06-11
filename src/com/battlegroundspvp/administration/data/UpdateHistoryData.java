package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 6/11/2018 */

import com.battlegroundspvp.administration.data.sql.UpdateHistoryEntity;
import com.battlegroundspvp.administration.data.util.Update;
import lombok.Getter;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class UpdateHistoryData {

    @Getter
    private ArrayList<Update> updates;
    @Getter
    private Set<UpdateHistoryEntity> entities;

    UpdateHistoryData(final Set<UpdateHistoryEntity> entities) {
        this.updates = new ArrayList<>();
        this.entities = entities;
        if (entities != null) {
            for (UpdateHistoryEntity entity : entities) {
                updates.add(new Update(entity.getId(), entity.getDate(), Update.Result.values()[entity.getResult()],
                        new ArrayList<>(Arrays.asList(entity.getNotes().replaceAll("[\\[\\] ]", "").split(",")))));
            }
        }
    }

    public void sync(Session session) {
        for (Update update : updates) {
            boolean registered = false;
            for (UpdateHistoryEntity entity : this.entities) {
                if (entity.getId() == update.getId()) {
                    entity.setDate(update.getDate());
                    entity.setResult(update.getResult().ordinal());
                    entity.setNotes(update.getNotes().toString());
                    session.merge(entity);
                    registered = true;
                }
            }
            if (!registered) {
                UpdateHistoryEntity entity = new UpdateHistoryEntity();
                entity.setDate(update.getDate());
                entity.setResult(update.getResult().ordinal());
                entity.setNotes(update.getNotes().toString());
                session.merge(entity);
            }
        }
    }

}
