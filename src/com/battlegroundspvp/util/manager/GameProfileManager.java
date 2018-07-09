package com.battlegroundspvp.util.manager;
/* Created by GamerBah on 7/9/2018 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.sql.GameProfilesEntity;
import com.battlegroundspvp.administration.data.sql.ServerDataEntity;
import lombok.Getter;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class GameProfileManager {

    private static BattlegroundsCore plugin;

    @Getter
    private static List<GameProfile> gameProfiles = new ArrayList<>();

    public GameProfileManager(final BattlegroundsCore pl) {
        plugin = pl;
    }

    public static boolean addGameProfile(final GameProfile gameProfile) {
        return getGameProfiles().add(gameProfile);
    }

    public static GameProfile getGameProfile(final int id) {
        Optional<GameProfile> gameProfileStream = gameProfiles.stream().filter(gameProfile ->
                gameProfile.getId() == id).findFirst();

        if (gameProfileStream.isPresent()) {
            return gameProfileStream.get();
        } else {
            Future<GameProfile> future = SessionManager.getService().submit(() -> {
                GameProfilesEntity gameProfileEntity = null;
                Session session = SessionManager.openSession();
                session.beginTransaction();
                if (!session.createQuery("from GameProfilesEntity where id = :id", GameProfilesEntity.class)
                        .setParameter("id", id).getResultList().isEmpty())
                    gameProfileEntity = session.createQuery("from GameProfilesEntity where id = :id", GameProfilesEntity.class)
                            .setParameter("id", id).getSingleResult();
                session.getTransaction().commit();
                SessionManager.closeSession(session);
                if (gameProfileEntity != null) {
                    gameProfiles.add(new GameProfile(gameProfileEntity));
                    return getGameProfile(id);
                } else {
                    return null;
                }
            });
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static GameProfile getGameProfile(final String name) {
        Optional<GameProfile> gameProfileStream = gameProfiles.stream().filter(gameProfile ->
                gameProfile.getName().equalsIgnoreCase(name)).findFirst();

        if (gameProfileStream.isPresent()) {
            return gameProfileStream.get();
        } else {
            Future<GameProfile> future = SessionManager.getService().submit(() -> {
                GameProfilesEntity gameProfileEntity = null;
                Session session = SessionManager.openSession();
                session.beginTransaction();
                if (!session.createQuery("from GameProfilesEntity where name = :name", GameProfilesEntity.class)
                        .setParameter("name", name).getResultList().isEmpty())
                    gameProfileEntity = session.createQuery("from GameProfilesEntity where name = :name", GameProfilesEntity.class)
                            .setParameter("name", name).getSingleResult();
                session.getTransaction().commit();
                SessionManager.closeSession(session);
                if (gameProfileEntity != null) {
                    gameProfiles.add(new GameProfile(gameProfileEntity));
                    return getGameProfile(name);
                } else {
                    return null;
                }
            });
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static GameProfile getGameProfile(final UUID uuid) {
        Optional<GameProfile> gameProfileStream = gameProfiles.stream().filter(gameProfile -> gameProfile != null &&
                gameProfile.getUuid().equals(uuid)).findFirst();

        if (gameProfileStream.isPresent()) {
            return gameProfileStream.get();
        } else {
            Future<GameProfile> future = SessionManager.getService().submit(() -> {
                GameProfilesEntity gameProfileEntity = null;
                Session session = SessionManager.openSession();
                session.beginTransaction();
                if (!session.createQuery("from GameProfilesEntity where uuid = :uuid", GameProfilesEntity.class)
                        .setParameter("uuid", uuid.toString()).getResultList().isEmpty())
                    gameProfileEntity = session.createQuery("from GameProfilesEntity where uuid = :uuid", GameProfilesEntity.class)
                            .setParameter("uuid", uuid.toString()).getSingleResult();
                session.getTransaction().commit();
                SessionManager.closeSession(session);
                if (gameProfileEntity != null) {
                    gameProfiles.add(new GameProfile(gameProfileEntity));
                    return getGameProfile(uuid);
                } else {
                    return null;
                }
            });
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void sync() {
        gameProfiles.forEach(GameProfile::fullSync);
        Session session = SessionManager.openSession();
        Transaction transaction;
        try {
            transaction = session.beginTransaction();
            ServerDataEntity serverData = session.get(ServerDataEntity.class, 1);
            serverData.setSnapshotDay(BattlegroundsCore.getDaySnapshot());
            serverData.setSnapshotMonth(BattlegroundsCore.getMonthSnapshot());
            serverData.setSnapshotYear(BattlegroundsCore.getYearSnapshot());
            session.update(serverData);
            transaction.commit();
        } catch (HibernateException e) {
            plugin.getLogger().warning(e.getLocalizedMessage());
        }
    }

}
