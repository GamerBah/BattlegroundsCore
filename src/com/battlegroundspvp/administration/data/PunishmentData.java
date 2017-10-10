package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 9/30/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.sql.GameProfilesEntity;
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
    @Getter
    private List<PunishmentsEntity> entities;
    @Getter
    private GameProfilesEntity gameProfilesEntity;

    public PunishmentData(List<PunishmentsEntity> list, GameProfilesEntity gameProfilesEntity) {
        this.id = gameProfilesEntity.getId();
        ArrayList<Punishment> punishments = new ArrayList<>();
        if (list != null) {
            for (PunishmentsEntity entity : list) {
                punishments.add(new Punishment(entity.getPunishmentId(),
                        Punishment.Type.valueOf(entity.getType()),
                        entity.getDate(),
                        entity.getDuration(),
                        entity.getExpiration(),
                        entity.getEnforcerId(),
                        Punishment.Reason.valueOf(entity.getReason()),
                        entity.isPardoned()));
            }
        }
        this.punishments = punishments;
        this.entities = list;
        this.gameProfilesEntity = gameProfilesEntity;
    }

    public void sync() {
        Session session = BattlegroundsCore.getSessionFactory().openSession();
        session.beginTransaction();
        for (Punishment punishment : punishments) {
            boolean registered = false;
            for (PunishmentsEntity entity : this.entities) {
                if (entity.hashCode() == punishment.hashCode()) {
                    entity.setDate(punishment.getDate());
                    entity.setDuration(punishment.getDuration());
                    entity.setEnforcerId(punishment.getEnforcerId());
                    entity.setExpiration(punishment.getExpiration());
                    entity.setPardoned(punishment.isPardoned());
                    entity.setReason(punishment.getReason().toString());
                    entity.setType(punishment.getType().toString());
                    session.merge(entity);
                    registered = true;
                }
            }
            if (!registered) {
                PunishmentsEntity entity = new PunishmentsEntity();
                entity.setDate(punishment.getDate());
                entity.setDuration(punishment.getDuration());
                entity.setEnforcerId(punishment.getEnforcerId());
                entity.setExpiration(punishment.getExpiration());
                entity.setPardoned(punishment.isPardoned());
                entity.setReason(punishment.getReason().toString());
                entity.setType(punishment.getType().toString());
                entity.setGameProfilesEntity(this.gameProfilesEntity);
                this.gameProfilesEntity.getPunishments().add(entity);
                session.merge(entity);
            }
        }
        session.getTransaction().commit();
        session.close();
    }

    public ArrayList<Punishment> getMutes() {
        ArrayList<Punishment> mutes = new ArrayList<>();
        if (this.punishments != null)
            for (Punishment punishment : this.punishments)
                if (punishment.getType().equals(Punishment.Type.MUTE))
                    mutes.add(punishment);
        return mutes;
    }

    public ArrayList<Punishment> getKicks() {
        ArrayList<Punishment> kicks = new ArrayList<>();
        if (this.punishments != null)
            for (Punishment punishment : this.punishments)
                if (punishment.getType().equals(Punishment.Type.KICK))
                    kicks.add(punishment);
        return kicks;
    }

    public ArrayList<Punishment> getTempBans() {
        ArrayList<Punishment> tempBans = new ArrayList<>();
        if (this.punishments != null)
            for (Punishment punishment : this.punishments)
                if (punishment.getType().equals(Punishment.Type.TEMP_BAN))
                    tempBans.add(punishment);
        return tempBans;
    }

    public ArrayList<Punishment> getBans() {
        ArrayList<Punishment> bans = new ArrayList<>();
        if (this.punishments != null)
            for (Punishment punishment : this.punishments)
                if (punishment.getType().equals(Punishment.Type.BAN))
                    bans.add(punishment);
        return bans;
    }

}
