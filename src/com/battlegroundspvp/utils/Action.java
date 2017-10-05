package com.battlegroundspvp.utils;
/* Created by GamerBah on 9/1/2017 */

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class Action {

    private List<Callable> customCalls;

    public Action(Callable<?>... calls) {
        this.customCalls = Arrays.asList(calls);
    }

    public void execute() {
        for (Callable call : customCalls)
            try {
                call.call();
            } catch (Exception e) {
                Bukkit.getServer().getLogger().severe("Call failed!");
                e.printStackTrace();
            }
    }

}
