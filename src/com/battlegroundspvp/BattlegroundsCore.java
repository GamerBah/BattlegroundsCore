package com.battlegroundspvp;
/* Created by GamerBah on 6/1/2017 */

import com.battlegroundspvp.administration.commands.*;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.sql.*;
import com.battlegroundspvp.administration.donations.Essence;
import com.battlegroundspvp.commands.*;
import com.battlegroundspvp.listeners.BlockListeners;
import com.battlegroundspvp.listeners.InventoryClickListener;
import com.battlegroundspvp.listeners.ServerListPingListener;
import com.battlegroundspvp.listeners.WeatherChangeListener;
import com.battlegroundspvp.playerevents.*;
import com.battlegroundspvp.punishments.commands.*;
import com.battlegroundspvp.runnables.*;
import com.battlegroundspvp.utils.Crate;
import com.battlegroundspvp.utils.Launcher;
import com.battlegroundspvp.utils.cosmetics.CosmeticManager;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.enums.Rarity;
import com.battlegroundspvp.utils.enums.Time;
import com.battlegroundspvp.utils.inventories.InventoryBuilder;
import com.battlegroundspvp.utils.messages.ColorBuilder;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.Herbystar.TTA.TTA_Methods;
import de.slikey.effectlib.EffectManager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

public class BattlegroundsCore extends JavaPlugin {

    @Getter
    private static BattlegroundsCore instance = null;
    @Getter
    private static BattleModuleLoader moduleLoader = null;

    @Getter
    private static SessionFactory sessionFactory = null;
    @Getter
    private static ProtocolManager protocolManager = null;
    @Getter
    private static EffectManager effectManager = null;

    @Getter
    private static List<GameProfile> gameProfiles = new ArrayList<>();
    @Getter
    private static List<Thread> activeThreads = new ArrayList<>();
    @Getter
    private static List<Crate> crates = new ArrayList<>();
    @Getter
    private static List<Launcher> launchers = new ArrayList<>();

    @Getter
    private GlobalStatsEntity globalStats = null;

    @Getter
    private static Map<Player, Player> pendingFriends = new HashMap<>();
    @Getter
    private static Map<UUID, UUID> messagers = new HashMap<>();
    @Getter
    private static Map<UUID, Runnable> offlineMessages = new HashMap<>();
    @Getter
    private static HashSet<UUID> afk = new HashSet<>();
    @Getter
    private static HashSet<UUID> cmdspies = new HashSet<>();
    @Getter
    private static HashSet<Player> fallDmg = new HashSet<>();
    @Getter
    private List<String> filteredWords = new ArrayList<>();
    @Getter
    private List<String> safeWords = new ArrayList<>();
    @Getter
    private List<String> autoMessages = new ArrayList<>();
    @Getter
    private static HashMap<Location, Player> usingCrates = new HashMap<>();
    @Getter
    private static HashMap<Player, Rarity> crateOpening = new HashMap<>();
    @Getter
    private static ArrayList<Entity> entities = new ArrayList<>();

    public void onEnable() {
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        effectManager = new EffectManager(this);

        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
            this.getServer().shutdown();
        }

        reloadGameProfiles();
        getServer().getOnlinePlayers().forEach(p -> {
            respawn(p);
            TTA_Methods.sendTitle(p, null, 0, 0, 0, null, 0, 0, 0);
        });


        Session session = sessionFactory.openSession();
        session.beginTransaction();
        this.globalStats = session.createQuery("from GlobalStatsEntity where id = 1", GlobalStatsEntity.class).getSingleResult();
        session.getTransaction().commit();
        session.close();

        moduleLoader = new BattleModuleLoader();
        moduleLoader.enableModules();

        setUpPacketHandlers();
        registerCommands();
        registerListeners();
        loadConfigs();
        startTaskTimers();

