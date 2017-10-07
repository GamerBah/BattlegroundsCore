package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 6/18/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.sql.GameProfilesEntity;
import com.battlegroundspvp.punishments.Punishment;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
        this.punishmentData = null;//new PunishmentData(entity.getPunishments());
    }

    public Player getPlayer() {
        return BattlegroundsCore.getInstance().getServer().getPlayer(this.uuid);
    }

    public boolean isMuted(AsyncPlayerChatEvent event) {
        for (int i = 0; i < punishmentData.getPunishments().size(); i++) {
            Punishment punishment = punishmentData.getPunishments().get(i);
            if (!punishment.isPardoned()) {
                event.setCancelled(true);
                BaseComponent baseComponent = new TextComponent(ChatColor.RED + "You are muted! " + ChatColor.GRAY + "(Hover to view details)");
                baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Muted by: "
                        + ChatColor.WHITE + BattlegroundsCore.getInstance().getServer().getPlayer(punishment.getEnforcer()).getName() + "\n" + ChatColor.GRAY + "Reason: "
                        + ChatColor.WHITE + punishment.getReason().getName() + "\n" + ChatColor.GRAY + "Time Remaining: " + ChatColor.WHITE +
                        Time.toString(Time.punishmentTimeRemaining(punishment.getExpiration()), true)).create()));
                event.getPlayer().spigot().sendMessage(baseComponent);
                EventSound.playSound(event.getPlayer(), EventSound.ACTION_FAIL);
                return true;
            }
        }
        return false;
    }

    public GameProfile playSound(EventSound eventSound) {
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
        BattlegroundsCore.getInstance().getServer().getPlayer(this.uuid).spigot().respawn();
        return this;
    }

    public GameProfile sendMessage(String message) {
        BattlegroundsCore.getInstance().getServer().getPlayer(this.uuid).sendMessage(message);
        return this;
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
        //this.punishmentData.sync();
        entity.setKitPvpData(this.kitPvpData.getEntity());
        entity.setSettings(this.playerSettings.getEntity());
        entity.setEssences(this.essenceData.getEntity());
        session.merge(entity);
        session.getTransaction().commit();
        session.close();
    }


}
