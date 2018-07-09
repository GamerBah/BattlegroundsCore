package com.battlegroundspvp.runnable.misc;

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.manager.UpdateManager;
import net.md_5.bungee.api.ChatColor;

import java.io.File;

public class UpdateRunnable implements Runnable {

    @Override
    public void run() {
        if (UpdateManager.getUpdateFile() == null || !UpdateManager.getUpdateFile().exists()) {
            for (BattleModule module : BattleModuleLoader.modules.keySet()) {
                if (moduleHasUpdate(module)) {
                    if (!module.isAwaitingUpdate()) {
                        module.setAwaitingUpdate(true);
                        GameProfileManager.getGameProfiles().stream().filter(gameProfile -> gameProfile.hasRank(Rank.ADMIN)).forEach(gameProfile -> {
                            gameProfile.sendMessage(" ");
                            gameProfile.sendMessage(ChatColor.GREEN + "Module " + ChatColor.GOLD + module.getName() + ChatColor.GREEN + " is ready to update!");
                            gameProfile.sendMessage(ChatColor.GRAY + "Use " + ChatColor.RED + "/reload" + ChatColor.GRAY + " to start the update");
                            gameProfile.sendMessage(" ");
                            gameProfile.playSound(EventSound.ACTION_SUCCESS);
                        });
                    }
                }
            }
            File updateFile = new File(BattlegroundsCore.getInstance().getServer().getUpdateFolderFile().getPath() + File.separator + "BattlegroundsCore.jar");
            if (updateFile.exists() && !UpdateManager.isAwaitingUpdate() && !UpdateManager.isUpdating()) {
                UpdateManager.setAwaitingUpdate(true);
                GameProfileManager.getGameProfiles().stream().filter(gameProfile -> gameProfile.hasRank(Rank.ADMIN) && gameProfile.isOnline()).forEach(gameProfile -> {
                    gameProfile.sendMessage(" ");
                    gameProfile.sendMessage(ChatColor.GREEN + "Core plugin is ready to update!");
                    gameProfile.sendMessage(ChatColor.GRAY + "Use " + ChatColor.RED + "/reload" + ChatColor.GRAY + " to start the update");
                    gameProfile.sendMessage(" ");
                    gameProfile.playSound(EventSound.ACTION_SUCCESS);
                });
            }
        }
    }

    private boolean moduleHasUpdate(BattleModule module) {
        String jarName = "Module-" + module.getName() + ".jar";
        File update = new File(BattlegroundsCore.getInstance().getServer().getUpdateFolderFile().getPath() + File.separator + jarName);
        return update.exists();
    }
}
