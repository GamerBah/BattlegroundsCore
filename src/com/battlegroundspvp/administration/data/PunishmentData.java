package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 9/30/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.sql.PunishmentsEntity;
import com.battlegroundspvp.punishments.Punishment;
import lombok.Getter;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class PunishmentData {

    @Getter
    private final int id;
    @Getter
    private ArrayList<Punishment> punishments;

    public PunishmentData(List<PunishmentsEntity> list) {
        this.id = list.get(0).getId();
        ArrayList<Punishment> punishments = new ArrayList<>(list.size());
        for (PunishmentsEntity entity : list) {
            punishments.add(new Punishment(entity.getId(),
                    Punishment.Type.valueOf(entity.getType()),
                    entity.getDate(),
                    entity.getDuration(),
                    entity.getExpiration(),
                    entity.getEnforcer(),
                    Punishment.Reason.valueOf(entity.getReason()),
                    entity.isPardoned()));
        }
        this.punishments = punishments;
    }

    public void sync() {
        PunishmentsEntity entity = null;
        Session session = BattlegroundsCore.getSessionFactory().openSession();
        session.beginTransaction();
        if (!session.createQuery("from PunishmentsEntity where id = :id", PunishmentsEntity.class)
                .setParameter("id", this.id).getResultList().isEmpty())
            entity = session.createQuery("from PunishmentsEntity where id = :id", PunishmentsEntity.class)
                    .setParameter("id", this.id).getSingleResult();
        if (entity != null) {
            // TODO: dunno how to do this yet
        }
        session.getTransaction().commit();
        session.close();
    }

}
