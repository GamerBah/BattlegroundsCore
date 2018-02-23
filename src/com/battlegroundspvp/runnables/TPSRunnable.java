package com.battlegroundspvp.runnables;
/* Created by GamerBah on 2/12/2018 */

import lombok.Getter;

import java.util.Arrays;

public class TPSRunnable implements Runnable {

    @Getter
    private static double tps = 0.00;
    @Getter
    private static double[] timings = new double[]{20, 20, 20, 20, 20, 20, 20, 20, 20, 20};

    private long currentSec;
    private int ticks;
    private int delay;

    @Override
    public void run() {
        long sec = (System.currentTimeMillis() / 1000);
        if (currentSec == sec) {
            ticks++;
        } else {
            currentSec = sec;
            tps = (tps == 0 ? ticks : ((tps + ticks) / 2) + 0.5);
            ticks = 0;
            if ((++delay % 10) == 0) {
                timings = Arrays.copyOfRange(timings, 1, 9);
                timings[timings.length - 1] = tps;
                delay = 0;
            }
        }
    }
}
