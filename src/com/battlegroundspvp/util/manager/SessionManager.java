package com.battlegroundspvp.util.manager;
/* Created by GamerBah on 7/1/2018 */

import com.battlegroundspvp.BattlegroundsCore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        if (!factory.isClosed()) {
            sessions.forEach(Session::close);
            factory.close();
        }
        setStarted(false);
    }

    public static ExecutorService start() {
        factory = BattlegroundsCore.setupSessionFactory();
        setStarted(true);
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(1, 100, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
        executorService.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executorService;
    }

}
