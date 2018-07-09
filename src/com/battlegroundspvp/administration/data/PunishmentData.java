package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 9/30/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.sql.GameProfilesEntity;
import com.battlegroundspvp.administration.data.sql.PunishmentsEntity;
import com.battlegroundspvp.punishment.Punishment;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.hibernate.Session;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;

public class PunishmentData {

    @Getter
    private final int id;
    @Getter
    private ArrayList<Punishment> punishments;
    private ArrayList<Punishment> removed = new ArrayList<>();
    @Getter
    private Set<PunishmentsEntity> entities;
    @Getter
    private GameProfilesEntity gameProfilesEntity;

    PunishmentData(Set<PunishmentsEntity> list, GameProfilesEntity gameProfilesEntity) {
        this.id = gameProfilesEntity.getId();
        ArrayList<Punishment> punishments = new ArrayList<>();
        if (list != null) {
            for (PunishmentsEntity entity : list) {
                punishments.add(new Punishment(entity.getPunishmentId(),
                        Punishment.Type.valueOf(entity.getType()),
                        entity.getDate(),
                        entity.getDuration(),
                        entity.getExpiration(),
                        entity.getEnforcerId(),
                        Punishment.Reason.valueOf(entity.getReason()),
                        entity.isPardoned()));
            }
        }
        this.punishments = punishments;
        this.entities = list;
        this.gameProfilesEntity = gameProfilesEntity;
    }

    public void sync(Session session) {
        for (Punishment punishment : removed) {
            this.entities.forEach(entity -> {
                if (entity.getPunishmentId() == punishment.getId()) {
                    this.entities.remove(entity);
                    this.gameProfilesEntity.getPunishments().remove(entity);
                    entity.setGameProfilesEntity(null);
                    session.delete(entity);
                }
            });
        }
        for (Punishment punishment : punishments) {
            boolean registered = false;
            for (PunishmentsEntity entity : this.entities) {
                if (entity.getPunishmentId() == punishment.getId()) {
                    entity.setDate(punishment.getDate());
                    entity.setDuration(punishment.getDuration());
                    entity.setEnforcerId(punishment.getEnforcerId());
                    entity.setExpiration(punishment.getExpiration());
                    entity.setPardoned(punishment.isPardoned());
                    entity.setReason(punishment.getReason().toString());
                    entity.setType(punishment.getType().toString());
                    session.merge(entity);
                    registered = true;
                }
            }
            if (!registered) {
                PunishmentsEntity entity = new PunishmentsEntity();
                entity.setDate(punishment.getDate());
                entity.setDuration(punishment.getDuration());
                entity.setEnforcerId(punishment.getEnforcerId());
                entity.setExpiration(punishment.getExpiration());
                entity.setPardoned(punishment.isPardoned());
                entity.setReason(punishment.getReason().toString());
                entity.setType(punishment.getType().toString());
                entity.setGameProfilesEntity(this.gameProfilesEntity);
                this.gameProfilesEntity.getPunishments().add(entity);
                session.merge(entity);
            }
        }
    }

    public void delete(Player player, GameProfile targetProfile, Punishment punishment) {
        if (punishments.contains(punishment)) {
            this.punishments.remove(punishment);
            this.removed.add(punishment);
        }
        GameProfile enforcerProfile = GameProfileManager.getGameProfile(punishment.getEnforcerId());
        if (enforcerProfile != null) {
            BaseComponent baseComponent = new TextComponent(ChatColor.RED + player.getName() + " deleted a " + punishment.getType().getName() + " from " + targetProfile.getName());
            baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Punished by: "
                    + enforcerProfile.getRank().getColor().create() + ""
                    + ChatColor.BOLD + enforcerProfile.getRank().getName().toUpperCase()
                    + ChatColor.WHITE + " " + enforcerProfile.getName() + "\n" + ChatColor.GRAY + "Reason: "
                    + ChatColor.GOLD + punishment.getReason().getName() + "\n"
                    + ChatColor.GRAY + "Date: " + ChatColor.AQUA + punishment.getDate().minusHours(9).format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(PST)'"))).create()));

            BattlegroundsCore.getInstance().getServer().getOnlinePlayers().stream().filter(staff ->
                    GameProfileManager.getGameProfile(staff.getUniqueId()).hasRank(Rank.HELPER)).forEach(staff ->
                    staff.spigot().sendMessage(baseComponent));
        }
    }

    public ArrayList<Punishment> getMutes() {
        ArrayList<Punishment> mutes = new ArrayList<>();
        if (this.punishments != null)
            for (Punishment punishment : this.punishments)
                if (punishment.getType().equals(Punishment.Type.MUTE))
                    mutes.add(punishment);
        return mutes;
    }

    public ArrayList<Punishment> getKicks() {
        ArrayList<Punishment> kicks = new ArrayList<>();
        if (this.punishments != null)
            for (Punishment punishment : this.punishments)
                if (punishment.getType().equals(Punishment.Type.KICK))
                    kicks.add(punishment);
        return kicks;
    }

    public ArrayList<Punishment> getTempBans() {
        ArrayList<Punishment> tempBans = new ArrayList<>();
        if (this.punishments != null)
            for (Punishment punishment : this.punishments)
                if (punishment.getType().equals(Punishment.Type.TEMP_BAN))
                    tempBans.add(punishment);
        return tempBans;
    }

    public ArrayList<Punishment> getBans() {
        ArrayList<Punishment> bans = new ArrayList<>();
        if (this.punishments != null)
            for (Punishment punishment : this.punishments)
                if (punishment.getType().equals(Punishment.Type.BAN))
                    bans.add(punishment);
        return bans;
    }

}
