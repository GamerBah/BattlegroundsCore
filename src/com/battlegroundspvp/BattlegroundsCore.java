package com.battlegroundspvp;
/* Created by GamerBah on 6/1/2017 */

import com.battlegroundspvp.administration.commands.*;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.administration.data.sql.EssencesEntity;
import com.battlegroundspvp.administration.data.sql.GameProfilesEntity;
import com.battlegroundspvp.administration.data.sql.KitPvpDataEntity;
import com.battlegroundspvp.administration.data.sql.SettingsEntity;
import com.battlegroundspvp.commands.FriendCommand;
import com.battlegroundspvp.commands.PingCommand;
import com.battlegroundspvp.listeners.InventoryClickListener;
import com.battlegroundspvp.listeners.ServerListPingListener;
import com.battlegroundspvp.listeners.WeatherChangeListener;
import com.battlegroundspvp.playerevents.*;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.punishments.commands.*;
import com.battlegroundspvp.runnables.*;
import com.battlegroundspvp.utils.ChatFilter;
import com.battlegroundspvp.utils.enums.Advancements;
import com.battlegroundspvp.utils.enums.ColorBuilder;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.enums.Time;
import com.battlegroundspvp.utils.inventories.InventoryBuilder;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.Herbystar.TTA.TTA_Methods;
import lombok.Getter;
import net.gpedro.integrations.slack.SlackApi;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Sign;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

public class BattlegroundsCore extends JavaPlugin {

    @Getter
    public static Map<Player, Player> pendingFriends = new HashMap<>();
    @Getter
    public static HashMap<Player, HashMap<Punishment.Reason, Integer>> punishmentCreation = new HashMap<>();

    @Getter
    private static BattlegroundsCore instance = null;
    @Getter
    private static SessionFactory sessionFactory = null;
    @Getter
    private static ProtocolManager protocolManager;

    @Getter
    private static HashSet<UUID> afk = new HashSet<>();
    @Getter
    private static HashSet<UUID> cmdspies = new HashSet<>();
    @Getter
    private static HashSet<Player> fallDmg = new HashSet<>();
    @Getter
    private static List<GameProfile> gameProfiles = new ArrayList<>();

    //@Getter private GlobalStats globalStats = null;

    @Getter
    private HashMap<UUID, ArrayList<Punishment>> playerPunishments = new HashMap<>();

    @Getter
    private Map<UUID, UUID> messagers = new HashMap<>();

    @Getter
    private List<String> filteredWords = new ArrayList<>();
    @Getter
    private List<String> safeWords = new ArrayList<>();
    @Getter
    private List<String> autoMessages = new ArrayList<>();

    @Getter
    private List<Location> uLaunchers = new ArrayList<>();
    @Getter
    private List<Location> fLaunchers = new ArrayList<>();
    @Getter
    private List<Location> fLaunchersParticle = new ArrayList<>();
    @Getter
    private List<Location> uLaunchersParticle = new ArrayList<>();

    public SlackApi slackReports = null;
    public SlackApi slackStaffRequests = null;
    public SlackApi slackDonations = null;
    public SlackApi slackPunishments = null;
    public SlackApi slackErrorReporting = null;

    public void onEnable() {
        instance = this;
        registerCommands();
        registerListeners();

        // Set up Hibernate SessionFactory for SQL
        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
            this.getServer().shutdown();
        }

        protocolManager = ProtocolLibrary.getProtocolManager();
        saveDefaultConfig();

        // Reload player data on reload
        reloadGameProfiles();
        for (Player player : getServer().getOnlinePlayers()) {
            if (player != null) {
                TTA_Methods.createBossBar(player, "", 0.0, BarStyle.SOLID, BarColor.WHITE, BarFlag.CREATE_FOG, false);
                TTA_Methods.removeBossBar(player);
                //GameProfile gameProfile = getGameProfile(player.getUniqueId());
                respawn(player);
                TTA_Methods.sendTitle(player, null, 0, 0, 0, null, 0, 0, 0);
            }
        }

        // Initialize Launcher Lists
        uLaunchers = (List<Location>) getConfig().getList("launchersUp");
        if (!uLaunchers.isEmpty())
            uLaunchers.remove(0);
        fLaunchers = (List<Location>) getConfig().getList("launchersForward");
        if (!fLaunchers.isEmpty())
            fLaunchers.remove(0);

