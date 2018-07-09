package com.battlegroundspvp;
/* Created by GamerBah on 6/1/2017 */

import com.battlegroundspvp.administration.command.*;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.sql.*;
import com.battlegroundspvp.administration.donation.Essence;
import com.battlegroundspvp.command.*;
import com.battlegroundspvp.event.*;
import com.battlegroundspvp.listener.BlockListeners;
import com.battlegroundspvp.listener.ServerListPingListener;
import com.battlegroundspvp.listener.WeatherChangeListener;
import com.battlegroundspvp.punishment.command.*;
import com.battlegroundspvp.runnable.game.MessageRunnable;
import com.battlegroundspvp.runnable.game.TrailRunnable;
import com.battlegroundspvp.runnable.game.WorldParticlesRunnable;
import com.battlegroundspvp.runnable.misc.PanelRunnables;
import com.battlegroundspvp.runnable.misc.TPSRunnable;
import com.battlegroundspvp.runnable.misc.TimeTracker;
import com.battlegroundspvp.runnable.misc.UpdateRunnable;
import com.battlegroundspvp.runnable.timer.AFKRunnable;
import com.battlegroundspvp.runnable.timer.DonationUpdater;
import com.battlegroundspvp.runnable.timer.PunishmentRunnable;
import com.battlegroundspvp.util.BattleCrate;
import com.battlegroundspvp.util.Launcher;
import com.battlegroundspvp.util.cosmetic.CosmeticManager;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Time;
import com.battlegroundspvp.util.manager.BattleCrateManager;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.manager.SessionManager;
import com.battlegroundspvp.util.manager.UpdateManager;
import com.battlegroundspvp.util.message.MessageBuilder;
import com.battlegroundspvp.util.nms.Hologram;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.Herbystar.TTA.TTA_Methods;
import de.slikey.effectlib.EffectManager;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class BattlegroundsCore extends JavaPlugin {

    @Getter
    private static BattlegroundsCore instance = null;

    @Getter
    private static ProtocolManager protocolManager = null;
    @Getter
    private static EffectManager effectManager = null;

    @Getter
    @Setter
    public static LocalDate daySnapshot;
    @Getter
    @Setter
    public static LocalDate monthSnapshot;
    @Getter
    @Setter
    public static LocalDate yearSnapshot;

    @Getter
    private static List<Thread> activeThreads = new ArrayList<>();
    @Getter
    private static List<Launcher> launchers = new ArrayList<>();

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
    private HashSet<Player> awaitingRegistration = new HashSet<>();
    @Getter
    private List<String> filteredWords = new ArrayList<>();
    @Getter
    private List<String> safeWords = new ArrayList<>();
    @Getter
    private List<String> autoMessages = new ArrayList<>();
    @Getter
    private static ArrayList<Entity> entities = new ArrayList<>();
    @Getter
    private static ArrayList<Hologram> holograms = new ArrayList<>();

    public void onEnable() {
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        effectManager = new EffectManager(this);

        new SessionManager(this);
        while (!SessionManager.isStarted()) ;

        new GameProfileManager(this);

        SessionManager.getService().execute(() -> {
            Session session = SessionManager.openSession();
            session.beginTransaction();
            if (!session.createQuery("from GameProfilesEntity", GameProfilesEntity.class).getResultList().isEmpty())
                for (GameProfilesEntity entity : session.createQuery("from GameProfilesEntity", GameProfilesEntity.class).getResultList())
                    if (entity.isOnline())
                        GameProfileManager.addGameProfile(new GameProfile(entity));
            session.getTransaction().commit();
            SessionManager.closeSession(session);
        });
        reloadSnapshots();
        getServer().getOnlinePlayers().forEach(p -> {
            respawn(p);
            TTA_Methods.sendTitle(p, null, 0, 0, 0, null, 0, 0, 0);
        });

        new BattleModuleLoader().enableModules();

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

        getConfig().set("battleCrates", "");
        BattleCrateManager.getCrates().forEach(crate -> {
            crate.getHologram().getStands().forEach(ArmorStand::remove);
            getConfig().set("battleCrates." + crate.getId() + ".world", crate.getLocation().getWorld().getName());
            getConfig().set("battleCrates." + crate.getId() + ".x", crate.getLocation().getBlockX());
            getConfig().set("battleCrates." + crate.getId() + ".y", crate.getLocation().getBlockY());
            getConfig().set("battleCrates." + crate.getId() + ".z", crate.getLocation().getBlockZ());
        });
        saveConfig();

        effectManager.dispose();
        activeThreads.forEach(Thread::interrupt);
        entities.forEach(Entity::remove);
        if (DonationUpdater.essenceBar != null) {
            DonationUpdater.essenceBar.removeAll();
            DonationUpdater.essenceBar.setVisible(false);
        }
    }

    public static SessionFactory setupSessionFactory() {
        SessionFactory factory = null;
        // sessionFactory SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }
        return factory;
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
                                    //InventoryBuilder.getInventoryUsers().get(player).search(term).open();
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

        if (getConfig().getConfigurationSection("battleCrates") != null)
            getConfig().getConfigurationSection("battleCrates").getKeys(false).forEach(id ->
                    BattleCrateManager.addCrate(new BattleCrate(Integer.parseInt(id),
                            new Location(getServer().getWorld(getConfig().getString("battleCrates." + id + ".world")),
                                    getConfig().getInt("battleCrates." + id + ".x"),
                                    getConfig().getInt("battleCrates." + id + ".y"),
                                    getConfig().getInt("battleCrates." + id + ".z")))));

        File filterFile = new File(getDataFolder(), "filter.txt");
        if (!filterFile.exists())
            saveResource("filter.txt", false);
        try {
            Files.lines(FileSystems.getDefault().getPath(filterFile.getPath())).forEach(filterLine ->
                    filteredWords.add(ChatColor.translateAlternateColorCodes('&', filterLine)));
        } catch (IOException e) {
            getLogger().severe("Could not get filtered words!");
        }

        // Save SafeWords File
        File safeWordsFile = new File(getDataFolder(), "safewords.txt");
        if (!filterFile.exists())
            saveResource("safewords.txt", false);
        try {
            Files.lines(FileSystems.getDefault().getPath(safeWordsFile.getPath())).forEach(wordLine ->
                    safeWords.add(ChatColor.translateAlternateColorCodes('&', wordLine)));
        } catch (IOException e) {
            getLogger().severe("Could not get safe words!");
        }
    }

    private void startTaskTimers() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new UpdateRunnable(), 120, 120);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new DonationUpdater(this), 0, 20);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TrailRunnable(this), 0, 2);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new AFKRunnable(this), 0, 20);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new PunishmentRunnable(this), 0, 20L);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new WorldParticlesRunnable(this), 0, 2L);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new MessageRunnable(this), 0, 6000L);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> GameProfileManager.getGameProfiles().forEach(GameProfile::partialSync), 6000L, 6000L);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TPSRunnable(), 0L, 0L);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TimeTracker(), 20L, 20L);
        Thread thread = new Thread(new PanelRunnables());
        thread.start();
    }

    public static GameProfile createNewGameProfile(String name, UUID uuid) {
        Future<GameProfile> future = SessionManager.getService().submit(() -> {
            Session session = SessionManager.openSession();

            GameProfilesEntity entity = new GameProfilesEntity();
            entity.setName(name);
            entity.setUuid(uuid.toString());
            entity.setDailyRewardLast(LocalDateTime.now());

            SettingsEntity settings = new SettingsEntity();
            KitPvpDataEntity kitPvpData = new KitPvpDataEntity();
            EssencesEntity essences = new EssencesEntity();
            CratesEntity crates = new CratesEntity();
            //GameProfileStatisticsEntity statistics = new GameProfileStatisticsEntity();

            entity.setSettings(settings);
            entity.setKitPvpData(kitPvpData);
            entity.setEssences(essences);
            entity.setCrates(crates);
            //entity.setGameProfileStatistics(statistics);
            settings.setGameProfile(entity);
            kitPvpData.setGameProfile(entity);
            essences.setGameProfile(entity);
            crates.setGameProfile(entity);
            //statistics.setGameProfile(entity);

            session.beginTransaction();
            session.saveOrUpdate(entity);
            session.getTransaction().commit();
            SessionManager.closeSession(session);

            return new GameProfile(entity);
        });
        GameProfile gameProfile = null;
        try {
            gameProfile = future.get();
        } catch (InterruptedException | ExecutionException ex) {
            UpdateManager.sendLocalizedLog(Level.WARNING, ex, "Unable to create new GameProfile", "Username: " + name, "UUID: " + uuid.toString());
        }
        if (gameProfile != null)
            BattlegroundsCore.getInstance().getServer().getLogger().info("GameProfile created for user " + name + " with UUID " + uuid.toString());
        return gameProfile;
    }

    public void sendNoPermission(Player player) {
        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Sorry! " + ChatColor.GRAY + "You aren't allowed to use this command!");
        EventSound.playSound(player, EventSound.ACTION_FAIL);
    }

    public void sendIncorrectUsage(Player player, String msg) {
        player.sendMessage(new MessageBuilder(ChatColor.RED).bold().create() + "Oops! " + ChatColor.GRAY + "Try this: " + ChatColor.RED + msg);
        EventSound.playSound(player, EventSound.ACTION_FAIL);
    }

    public void sendNoResults(Player player, String msg) {
        player.sendMessage(new MessageBuilder(ChatColor.RED).bold().create() + "Sorry! " + ChatColor.GRAY + "I couldn't " + msg);
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

    public static void sendRegisterMessage(Player player) {
        GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());
        if (gameProfile != null) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.2F);
            player.sendMessage(" ");
            player.sendMessage(ChatColor.GRAY + "You need a " + ChatColor.GOLD + "Battlegrounds Panel " + ChatColor.GRAY + "account!");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.AQUA + "The Battlegrounds Panel is used for");
            player.sendMessage(ChatColor.WHITE + "  \u00BB " + ChatColor.GRAY + "Checking leaderboards and your personal stats");
            player.sendMessage(ChatColor.WHITE + "  \u00BB " + ChatColor.GRAY + "Seeing what kits and cosmetics you own");
            player.sendMessage(ChatColor.WHITE + "  \u00BB " + ChatColor.GRAY + "Opening Battle Crates and buying random kits");
            player.sendMessage(ChatColor.WHITE + "  \u00BB " + ChatColor.GRAY + "Creating & managing clans and friends");
            player.sendMessage(ChatColor.WHITE + "  \u00BB " + ChatColor.GRAY + "So much more!");

            player.sendMessage(" ");
            TextComponent component = new TextComponent(ChatColor.RED + "\u00BB " + ChatColor.YELLOW + "Click here to register an account!" + ChatColor.RED + " \u00AB");
            BaseComponent[] hoverText = {
                    new TextComponent(ChatColor.GRAY + "Registration is" + ChatColor.GREEN + " free" + ChatColor.GRAY + "! No need to pay!\n"),
                    new TextComponent(ChatColor.GRAY + "In order to register, you'll need to provide: \n"),
                    new TextComponent(ChatColor.WHITE + "  \u00BB " + ChatColor.YELLOW + "A valid email " + ChatColor.GRAY + "(for account recovery)\n"),
                    new TextComponent(ChatColor.WHITE + "  \u00BB " + ChatColor.YELLOW + "A secure password " + ChatColor.GRAY + "(which we encrypt for you!)\n"),
                    new TextComponent("\n"),
                    new TextComponent(ChatColor.GRAY + "This link provided is unique to " + ChatColor.ITALIC + "your account" + ChatColor.GRAY + ". Awesome, right?")
            };
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://dashboard.battlegroundspvp.com/register.php?token=" + gameProfile.getToken()));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
            player.spigot().sendMessage(component);
            player.sendMessage(" ");
            player.sendMessage(ChatColor.GRAY + "You can always type " + ChatColor.RED + "/panel register" + ChatColor.GRAY + " if you lose the link above!");
            player.sendMessage(" ");
        }
    }

    private static void reloadSnapshots() {
        SessionManager.getService().submit(() -> {
            Session session = SessionManager.openSession();
            ServerDataEntity serverData = session.get(ServerDataEntity.class, 1);
            setDaySnapshot(serverData.getSnapshotDay());
            setMonthSnapshot(serverData.getSnapshotMonth());
            setYearSnapshot(serverData.getSnapshotYear());
            SessionManager.closeSession(session);
        });
    }

    private static PluginCommand getReflectCommand(String name, Plugin plugin) {
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

    private static CommandMap getCommandMap() {
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