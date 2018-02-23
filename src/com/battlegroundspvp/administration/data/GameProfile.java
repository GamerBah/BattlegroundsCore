package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 6/18/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.commands.WarnCommand;
import com.battlegroundspvp.administration.data.sql.GameProfilesEntity;
import com.battlegroundspvp.administration.donations.Essence;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.punishments.commands.BanCommand;
import com.battlegroundspvp.punishments.commands.MuteCommand;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.enums.Time;
import com.battlegroundspvp.utils.messages.FriendMessages;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GameProfile {

    @Getter
    private final GameProfilesEntity entity;
    @Getter
    private final int id;
    @Getter
    private final UUID uuid;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private Rank rank;
    @Getter
    @Setter
    private boolean online;
    @Getter
    @Setter
    private int coins, playersRecruited;
    @Getter
    @Setter
    private int recruitedBy;
    @Getter
    @Setter
    private boolean dailyReward;
    @Getter
    @Setter
    private LocalDateTime dailyRewardLast, lastOnline;
    @Getter
    @Setter
    private ArrayList<Integer> friends;
    @Getter
    @Setter
    private ArrayList<Integer> friendRequests;
    @Getter
    @Setter
    private ArrayList<Integer> cosmetics;
    @Getter
    private String token;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String email;
    @Getter
    private final KitPvpData kitPvpData;
    @Getter
    private final PlayerSettings playerSettings;
    @Getter
    private final EssenceData essenceData;
    @Getter
    private final CratesData cratesData;
    @Getter
    private final PunishmentData punishmentData;
    @Getter
    private final BugReportData bugReportData;

    @Getter
    @Setter
    public HashMap<GameProfile, Integer> friendRequestCooldowns = new HashMap<>();

    public GameProfile(GameProfilesEntity entity) {
        this.entity = entity;
        this.id = entity.getId();
        this.uuid = UUID.fromString(entity.getUuid());
        this.name = entity.getName();
        this.rank = Rank.ofId(entity.getRank());
        this.online = entity.isOnline();
        this.coins = entity.getCoins();
        this.playersRecruited = entity.getPlayersRecruited();
        this.recruitedBy = entity.getRecruitedBy();
        this.dailyReward = entity.isDailyReward();
        this.dailyRewardLast = entity.getDailyRewardLast();
        this.lastOnline = entity.getLastOnline();
        this.password = entity.getPassword();
        this.email = entity.getEmail();
        this.kitPvpData = new KitPvpData(entity.getKitPvpData());
        this.playerSettings = new PlayerSettings(entity.getSettings());
        this.essenceData = new EssenceData(entity.getEssences());
        this.cratesData = new CratesData(entity.getCrates());
        this.punishmentData = new PunishmentData(entity.getPunishments(), entity);
        this.bugReportData = new BugReportData(entity.getBugReports(), entity);
        {
            ArrayList<Integer> list = new ArrayList<>();
            if (entity.getFriends() != null) {
                String friends = entity.getFriends().replace("[", "").replace("]", "").replace(" ", "");
                if (!friends.equals(""))
                    for (String id : friends.split(","))
                        list.add(Integer.parseInt(id));
            }
            this.friends = list;
        }
        {
            ArrayList<Integer> list = new ArrayList<>();
            if (entity.getFriendRequests() != null) {
                String requests = entity.getFriendRequests().replace("[", "").replace("]", "").replace(" ", "");
                if (!requests.equals(""))
                    for (String id : requests.split(","))
                        list.add(Integer.parseInt(id));
            }
            this.friendRequests = list;
        }
        {
            ArrayList<Integer> list = new ArrayList<>();
            if (entity.getCosmetics() != null) {
                String cosmetics = entity.getCosmetics().replace("[", "").replace("]", "").replace(" ", "");
                if (!cosmetics.equals(""))
                    for (String id : cosmetics.split(","))
                        list.add(Integer.parseInt(id));
            }
            this.cosmetics = list;
        }
        if (entity.getToken() == null)
            this.token = UUID.randomUUID().toString();
        else this.token = entity.getToken();
    }

    public GameProfile addCoins(int amount) {
        setCoins(getCoins() + amount);
        return this;
    }

    public GameProfile addFriend(GameProfile gameProfile) {
        friendRequests.remove(gameProfile.getId());
        friends.add(gameProfile.getId());
        gameProfile.getFriends().add(id);
        FriendMessages.sendAccepted(this, gameProfile);
        return this;
    }

    public GameProfile addPlayerRecruited() {
        setPlayersRecruited(this.playersRecruited + 1);
        return this;
    }

    public void ban(Punishment.Reason reason, GameProfile enforcerProfile) {
        if (isBanned()) {
            BanCommand.sendErrorMessage(enforcerProfile, this);
            return;
        }

        if (!WarnCommand.getWarned().containsKey(this.uuid))
            WarnCommand.getWarned().remove(this.uuid);

        // TODO:
        // BattlegroundsCore.getInstance().getGlobalStats().setTotalBans(BattlegroundsCore.getInstance().getGlobalStats().getTotalBans() + 1);

        punishmentData.getPunishments().add(new Punishment(this.id, Punishment.Type.BAN, LocalDateTime.now(),
                -1, LocalDateTime.parse("1998-03-27T17:31:41.592"), enforcerProfile.id, reason, false));

        BaseComponent baseComponent = new TextComponent(ChatColor.RED + enforcerProfile.getName() + " permanently banned " + ChatColor.RED + this.name);
        baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Reason: "
                + ChatColor.GOLD + reason.getName()).create()));

        BattlegroundsCore.getInstance().getServer().getOnlinePlayers().stream().filter(staff ->
                BattlegroundsCore.getInstance().getGameProfile(staff.getUniqueId()).hasRank(Rank.HELPER)).forEach(staff -> {
            staff.spigot().sendMessage(baseComponent);
            EventSound.playSound(staff, EventSound.CLICK);
        });

        if (isOnline())
            getPlayer().kickPlayer(ChatColor.RED + "You were permanently banned by " + ChatColor.GOLD + enforcerProfile.getName()
                    + ChatColor.RED + " for " + ChatColor.GOLD + reason.getName() + "\n"
                    + ChatColor.YELLOW + reason.getMessage() + "\n\n" + ChatColor.GRAY + "Appeal your ban on the forums: battlegroundspvp.com/forums");

    }

    public Punishment getCurrentBan() {
        if (isBanned()) {
            for (Punishment punishment : getPunishmentData().getTempBans())
                if (!punishment.isPardoned())
                    return punishment;
            for (Punishment punishment : getPunishmentData().getBans())
                if (!punishment.isPardoned())
                    return punishment;
        }
        return null;
    }

    public Punishment getCurrentMute() {
        if (isMuted()) {
            for (Punishment punishment : getPunishmentData().getMutes()) {
                if (!punishment.isPardoned())
                    return punishment;
            }
        }
        return null;
    }

    public long getFriendRequestCooldown(GameProfile gameProfile) {
        if (friendRequestCooldowns.containsKey(gameProfile))
            return friendRequestCooldowns.get(gameProfile) * 1000;
        return 0;
    }

    public long getLastOnlineTime() {
        return (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - getLastOnline().toEpochSecond(ZoneOffset.UTC)) * 1000;
    }

    public Player getPlayer() {
        return BattlegroundsCore.getInstance().getServer().getPlayer(this.uuid);
    }

    public boolean hasFriend(GameProfile gameProfile) {
        return friends.contains(gameProfile.getId()) && gameProfile.getFriends().contains(id);
    }

    public boolean hasFriendRequestFrom(GameProfile fromProfile) {
        return fromProfile.getFriendRequests().contains(id);
    }

    public boolean hasFriendRequestCooldown(GameProfile gameProfile) {
        return friendRequestCooldowns.containsKey(gameProfile);
    }

    public boolean hasRank(Rank rank) {
        return this.rank.getLevel() >= rank.getLevel();
    }

    public boolean isBanned() {
        for (Punishment punishment : getPunishmentData().getTempBans())
            if (!punishment.isPardoned())
                return true;
        for (Punishment punishment : getPunishmentData().getBans())
            if (!punishment.isPardoned())
                return true;
        return false;
    }

    public boolean isMuted() {
        for (Punishment punishment : getPunishmentData().getMutes())
            if (!punishment.isPardoned())
                return true;
        return false;
    }

    public void kick(Punishment.Reason reason, GameProfile enforcerProfile) {
        if (!isOnline()) {
            enforcerProfile.sendMessage(ChatColor.RED + "That player isn't online!");
            enforcerProfile.playSound(EventSound.ACTION_FAIL);
            return;
        }

        if (!WarnCommand.getWarned().containsKey(this.uuid))
            WarnCommand.getWarned().remove(this.uuid);

        // TODO:
        // BattlegroundsCore.getInstance().getGlobalStats().setTotalKicks(BattlegroundsCore.getInstance().getGlobalStats().getTotalKicks() + 1);

        punishmentData.getPunishments().add(new Punishment(this.id, Punishment.Type.KICK, LocalDateTime.now(),
                0, LocalDateTime.now(), enforcerProfile.id, reason, true));

        BaseComponent baseComponent = new TextComponent(ChatColor.RED + enforcerProfile.getName() + " kicked " + ChatColor.RED + this.name);
        baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Reason: " + ChatColor.WHITE + reason.getName()).create()));

        BattlegroundsCore.getInstance().getServer().getOnlinePlayers().stream().filter(staff ->
                BattlegroundsCore.getInstance().getGameProfile(staff.getUniqueId()).hasRank(Rank.HELPER)).forEach(staff -> {
            staff.spigot().sendMessage(baseComponent);
            EventSound.playSound(staff, EventSound.CLICK);
        });

        getPlayer().kickPlayer(ChatColor.RED + "You were kicked by " + ChatColor.GOLD + enforcerProfile.getName() + ChatColor.RED + " for " + ChatColor.GOLD + reason.getName() + "\n"
                + ChatColor.YELLOW + reason.getMessage() + "\n\n" + ChatColor.GRAY + "If you feel that staff abuse was an issue, please email support@battlegroundspvp.com");
    }

    public void mute(Punishment.Reason reason, int duration, GameProfile enforcerProfile) {
        if (isMuted()) {
            MuteCommand.sendErrorMessage(enforcerProfile, this);
            return;
        }

        if (!WarnCommand.getWarned().containsKey(this.uuid))
            WarnCommand.getWarned().remove(this.uuid);

        // TODO:
        // BattlegroundsCore.getInstance().getGlobalStats().setTotalMutes(BattlegroundsCore.getInstance().getGlobalStats().getTotalMutes() + 1);

        punishmentData.getPunishments().add(new Punishment(this.id, Punishment.Type.MUTE, LocalDateTime.now(),
                duration * 1000, LocalDateTime.now().plusSeconds(duration), enforcerProfile.id, reason, false));

        BaseComponent baseComponent = new TextComponent(ChatColor.RED + enforcerProfile.getName() + " muted " + ChatColor.RED + this.name);
        baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Reason: "
                + ChatColor.WHITE + reason.getName() + "\n" + ChatColor.GRAY + "Time: " + ChatColor.WHITE + Time.toString(duration * 1000, false)).create()));

        BattlegroundsCore.getInstance().getServer().getOnlinePlayers().stream().filter(staff ->
                BattlegroundsCore.getInstance().getGameProfile(staff.getUniqueId()).hasRank(Rank.HELPER)).forEach(staff -> {
            staff.spigot().sendMessage(baseComponent);
            EventSound.playSound(staff, EventSound.CLICK);
        });

        if (isOnline())
            sendMessage(ChatColor.GOLD + enforcerProfile.getName() + ChatColor.RED + " muted you for " + ChatColor.GOLD + Time.toString(duration * 1000, true)
                    + ChatColor.RED + " for " + ChatColor.GOLD + reason.getName());
    }

    public GameProfile playSound(EventSound eventSound) {
        if (isOnline())
            EventSound.playSound(BattlegroundsCore.getInstance().getServer().getPlayer(this.uuid), eventSound);
        return this;
    }

    public GameProfile removeFriend(GameProfile gameProfile) {
        friends.remove(gameProfile.getId());
        gameProfile.getFriends().remove(this.getId());
        return this;
    }

    public GameProfile removeFriendRequest(GameProfile gameProfile) {
        friendRequests.remove(gameProfile.getId());
        FriendMessages.sendDeclined(this, gameProfile);
        return this;
    }

    public GameProfile respawn() {
        if (isOnline())
            BattlegroundsCore.getInstance().getServer().getPlayer(this.uuid).spigot().respawn();
        return this;
    }

    public GameProfile sendFriendRequest(GameProfile gameProfile) {
        if (hasFriend(gameProfile)) {
            sendMessage(ChatColor.RED + "You are already friends with " + gameProfile.getName() + "!");
            return this;
        }
        friendRequests.add(gameProfile.getId());
        FriendMessages.sendRequested(this, gameProfile);
        return this;
    }

    public GameProfile sendMessage(String message) {
        if (isOnline())
            BattlegroundsCore.getInstance().getServer().getPlayer(this.uuid).sendMessage(message);
        return this;
    }

    public void tempBan(Punishment.Reason reason, int duration, GameProfile enforcerProfile) {
        if (isBanned()) {
            BanCommand.sendErrorMessage(enforcerProfile, this);
            return;
        }

        if (!WarnCommand.getWarned().containsKey(this.uuid))
            WarnCommand.getWarned().remove(this.uuid);

        // TODO:
        // BattlegroundsCore.getInstance().getGlobalStats().setTotalBans(BattlegroundsCore.getInstance().getGlobalStats().getTotalBans() + 1);

        punishmentData.getPunishments().add(new Punishment(this.id, Punishment.Type.TEMP_BAN, LocalDateTime.now(),
                duration * 1000, LocalDateTime.now().plusSeconds(duration), enforcerProfile.id, reason, false));

        BaseComponent baseComponent = new TextComponent(ChatColor.RED + enforcerProfile.getName() + " temp-banned " + ChatColor.RED + this.name);
        baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Reason: "
                + ChatColor.WHITE + reason.getName() + "\n" + ChatColor.GRAY + "Time: " + ChatColor.WHITE + Time.toString(duration * 1000, false)).create()));

        BattlegroundsCore.getInstance().getServer().getOnlinePlayers().stream().filter(staff ->
                BattlegroundsCore.getInstance().getGameProfile(staff.getUniqueId()).hasRank(Rank.HELPER)).forEach(staff -> {
            staff.spigot().sendMessage(baseComponent);
            EventSound.playSound(staff, EventSound.CLICK);
        });

        if (isOnline())
            getPlayer().kickPlayer(ChatColor.RED + "You were temporarily banned by " + ChatColor.GOLD + enforcerProfile.getName()
                    + ChatColor.RED + " for " + ChatColor.GOLD + Time.toString(duration * 1000, false) + ChatColor.RED + " for "
                    + ChatColor.GOLD + reason.getName() + "\n" + ChatColor.YELLOW + reason.getMessage() + "\n\n"
                    + ChatColor.GRAY + "Appeal your ban on the forums: battlegroundspvp.com/forums");

    }

    public void unban(GameProfile gameProfile, Punishment punishment) {
        if (punishment.isPardoned()) {
            gameProfile.sendMessage(ChatColor.RED + "That player isn't banned!");
            gameProfile.playSound(EventSound.ACTION_FAIL);
            return;
        }

        BattlegroundsCore.getInstance().getServer().getOnlinePlayers().stream().filter(staff ->
                BattlegroundsCore.getInstance().getGameProfile(staff.getUniqueId()).hasRank(Rank.HELPER)).forEach(staff ->
                staff.sendMessage(ChatColor.RED + gameProfile.getName() + " unbanned " + this.name));
        punishment.setPardoned(true);
    }

    public void unmute(GameProfile gameProfile, Punishment punishment) {
        if (punishment.isPardoned()) {
            gameProfile.sendMessage(ChatColor.RED + "That player isn't muted!");
            gameProfile.playSound(EventSound.ACTION_FAIL);
            return;
        }

        BattlegroundsCore.getInstance().getServer().getOnlinePlayers().stream().filter(staff ->
                BattlegroundsCore.getInstance().getGameProfile(staff.getUniqueId()).hasRank(Rank.HELPER)).forEach(staff ->
                staff.sendMessage(ChatColor.RED + gameProfile.getName() + " unmuted " + this.name));

        sendMessage(ChatColor.RED + " \nYou were unmuted by " + ChatColor.GOLD + gameProfile.getName());
        sendMessage(ChatColor.GRAY + punishment.getReason().getMessage() + "\n ");
        punishment.setPardoned(true);
    }

    public int getEssenceAmount(final Essence.Type type) {
        switch (type) {
            case ONE_HOUR_50_PERCENT:
                return essenceData.getOne50();
            case ONE_HOUR_100_PERCENT:
                return essenceData.getOne100();
            case ONE_HOUR_150_PERCENT:
                return essenceData.getOne150();
            case THREE_HOUR_50_PERCENT:
                return essenceData.getThree50();
            case THREE_HOUR_100_PERCENT:
                return essenceData.getThree100();
            case THREE_HOUR_150_PERCENT:
                return essenceData.getThree150();
            case SIX_HOUR_50_PERCENT:
                return essenceData.getSix50();
            case SIX_HOUR_100_PERCENT:
                return essenceData.getSix100();
            case SIX_HOUR_150_PERCENT:
                return essenceData.getSix150();
        }
        return essenceData.getOne50();
    }

    public int getTotalEssenceAmount() {
        int total = 0;
        for (Essence.Type type : Essence.Type.values())
            total += getEssenceAmount(type);
        return total;
    }

    public void fullSync() {
        BattlegroundsCore.executorService.execute(() -> {
            Session session = BattlegroundsCore.getSessionFactory().openSession();
            session.beginTransaction();
            entity.setName(this.name);
            entity.setRank(this.rank.getId());
            entity.setOnline(this.online);
            entity.setCoins(this.coins);
            entity.setPlayersRecruited(this.playersRecruited);
            entity.setRecruitedBy(this.recruitedBy);
            entity.setDailyReward(this.dailyReward);
            entity.setDailyRewardLast(this.dailyRewardLast);
            entity.setLastOnline(this.lastOnline);
            entity.setFriends(this.friends.toString());
            entity.setFriendRequests(this.friendRequests.toString());
            entity.setToken(this.token);
            entity.setPassword(this.password);
            entity.setEmail(this.email);
            this.kitPvpData.fullSync();
            this.essenceData.sync();
            this.playerSettings.sync();
            this.cratesData.sync();
            this.punishmentData.sync(session);
            this.bugReportData.sync(session);
            entity.setKitPvpData(this.kitPvpData.getEntity());
            entity.setSettings(this.playerSettings.getEntity());
            entity.setEssences(this.essenceData.getEntity());
            entity.setCrates(this.cratesData.getEntity());
            entity.setPunishments(this.punishmentData.getEntities());
            entity.setBugReports(this.bugReportData.getEntities());
            session.merge(entity);
            session.getTransaction().commit();
            session.close();
        });
    }

    public void partialSync() {
        BattlegroundsCore.executorService.execute(() -> {
            Session session = BattlegroundsCore.getSessionFactory().openSession();
            session.beginTransaction();
            entity.setRank(this.rank.getId());
            entity.setOnline(this.online);
            entity.setCoins(this.coins);
            entity.setPlayersRecruited(this.playersRecruited);
            entity.setRecruitedBy(this.recruitedBy);
            entity.setFriends(this.friends.toString());
            entity.setFriendRequests(this.friendRequests.toString());
            entity.setToken(this.token);
            entity.setPassword(this.password);
            entity.setEmail(this.email);
            this.kitPvpData.partialSync();
            this.playerSettings.sync();
            entity.setKitPvpData(this.kitPvpData.getEntity());
            entity.setSettings(this.playerSettings.getEntity());
            session.merge(entity);
            session.getTransaction().commit();
            session.close();
        });
    }

}