        for (Location location : uLaunchers)
            uLaunchersParticle.add(location.clone().add(0, 1, 0));
        for (Location location : fLaunchers)
            fLaunchersParticle.add(location.clone().add(0, 1, 0));

        // Initialize Various Repeating Tasks
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new UpdateRunnable(this), 120, 120);
        //getServer().getScheduler().scheduleSyncRepeatingTask(this, new DonationUpdater(this), 0, 20);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TrailRunnable(this), 0, 2);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new HelixRunnable(this), 0, 5);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new AFKRunnable(this), 0, 20);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new PunishmentRunnable(this), 0, 20L);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new WorldParticlesRunnable(this), 0, 2L);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new MessageRunnable(this), 0, 6000L);

        // Save Filter File
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

        // Initialize SlackApis
        slackReports = new SlackApi("https://hooks.slack.com/services/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");
        slackStaffRequests = new SlackApi("https://hooks.slack.com/services/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");
        slackDonations = new SlackApi("https://hooks.slack.com/services/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");
        slackPunishments = new SlackApi("https://hooks.slack.com/services/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");
        slackErrorReporting = new SlackApi("https://hooks.slack.com/services/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");

        setUpPacketHandlers();
    }

    public void onDisable() {
        for (Advancements advancements : Advancements.values())
            advancements.getCustomAdvancement().getApi().remove();
        uLaunchers.add(0, new Location(getServer().getWorlds().get(0), 3.1415, 3.1415, 3.1415));
        fLaunchers.add(0, new Location(getServer().getWorlds().get(0), 3.1415, 3.1415, 3.1415));
        getConfig().set("launchersUp", uLaunchers);
        getConfig().set("launchersForward", fLaunchers);
        saveConfig();
        sessionFactory.close();
    }


    private void setUp() throws Exception {
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

    public void setUpPacketHandlers() {
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
                                    sign.setLine(1, "§e§l^ ^ ^");
                                    sign.setLine(2, "§cEnter your search");
                                    sign.setLine(3, "§cterm here!");
                                    sign.update();
                                }
                            }
                        }
                    }).syncStart();
        for (Advancements advancement : Advancements.values())
            advancement.getCustomAdvancement().register();
    }

    private void registerCommands() {
        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("clearchat").setExecutor(new ChatCommands(this));
        getCommand("lockchat").setExecutor(new ChatCommands(this));
        getCommand("staff").setExecutor(new StaffChatCommand(this));
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

        pluginManager.registerEvents(new ChatFilter(this), this);
        pluginManager.registerEvents(new ServerListPingListener(this), this);
        pluginManager.registerEvents(new InventoryClickListener(), this);
        pluginManager.registerEvents(new WeatherChangeListener(), this);
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
        gameProfilesEntity.setUuid(uuid);
        gameProfilesEntity.setDailyRewardLast(LocalDateTime.now());

        SettingsEntity settingsEntity = new SettingsEntity();
        KitPvpDataEntity kitPvpDataEntity = new KitPvpDataEntity();
        EssencesEntity essencesEntity = new EssencesEntity();

        gameProfilesEntity.setSettings(settingsEntity);
        gameProfilesEntity.setKitPvpData(kitPvpDataEntity);
        gameProfilesEntity.setEssences(essencesEntity);
        settingsEntity.setGameProfile(gameProfilesEntity);
        kitPvpDataEntity.setGameProfile(gameProfilesEntity);
        essencesEntity.setGameProfile(gameProfilesEntity);

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
        player.sendMessage(new ColorBuilder(ChatColor.RED).bold().create() + "Sorry! " + ChatColor.GRAY + "I wasn't able to ");
        EventSound.playSound(player, EventSound.ACTION_FAIL);
    }

    public boolean hasAdvancement(Player player, Advancements advancements) {
        Bukkit.reloadData();
        Advancement advancement = Bukkit.getAdvancement(advancements.getCustomAdvancement().getKey());
        return player.getAdvancementProgress(advancement).getRemainingCriteria().size() <= 0;
    }

    public void warnPlayer(Player player, GameProfile targetData, Punishment.Reason reason) {
        if (!WarnCommand.getWarned().containsKey(targetData.getUuid())) {
            WarnCommand.getWarned().put(targetData.getUuid(), 1);
        } else {
            WarnCommand.getWarned().put(targetData.getUuid(), WarnCommand.getWarned().get(targetData.getUuid()) + 1);
        }
        int warns = WarnCommand.getWarned().get(targetData.getUuid());
        if (WarnCommand.getWarned().get(targetData.getUuid()) == 5) {
            getServer().getOnlinePlayers().stream().filter(players ->
                    getGameProfile(players.getUniqueId()).hasRank(Rank.HELPER))
                    .forEach(players -> players.sendMessage(
                            new ColorBuilder(ChatColor.DARK_RED).bold().create() + " [ARES] " + ChatColor.GOLD + targetData.getName() + new ColorBuilder(ChatColor.DARK_RED).bold().create() + " (5)"
                                    + ChatColor.RED + "was " + (reason.getType().equals(Punishment.Type.MUTE) || reason.getType().equals(Punishment.Type.ALL) ? "muted" : "kicked")
                                    + " for " + ChatColor.GRAY + reason.getName() + (reason.getType().equals(Punishment.Type.MUTE)
                                    || reason.getType().equals(Punishment.Type.ALL) ? " for " + ChatColor.GRAY + Time.toString(reason.getLength() * 1000, true) : "")));
            getServer().getOnlinePlayers().stream().filter(players ->
                    getGameProfile(players.getUniqueId()).hasRank(Rank.HELPER))
                    .forEach(players -> players.playSound(players.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1));
            if (player != null) {
                player.closeInventory();
            }

            if (reason.getType().equals(Punishment.Type.MUTE) || reason.getType().equals(Punishment.Type.ALL)) {
                HashMap<Punishment.Reason, Integer> punishment = new HashMap<>();
                punishment.put(reason, reason.getLength());
                MuteCommand.mutePlayer(targetData.getUuid(), player, punishment);
            } else {
                HashMap<Punishment.Reason, Integer> punishment = new HashMap<>();
                punishment.put(reason, -1);
                KickCommand.kickPlayer(targetData.getUuid(), player, punishment);
            }
            return;
        }
        if (WarnCommand.getWarned().get(targetData.getUuid()) >= 3) {
            getServer().getOnlinePlayers().stream().filter(players ->
                    getGameProfile(players.getUniqueId()).hasRank(Rank.HELPER))
                    .forEach(players -> players.sendMessage(player != null ?
                            new ColorBuilder(ChatColor.DARK_RED).bold().create() + " !!! " + ChatColor.GRAY + player.getName() + ChatColor.RED + " warned "
                                    + ChatColor.GOLD + targetData.getName() + " (" + warns + ")" + ChatColor.RED + " for " + ChatColor.GRAY + reason.getName()
                            : new ColorBuilder(ChatColor.DARK_RED).bold().create() + " !!! " + new ColorBuilder(ChatColor.AQUA).bold().create() + "Ares" + ChatColor.GRAY + ": " + ChatColor.RED + "ItemBuilder automatically warned "
                            + ChatColor.GOLD + targetData.getName() + " (" + warns + ")" + ChatColor.RED + " for you " + ChatColor.GRAY + "(" + reason.getName() + ")"));
            getServer().getOnlinePlayers().stream().filter(players ->
                    getGameProfile(players.getUniqueId()).hasRank(Rank.HELPER))
                    .forEach(players -> players.playSound(players.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1));
            if (player != null) {
                player.closeInventory();
            }
            return;
        }
        if (player != null) {
            player.closeInventory();
        }
        getServer().getOnlinePlayers().stream().filter(players ->
                getGameProfile(players.getUniqueId()).hasRank(Rank.HELPER))
                .forEach(players -> players.sendMessage(player != null ?
                        ChatColor.GRAY + player.getName() + ChatColor.RED + " warned "
                                + ChatColor.GOLD + targetData.getName() + " (" + warns + ")" + ChatColor.RED + " for " + ChatColor.GRAY + reason.getName()
                        : new ColorBuilder(ChatColor.AQUA).bold().create() + "Ares" + ChatColor.GRAY + ": " + ChatColor.RED + "ItemBuilder automatically warned "
                        + ChatColor.GOLD + targetData.getName() + " (" + warns + ")" + ChatColor.RED + " for you " + ChatColor.GRAY + "(" + reason.getName() + ")"));
        getServer().getOnlinePlayers().stream().filter(players ->
                getGameProfile(players.getUniqueId()).hasRank(Rank.HELPER))
                .forEach(players -> players.playSound(players.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1));
    }

    // Respawn at location
    public void respawn(Player player, Location location) {
        player.spigot().respawn();
        player.teleport(location);
        getServer().getPluginManager().callEvent(new PlayerRespawnEvent(player, location, false));
    }

    // Respawn at world spawn
    public void respawn(Player player) {
        respawn(player, player.getWorld().getSpawnLocation().add(0.5, 0, 0.5));
    }

    public int getTotalEssenceAmount(Player player) {
        GameProfile gameProfile = getGameProfile(player.getUniqueId());
        return (gameProfile.getEssenceData().getOne50()
                + gameProfile.getEssenceData().getOne100()
                + gameProfile.getEssenceData().getOne150()
                + gameProfile.getEssenceData().getThree50()
                + gameProfile.getEssenceData().getThree100()
                + gameProfile.getEssenceData().getThree150()
                + gameProfile.getEssenceData().getSix50()
                + gameProfile.getEssenceData().getSix100()
                + gameProfile.getEssenceData().getSix150()
        );
    }

    public static String getLore(ItemStack itemStack, int line) {
        return itemStack.getItemMeta().getLore().get(line - 1);
    }

    public ArrayList<Punishment> getMutes(GameProfile targetData) {
        ArrayList<Punishment> mutes = new ArrayList<>();
        if (this.getPlayerPunishments().get(targetData.getUuid()) != null) {
            for (int i = 0; i < this.getPlayerPunishments().get(targetData.getUuid()).size(); i++)
                if (this.getPlayerPunishments().get(targetData.getUuid()).get(i).getType().equals(Punishment.Type.MUTE))
                    mutes.add(this.getPlayerPunishments().get(targetData.getUuid()).get(i));
            if (mutes.isEmpty())
                return null;
        } else return null;
        return mutes;
    }

    public ArrayList<Punishment> getKicks(GameProfile targetData) {
        ArrayList<Punishment> kicks = new ArrayList<>();
        if (this.getPlayerPunishments().get(targetData.getUuid()) != null) {
            for (int i = 0; i < this.getPlayerPunishments().get(targetData.getUuid()).size(); i++)
                if (this.getPlayerPunishments().get(targetData.getUuid()).get(i).getType().equals(Punishment.Type.KICK))
                    kicks.add(this.getPlayerPunishments().get(targetData.getUuid()).get(i));
            if (kicks.isEmpty())
                return null;
        } else return null;
        return kicks;
    }

    public ArrayList<Punishment> getBans(GameProfile targetData) {
        ArrayList<Punishment> bans = new ArrayList<>();
        if (this.getPlayerPunishments().get(targetData.getUuid()) != null) {
            for (int i = 0; i < this.getPlayerPunishments().get(targetData.getUuid()).size(); i++)
                if (this.getPlayerPunishments().get(targetData.getUuid()).get(i).getType().equals(Punishment.Type.BAN))
                    bans.add(this.getPlayerPunishments().get(targetData.getUuid()).get(i));
            if (bans.isEmpty())
                return null;
        } else return null;
        return bans;
    }

    public ArrayList<Punishment> getTempBans(GameProfile targetData) {
        ArrayList<Punishment> tempBans = new ArrayList<>();
        if (this.getPlayerPunishments().get(targetData.getUuid()) != null) {
            for (int i = 0; i < this.getPlayerPunishments().get(targetData.getUuid()).size(); i++)
                if (this.getPlayerPunishments().get(targetData.getUuid()).get(i).getType().equals(Punishment.Type.TEMP_BAN))
                    tempBans.add(this.getPlayerPunishments().get(targetData.getUuid()).get(i));
            if (tempBans.isEmpty())
                return null;
        } else return null;
        return tempBans;
    }

}
