package com.battlegroundspvp.util;
/* Created by GamerBah on 7/1/2018 */

import com.battlegroundspvp.BattlegroundsCore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class SessionManager {

    private static BattlegroundsCore plugin;

    @Getter
    private static ExecutorService service;
    @Getter
    private static SessionFactory factory;
    @Getter
    @Setter
    private static boolean started;

    @Getter
    private static ArrayList<Session> sessions = new ArrayList<>();

    public SessionManager(BattlegroundsCore pl) {
        plugin = pl;
        service = start();
    }

    public static Session openSession() {
        Session session = factory.openSession();
        sessions.add(session);
        return session;
    }

    public static void closeSession(final Session session) {
        sessions.remove(session);
        session.close();
    }

    public static void shutdown() {
        closeFactory();
        if (!service.isTerminated()) {
            try {
                service.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                service.shutdown();
            } finally {
                if (!service.isShutdown()) {
                    service.shutdownNow();
                    plugin.getLogger().log(Level.WARNING, "Forced shutdown of executor service was required");
                }
            }
        }
        setStarted(false);
    }

    public static ExecutorService start() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            UpdateManager.sendLocalizedLog(Level.SEVERE, e, "Unable to start SessionFactory!", "Entering Maintenance Mode...");
            UpdateManager.enterMaintenance();
            return null;
        }
        setStarted(true);
        return Executors.newCachedThreadPool();
    }

    private static void closeFactory() {
        if (!factory.isClosed()) {
            sessions.forEach(Session::close);
            factory.close();
        }
    }

}
