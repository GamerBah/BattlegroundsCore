package com.battlegroundspvp.runnable.misc;
/* Created by GamerBah on 2/7/2018 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.sql.ServerDataEntity;
import com.battlegroundspvp.util.manager.SessionManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.stream.DoubleStream;

public class PanelRunnables implements Runnable {

    @Override
    public void run() {
        while (true) {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
            int ramUsed = Math.round((memBean.getHeapMemoryUsage().getUsed() + memBean.getNonHeapMemoryUsage().getUsed()) / 1000000);
            double cpu = osBean.getSystemLoadAverage();
            double curTps = (double) Math.round(TPSRunnable.getTps() * 100) / 100;
            double avgTps = (double) Math.round(DoubleStream.of(TPSRunnable.getTimings()).sum() / DoubleStream.of(TPSRunnable.getTimings()).count() * 100) / 100;
            int online = BattlegroundsCore.getInstance().getServer().getOnlinePlayers().size();
            int status = 0;

            try {
                Session session = SessionManager.openSession();
                Transaction transaction;
                try {
                    transaction = session.beginTransaction();
                    ServerDataEntity serverData = session.get(ServerDataEntity.class, 1);
                    serverData.setRamUsed(ramUsed);
                    serverData.setCpuUsage(cpu);
                    serverData.setCurTps(curTps);
                    serverData.setAvgTps(avgTps);
                    serverData.setPlayersOnline(online);
                    serverData.setStatus(status);
                    session.update(serverData);
                    transaction.commit();
                } catch (HibernateException e) {
                    break;
                }
                if (Thread.currentThread().isInterrupted()) {
                    SessionManager.closeSession(session);
                    transaction = session.beginTransaction();
                    ServerDataEntity serverData = session.get(ServerDataEntity.class, 1);
                    serverData.setRamUsed(0);
                    serverData.setCpuUsage(0);
                    serverData.setCurTps(0);
                    serverData.setAvgTps(20);
                    session.update(serverData);
                    transaction.commit();
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                } finally {
                    SessionManager.closeSession(session);
                }
            } catch (IllegalStateException e) {
                break;
            }
        }
    }
}
