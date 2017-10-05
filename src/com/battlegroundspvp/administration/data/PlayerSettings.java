package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 1/3/2017 */

import com.battlegroundspvp.Core;
import com.battlegroundspvp.administration.data.sql.SettingsEntity;
import com.battlegroundspvp.utils.enums.ParticleQuality;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;

public class PlayerSettings {

    @Getter
    private final SettingsEntity entity;
    @Getter
    private final int id;
    @Getter
    @Setter
    private boolean teamRequests, privateMessaging, stealthyJoin;
    @Getter
    @Setter
    private ParticleQuality particleQuality;

    public PlayerSettings(SettingsEntity entity) {
        this.entity = entity;
        this.id = entity.getId();
        this.teamRequests = entity.isTeamRequests();
        this.privateMessaging = entity.isPrivateMessaging();
        this.stealthyJoin = entity.isStealthyJoin();
        this.particleQuality = ParticleQuality.fromString(entity.getParticleQuality());
    }

    public void sync() {
        SettingsEntity entity = null;
        Session session = Core.getSessionFactory().openSession();
        session.beginTransaction();
        if (!session.createQuery("from SettingsEntity where id = :id", SettingsEntity.class)
                .setParameter("id", this.id).getResultList().isEmpty())
            entity = session.createQuery("from SettingsEntity where id = :id", SettingsEntity.class)
                    .setParameter("id", this.id).getSingleResult();
        if (entity != null) {
            entity.setTeamRequests(this.teamRequests);
            entity.setPrivateMessaging(this.privateMessaging);
            entity.setStealthyJoin(this.stealthyJoin);
            entity.setParticleQuality(this.particleQuality.toString());
        }
        session.getTransaction().commit();
        session.close();
    }
}
