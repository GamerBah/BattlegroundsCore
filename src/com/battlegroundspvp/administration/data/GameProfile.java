package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 6/18/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.commands.WarnCommand;
import com.battlegroundspvp.administration.data.sql.GameProfilesEntity;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.punishments.commands.BanCommand;
import com.battlegroundspvp.punishments.commands.MuteCommand;
import com.battlegroundspvp.utils.enums.Cosmetic;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.enums.Time;
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
    private int coins, playersRecruited;
    @Getter
    @Setter
    private int recruitedBy;
    @Getter
    @Setter
    private boolean dailyReward = false;
    @Getter
    @Setter
    private Cosmetic.Item trail, warcry, gore;
    @Getter
    @Setter
    private LocalDateTime dailyRewardLast, lastOnline;
    @Getter
    @Setter
    private String friends, ownedCosmetics;
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

    public GameProfile(GameProfilesEntity entity) {
        this.entity = entity;
        this.id = entity.getId();
        this.uuid = entity.getUuid();
        this.name = entity.getName();
        this.rank = Rank.fromString(entity.getRank());
        this.coins = entity.getCoins();
        this.playersRecruited = entity.getPlayersRecruited();
        this.recruitedBy = entity.getRecruitedBy();
        this.dailyReward = entity.isDailyReward();
        this.trail = Cosmetic.Item.fromString(entity.getTrail());
        this.warcry = Cosmetic.Item.fromString(entity.getWarcry());
        this.gore = Cosmetic.Item.fromString(entity.getGore());
        this.dailyRewardLast = entity.getDailyRewardLast();
        this.lastOnline = entity.getLastOnline();
        this.friends = entity.getFriends();
        this.ownedCosmetics = entity.getCosmetics();
        this.kitPvpData = new KitPvpData(entity.getKitPvpData());
        this.playerSettings = new PlayerSettings(entity.getSettings());
        this.essenceData = new EssenceData(entity.getEssences());
        this.cratesData = new CratesData(entity.getCrates());
        this.punishmentData = new PunishmentData(entity.getPunishments(), entity);
    }

    public Player getPlayer() {
        return BattlegroundsCore.getInstance().getServer().getPlayer(this.uuid);
    }

    public GameProfile playSound(EventSound eventSound) {
        if (isOnline())
            EventSound.playSound(BattlegroundsCore.getInstance().getServer().getPlayer(this.uuid), eventSound);
        return this;
    }

    public boolean hasRank(Rank rank) {
        return this.rank.getLevel() >= rank.getLevel();
    }

    public GameProfile addCoins(int amount) {
        setCoins(getCoins() + amount);
        return this;
    }

    public GameProfile removeCoins(int amount) {
        setCoins(getCoins() - amount);
        return this;
    }

    public GameProfile addPlayerRecruited() {
        setPlayersRecruited(this.playersRecruited + 1);
        return this;
    }

    public GameProfile respawn() {
        if (isOnline())
            BattlegroundsCore.getInstance().getServer().getPlayer(this.uuid).spigot().respawn();
        return this;
    }

    public GameProfile sendMessage(String message) {
        if (isOnline())
            BattlegroundsCore.getInstance().getServer().getPlayer(this.uuid).sendMessage(message);
        return this;
    }

    public boolean isOnline() {
        if (BattlegroundsCore.getInstance().getServer().getPlayer(uuid) != null) {
            if (BattlegroundsCore.getInstance().getServer().getPlayer(uuid).isOnline())
                return true;
        }
        return false;
    }

    public void mute(Punishment.Reason reason, int duration, GameProfile enforcerProfile) {
        if (isMuted()) {
            MuteCommand.sendErrorMessage(enforcerProfile, this);
            return;
        }

        if (!WarnCommand.getWarned().containsKey(this.uuid))
            WarnCommand.getWarned().remove(this.uuid);

        BattlegroundsCore.getInstance().getGlobalStats().setTotalMutes(BattlegroundsCore.getInstance().getGlobalStats().getTotalMutes() + 1);

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

    public boolean isMuted() {
        for (Punishment punishment : getPunishmentData().getMutes())
            if (!punishment.isPardoned())
                return true;
        return false;
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

    public void kick(Punishment.Reason reason, GameProfile enforcerProfile) {
        if (!isOnline()) {
            enforcerProfile.sendMessage(ChatColor.RED + "That player isn't online!");
            enforcerProfile.playSound(EventSound.ACTION_FAIL);
            return;
        }

        if (!WarnCommand.getWarned().containsKey(this.uuid))
            WarnCommand.getWarned().remove(this.uuid);

        BattlegroundsCore.getInstance().getGlobalStats().setTotalKicks(BattlegroundsCore.getInstance().getGlobalStats().getTotalKicks() + 1);

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

    public void tempBan(Punishment.Reason reason, int duration, GameProfile enforcerProfile) {
        if (isBanned()) {
            BanCommand.sendErrorMessage(enforcerProfile, this);
            return;
        }

        if (!WarnCommand.getWarned().containsKey(this.uuid))
            WarnCommand.getWarned().remove(this.uuid);

        BattlegroundsCore.getInstance().getGlobalStats().setTotalBans(BattlegroundsCore.getInstance().getGlobalStats().getTotalBans() + 1);

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

    public void ban(Punishment.Reason reason, GameProfile enforcerProfile) {
        if (isBanned()) {
            BanCommand.sendErrorMessage(enforcerProfile, this);
            return;
        }

        if (!WarnCommand.getWarned().containsKey(this.uuid))
            WarnCommand.getWarned().remove(this.uuid);

        BattlegroundsCore.getInstance().getGlobalStats().setTotalBans(BattlegroundsCore.getInstance().getGlobalStats().getTotalBans() + 1);

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

    public boolean isBanned() {
        for (Punishment punishment : getPunishmentData().getTempBans())
            if (!punishment.isPardoned())
                return true;
        for (Punishment punishment : getPunishmentData().getBans())
            if (!punishment.isPardoned())
                return true;
        return false;
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

    public void sync() {
        Session session = BattlegroundsCore.getSessionFactory().openSession();
        session.beginTransaction();
        entity.setName(this.name);
        entity.setRank(this.rank.toString());
        entity.setCoins(this.coins);
        entity.setPlayersRecruited(this.playersRecruited);
        entity.setRecruitedBy(this.recruitedBy);
        entity.setDailyReward(this.dailyReward);
        entity.setTrail(this.trail.toString());
        entity.setWarcry(this.warcry.toString());
        entity.setGore(this.gore.toString());
        entity.setDailyRewardLast(this.dailyRewardLast);
        entity.setLastOnline(this.lastOnline);
        entity.setFriends(this.friends);
        entity.setCosmetics(this.ownedCosmetics);
        this.kitPvpData.sync();
        this.essenceData.sync();
        this.playerSettings.sync();
        this.cratesData.sync();
        this.punishmentData.sync();
        entity.setKitPvpData(this.kitPvpData.getEntity());
        entity.setSettings(this.playerSettings.getEntity());
        entity.setEssences(this.essenceData.getEntity());
        entity.setCrates(this.cratesData.getEntity());
        entity.setPunishments(this.punishmentData.getEntities());
        session.saveOrUpdate(entity);
        session.getTransaction().commit();
        session.close();
    }


}
