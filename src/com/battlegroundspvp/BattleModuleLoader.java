package com.battlegroundspvp;
/* Created by GamerBah on 10/16/2017 */

import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;

public class BattleModuleLoader {

    private BattlegroundsCore plugin = BattlegroundsCore.getInstance();

    public static HashMap<BattleModule, Plugin> modules = new HashMap<>();

    BattleModuleLoader() {
        File moduleFolder = new File(plugin.getDataFolder().getPath() + "/modules");
        if (!moduleFolder.isDirectory()) {
            plugin.getPluginLoader().disablePlugin(plugin);
            plugin.getLogger().severe("Module folder does not exist! Disabling...");
        }
        if (moduleFolder.listFiles() == null || moduleFolder.listFiles().length == 0) {
            plugin.getPluginLoader().disablePlugin(plugin);
            plugin.getLogger().severe("Module folder does not contain module jars! Disabling...");
        }

        if (moduleFolder.listFiles() != null) {
            for (File file : moduleFolder.listFiles()) {
                if (file.getName().startsWith("Module-")) {
                    try {
                        Plugin modulePlugin = this.plugin.getPluginLoader().loadPlugin(file);
                        Class<?> jarClass = null;
                        BattleModule battleModule = null;
                        try {
                            jarClass = Class.forName(modulePlugin.getDescription().getMain());
                        } catch (ClassNotFoundException e) {
                            plugin.getLogger().severe("Couldn't find main class!");
                        }
                        if (jarClass != null) {
                            Class<? extends BattleModule> moduleClass;
                            try {
                                moduleClass = Class.forName(jarClass.getPackage().getName() + ".ModulePlugin").asSubclass(BattleModule.class);
                            } catch (ClassCastException | ClassNotFoundException e) {
                                throw new InvalidPluginException("Module class `" + jarClass.getPackage().getName() + ".ModulePlugin` does not extend BattleModule", e);
                            }

                            try {
                                battleModule = moduleClass.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }

                            if (battleModule == null) {
                                plugin.getLogger().severe("Module cannot be null!");
                                return;
                            }
                        } else {
                            plugin.getLogger().severe("Main class is null!");
                            return;
                        }
                        modules.put(battleModule, modulePlugin);
                    } catch (InvalidPluginException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    void enableModules() {
        for (BattleModule module : modules.keySet()) {
            if (module == null) {
                this.plugin.getLogger().severe("Module cannot be null!");
                return;
            }
            this.plugin.getPluginLoader().enablePlugin(modules.get(module));
        }
    }

}
