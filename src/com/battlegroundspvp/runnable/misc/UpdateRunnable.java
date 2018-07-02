package com.battlegroundspvp.runnable.misc;

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.util.UpdateManager;

import java.io.File;

public class UpdateRunnable implements Runnable {

    @Override
    public void run() {
        if (UpdateManager.getUpdateFile() == null || !UpdateManager.getUpdateFile().exists()) {
            for (BattleModule module : BattleModuleLoader.modules.keySet())
                if (moduleHasUpdate(module))
                    UpdateManager.update(UpdateManager.UpdateType.PLUGIN);
        }
    }

    private boolean moduleHasUpdate(BattleModule module) {
        String jarName = "Module-" + module.getName() + ".jar";
        File update = new File(BattlegroundsCore.getInstance().getServer().getUpdateFolderFile().getPath() + File.separator + jarName);
        return update.exists();
    }
}