        if (getConfig().getBoolean("essence.active")) {
            Essence.Type type = Essence.fromId(getConfig().getInt("essence.id"));
            long milliseconds = getConfig().getInt("essence.timeRemaining") * 1000;
            DonationUpdater.essenceBar = Bukkit.createBossBar(type.getChatColor() + Time.toString(milliseconds, true)
                    + ChatColor.GRAY + " remaining in " + type.getChatColor() + getConfig().getString("essenceOwner") + ChatColor.GRAY + "'s Battle Essence "
                    + type.getChatColor() + "(+" + type.getPercent() + "%)", type.getBarColor(), BarStyle.SOLID);
            getServer().getOnlinePlayers().forEach(p -> DonationUpdater.essenceBar.addPlayer(p));
        }
    }

    public void onDisable() {
        effectManager.dispose();

        activeThreads.forEach(Thread::interrupt);
        entities.forEach(Entity::remove);

        if (DonationUpdater.essenceBar != null) {
            DonationUpdater.essenceBar.removeAll();
            DonationUpdater.essenceBar.setVisible(false);
        }

        getConfig().set("launchers", "");
        launchers.forEach(launcher -> {
            getConfig().set("launchers." + launcher.getId() + ".type", launcher.getType().toString());
            getConfig().set("launchers." + launcher.getId() + ".strength", launcher.getStrength());
            getConfig().set("launchers." + launcher.getId() + ".location.world", launcher.getLocation().getWorld().getName());
            getConfig().set("launchers." + launcher.getId() + ".location.x", launcher.getLocation().getBlockX());
            getConfig().set("launchers." + launcher.getId() + ".location.y", launcher.getLocation().getBlockY());
            getConfig().set("launchers." + launcher.getId() + ".location.z", launcher.getLocation().getBlockZ());
            getConfig().set("launchers." + launcher.getId() + ".location.pitch", launcher.getLocation().getPitch());
            getConfig().set("launchers." + launcher.getId() + ".location.yaw", launcher.getLocation().getYaw());
        });

        getConfig().set("crates", "");
        crates.forEach(crate -> {
            crate.getHologram().getStands().forEach(ArmorStand::remove);
            getConfig().set("crates." + crate.getId() + ".world", crate.getLocation().getWorld().getName());
            getConfig().set("crates." + crate.getId() + ".x", crate.getLocation().getBlockX());
            getConfig().set("crates." + crate.getId() + ".y", crate.getLocation().getBlockY());
            getConfig().set("crates." + crate.getId() + ".z", crate.getLocation().getBlockZ());
        });
        saveConfig();
        sessionFactory.close();
    }

    private void setUp() {
        // sessionFactory SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private void setUpPacketHandlers() {
        if (getProtocolManager() != null)
            getProtocolManager().getAsynchronousManager().registerAsyncHandler(
                    new PacketAdapter(this, PacketType.Play.Client.UPDATE_SIGN) {
                        @Override
                        public void onPacketReceiving(PacketEvent event) {
                            if (event.getPacketType().equals(PacketType.Play.Client.UPDATE_SIGN)) {
                                if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                                    event.setCancelled(true);
                                    Player player = event.getPlayer();
                                    PacketContainer packetContainer = event.getPacket();
                                    String term = packetContainer.getStringArrays().read(0)[0];
                                    InventoryBuilder.getInventoryUsers().get(player).search(term).open();
                                    EventSound.playSound(player, EventSound.INVENTORY_OPEN_MENU);
                                    Location loc = new Location(player.getWorld(), 0, 0, 0);
                                    Sign sign = (Sign) loc.getBlock().getState();
                                    sign.setLine(1, "§f§l^ ^ ^");
                                    sign.setLine(2, "§bEnter your search");
                                    sign.setLine(3, "§bterm here!");
                                    sign.update();
                                }
                            }
                        }
                    }).syncStart();
    }

    private void registerCommands() {
        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("clearchat").setExecutor(new ChatCommands(this));
        getCommand("lockchat").setExecutor(new ChatCommands(this));
        getCommand("staff").setExecutor(new StaffChatCommand(this));
        getCommand("staffreq").setExecutor(new StaffReqCommand(this));
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("flyspeed").setExecutor(new FlySpeedCommand(this));
        getCommand("donation").setExecutor(new DonationCommand(this));
        getCommand("punish").setExecutor(new PunishCommand(this));
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));
        getCommand("maintenance").setExecutor(new MaintenanceCommand(this));
        getCommand("skull").setExecutor(new SkullCommand(this));
        getCommand("reload").setExecutor(new ReloadCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("tempban").setExecutor(new TempBanCommand(this));
        getCommand("warn").setExecutor(new WarnCommand(this));
        getCommand("launcher").setExecutor(new LauncherCommand(this));
        getCommand("ping").setExecutor(new PingCommand(this));
        getCommand("friend").setExecutor(new FriendCommand(this));
        getCommand("help").setExecutor(new HelpCommand(this));
        getCommand("message").setExecutor(new MessageCommand(this));
        getCommand("reply").setExecutor(new ReplyCommand(this));
        getCommand("rules").setExecutor(new RulesCommand(this));
        getCommand("crate").setExecutor(new CrateCommand());
        getCommand("setlocation").setExecutor(new SetLocationCommand(this));
        getCommand("afk").setExecutor(new AFKCommand(this));

        for (BattleModule module : BattleModuleLoader.modules.keySet()) {
            HashMap<String, CommandExecutor> commands = module.getCommands();
            for (String name : commands.keySet()) {
                getCommandMap().register(getConfig().getName(), getReflectCommand(name.replaceAll("\\s+", ""), this));
                this.getCommand(name.replaceAll("\\s+", "")).setExecutor(commands.get(name));
            }
        }
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoin(this), this);
        pluginManager.registerEvents(new PlayerQuit(this), this);
        pluginManager.registerEvents(new PlayerChat(this), this);
        pluginManager.registerEvents(new PlayerCommandPreProcess(this), this);
        pluginManager.registerEvents(new PlayerRespawn(), this);
        pluginManager.registerEvents(new PlayerCloseInventory(), this);
        pluginManager.registerEvents(new PlayerDamage(), this);
        pluginManager.registerEvents(new PlayerInteract(), this);
        pluginManager.registerEvents(new PlayerInteractEntity(), this);

        pluginManager.registerEvents(new ServerListPingListener(this), this);
        pluginManager.registerEvents(new InventoryClickListener(), this);
        pluginManager.registerEvents(new WeatherChangeListener(), this);
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new CosmeticManager(this), this);
    }

    @SuppressWarnings("unchecked")
    private void loadConfigs() {
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveDefaultConfig();

        if (getConfig().getConfigurationSection("launchers") != null)
            getConfig().getConfigurationSection("launchers").getKeys(false).forEach(id -> {
                Location location = new Location(getServer().getWorld(getConfig().getString("launchers." + id + ".location.world")),
                        getConfig().getInt("launchers." + id + ".location.x"),
                        getConfig().getInt("launchers." + id + ".location.y"),
                        getConfig().getInt("launchers." + id + ".location.z"));
                location.setPitch(getConfig().getInt("launchers." + id + ".location.pitch"));
                location.setYaw(getConfig().getInt("launchers." + id + ".location.yaw"));
                launchers.add(new Launcher(Integer.parseInt(id), location, Launcher.Type.valueOf(getConfig().getString("launchers." + id + ".type")),
                        getConfig().getInt("launchers." + id + ".strength")));
            });

        if (getConfig().getConfigurationSection("crates") != null)
            getConfig().getConfigurationSection("crates").getKeys(false).forEach(id ->
                    crates.add(new Crate(Integer.parseInt(id),
                            new Location(getServer().getWorld(getConfig().getString("crates." + id + ".world")),
                                    getConfig().getInt("crates." + id + ".x"),
                                    getConfig().getInt("crates." + id + ".y"),
                                    getConfig().getInt("crates." + id + ".z")))));

        File filterFile = new File(getDataFolder(), "filter.txt");
        if (!filterFile.exists()) {
            saveResource("filter.txt", false);
        }
        try {
            Files.lines(FileSystems.getDefault().getPath(filterFile.getPath())).forEach(filterLine ->
                    filteredWords.add(ChatColor.translateAlternateColorCodes('&', filterLine)));
        } catch (IOException e) {
            getLogger().severe("Could not get filtered words!");
        }

        // Save SafeWords File
        File safeWordsFile = new File(getDataFolder(), "safewords.txt");
        if (!filterFile.exists()) {
            saveResource("safewords.txt", false);
        }
        try {
            Files.lines(FileSystems.getDefault().getPath(safeWordsFile.getPath())).forEach(wordLine ->
                    safeWords.add(ChatColor.translateAlternateColorCodes('&', wordLine)));
        } catch (IOException e) {
            getLogger().severe("Could not get safe words!");
        }
    }

    private void startTaskTimers() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new UpdateRunnable(this), 120, 120);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new DonationUpdater(this), 0, 20);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TrailRunnable(this), 0, 2);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new AFKRunnable(this), 0, 20);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new PunishmentRunnable(this), 0, 20L);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new WorldParticlesRunnable(this), 0, 2L);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new MessageRunnable(this), 0, 6000L);
    }

    public GameProfile getGameProfile(UUID uuid) {
        Optional<GameProfile> gameProfileStream = gameProfiles.stream().filter(gameProfile ->
                gameProfile.getUuid().equals(uuid)).findFirst();

        if (gameProfileStream.isPresent()) {
            return gameProfileStream.get();
        } else {
            GameProfilesEntity dbGameProfile = null;
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            if (!session.createQuery("from GameProfilesEntity where uuid = :uuid", GameProfilesEntity.class)
                    .setParameter("uuid", uuid).getResultList().isEmpty())
                dbGameProfile = session.createQuery("from GameProfilesEntity where uuid = :uuid", GameProfilesEntity.class)
                        .setParameter("uuid", uuid).getSingleResult();
            session.getTransaction().commit();
            session.close();
            if (dbGameProfile != null) {
                gameProfiles.add(new GameProfile(dbGameProfile));
                return getGameProfile(uuid);
            } else {

                return null;
            }
        }
    }

    public GameProfile getGameProfile(String name) {
        Optional<GameProfile> playerDataStream = gameProfiles.stream().filter(gameProfile ->
                gameProfile.getName().equalsIgnoreCase(name)).findFirst();

        if (playerDataStream.isPresent()) {
            return playerDataStream.get();
        } else {
            GameProfilesEntity dbGameProfile = null;
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            if (!session.createQuery("from GameProfilesEntity where name = :name", GameProfilesEntity.class)
                    .setParameter("name", name).getResultList().isEmpty())
                dbGameProfile = session.createQuery("from GameProfilesEntity where name = :name", GameProfilesEntity.class)
                        .setParameter("name", name).getSingleResult();
            session.getTransaction().commit();
            session.close();
            if (dbGameProfile != null) {
                gameProfiles.add(new GameProfile(dbGameProfile));
                return getGameProfile(name);
            } else {
                return null;
            }
        }
    }

    public GameProfile getGameProfile(int id) {
        Optional<GameProfile> playerDataStream = gameProfiles.stream().filter(gameProfile ->
                gameProfile.getId() == id).findFirst();

        if (playerDataStream.isPresent()) {
            return playerDataStream.get();
        } else {
            GameProfilesEntity dbGameProfile = null;
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            if (!session.createQuery("from GameProfilesEntity where id = :id", GameProfilesEntity.class)
                    .setParameter("id", id).getResultList().isEmpty())
                dbGameProfile = session.createQuery("from GameProfilesEntity where id = :id", GameProfilesEntity.class)
                        .setParameter("id", id).getSingleResult();
            session.getTransaction().commit();
            session.close();
            if (dbGameProfile != null) {
                gameProfiles.add(new GameProfile(dbGameProfile));
                return getGameProfile(id);
            } else {
                return null;
            }
        }
    }

    private void reloadGameProfiles() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        if (!session.createQuery("from GameProfilesEntity", GameProfilesEntity.class).getResultList().isEmpty())
            for (GameProfilesEntity entity : session.createQuery("from GameProfilesEntity", GameProfilesEntity.class).getResultList())
                gameProfiles.add(new GameProfile(entity));
        session.getTransaction().commit();
        session.close();
    }

    public static void syncGameProfiles() {
        for (GameProfile gameProfile : gameProfiles)
            gameProfile.sync();
    }

    public static void createNewGameProfile(String name, UUID uuid) {
        Session session = BattlegroundsCore.getSessionFactory().openSession();

        GameProfilesEntity gameProfilesEntity = new GameProfilesEntity();
        gameProfilesEntity.setName(name);
        gameProfilesEntity.setUuid(uuid.toString());
        gameProfilesEntity.setDailyRewardLast(LocalDateTime.now());

        SettingsEntity settingsEntity = new SettingsEntity();
        KitPvpDataEntity kitPvpDataEntity = new KitPvpDataEntity();
        EssencesEntity essencesEntity = new EssencesEntity();
        CratesEntity cratesEntity = new CratesEntity();

        gameProfilesEntity.setSettings(settingsEntity);
        gameProfilesEntity.setKitPvpData(kitPvpDataEntity);
        gameProfilesEntity.setEssences(essencesEntity);
        gameProfilesEntity.setCrates(cratesEntity);
        settingsEntity.setGameProfile(gameProfilesEntity);
        kitPvpDataEntity.setGameProfile(gameProfilesEntity);
        essencesEntity.setGameProfile(gameProfilesEntity);
        cratesEntity.setGameProfile(gameProfilesEntity);

        session.beginTransaction();
        session.saveOrUpdate(gameProfilesEntity);
        session.getTransaction().commit();
        session.close();

        gameProfiles.add(new GameProfile(gameProfilesEntity));
        BattlegroundsCore.getInstance().getServer().getLogger().info("New GameProfile created for user " + name);
    }

    public void sendNoPermission(Player player) {
        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Sorry! " + ChatColor.GRAY + "You aren't allowed to use this command!");
        EventSound.playSound(player, EventSound.ACTION_FAIL);
    }

    public void sendIncorrectUsage(Player player, String msg) {
        player.sendMessage(new ColorBuilder(ChatColor.RED).bold().create() + "Oops! " + ChatColor.GRAY + "Try this: " + ChatColor.RED + msg);
        EventSound.playSound(player, EventSound.ACTION_FAIL);
    }

    public void sendNoResults(Player player, String msg) {
        player.sendMessage(new ColorBuilder(ChatColor.RED).bold().create() + "Sorry! " + ChatColor.GRAY + "I couldn't " + msg);
        EventSound.playSound(player, EventSound.ACTION_FAIL);
    }

    public void respawn(Player player, Location location) {
        player.spigot().respawn();
        player.teleport(location);
        getServer().getPluginManager().callEvent(new PlayerRespawnEvent(player, location, false));
    }

    public void respawn(Player player) {
        respawn(player, (Location) BattlegroundsCore.getInstance().getConfig().get("locations.spawn"));
    }

    public static String getLore(ItemStack itemStack, int line) {
        return itemStack.getItemMeta().getLore().get(line - 1);
    }

    public static void clearTitle(Player player) {
        TTA_Methods.sendTitle(player, null, 0, 0, 0, null, 0, 0, 0);
    }

    public static PluginCommand getReflectCommand(String name, Plugin plugin) {
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);

            command = c.newInstance(name, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return command;
    }

    public static CommandMap getCommandMap() {
        CommandMap commandMap = null;

        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);

                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return commandMap;
    }

}
