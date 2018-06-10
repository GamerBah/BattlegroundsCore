package com.battlegroundspvp.runnable.misc;
/* Created by GamerBah on 2/23/2018 */

import com.battlegroundspvp.BattlegroundsCore;

import java.time.LocalDate;

public class TimeTracker implements Runnable {

    @Override
    public void run() {
        if (BattlegroundsCore.getDaySnapshot() != null)
            if (BattlegroundsCore.getDaySnapshot().getDayOfYear() < LocalDate.now().getDayOfYear())
                BattlegroundsCore.setDaySnapshot(LocalDate.now());
            else BattlegroundsCore.setDaySnapshot(LocalDate.now());
        if (BattlegroundsCore.getMonthSnapshot() != null)
            if (BattlegroundsCore.getMonthSnapshot().getMonthValue() < LocalDate.now().getMonthValue())
                BattlegroundsCore.setMonthSnapshot(LocalDate.now());
            else BattlegroundsCore.setMonthSnapshot(LocalDate.now());
        if (BattlegroundsCore.getYearSnapshot() != null)
            if (BattlegroundsCore.getYearSnapshot().getYear() < LocalDate.now().getYear())
                BattlegroundsCore.setYearSnapshot(LocalDate.now());
            else BattlegroundsCore.setYearSnapshot(LocalDate.now());
        BattlegroundsCore.getGameProfiles().forEach(gameProfile -> {
            gameProfile.getStatistics().getDailyHours().put(BattlegroundsCore.getDaySnapshot(),
                    gameProfile.getStatistics().getDailyHours().getOrDefault(BattlegroundsCore.getDaySnapshot(), 0L) + 1L);
            gameProfile.getStatistics().getMonthlyHours().put(BattlegroundsCore.getMonthSnapshot(),
                    gameProfile.getStatistics().getMonthlyHours().getOrDefault(BattlegroundsCore.getMonthSnapshot(), 0L) + 1L);
            gameProfile.getStatistics().getYearlyHours().put(BattlegroundsCore.getYearSnapshot(),
                    gameProfile.getStatistics().getYearlyHours().getOrDefault(BattlegroundsCore.getYearSnapshot(), 0L) + 1L);
            gameProfile.getStatistics().setAlltimeHours(gameProfile.getStatistics().getAlltimeHours() + 1L);
        });
    }

}
