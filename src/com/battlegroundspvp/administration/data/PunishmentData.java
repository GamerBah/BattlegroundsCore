package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 9/30/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.sql.GameProfilesEntity;
import com.battlegroundspvp.administration.data.sql.PunishmentsEntity;
import com.battlegroundspvp.punishments.Punishment;
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
import java.util.List;

public class PunishmentData {

    @Getter
    private final int id;
    @Getter
    private ArrayList<Punishment> punishments;
    private ArrayList<Punishment> removed = new ArrayList<>();
    @Getter
    private List<PunishmentsEntity> entities;
    @Getter
    private GameProfilesEntity gameProfilesEntity;

    public PunishmentData(List<PunishmentsEntity> list, GameProfilesEntity gameProfilesEntity) {
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

    public void sync() {
        Session session = BattlegroundsCore.getSessionFactory().openSession();
        session.beginTransaction();
        for (Punishment punishment : removed) {
            for (int i = 0; i < this.entities.size(); i++) {
                PunishmentsEntity entity = this.entities.get(i);
                if (entity.getPunishmentId() == punishment.getId()) {
                    this.entities.remove(i);
                    this.gameProfilesEntity.getPunishments().remove(entity);
                    entity.setGameProfilesEntity(null);
                    session.delete(entity);
                }
            }
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
                punishment.setId(entity.getPunishmentId());
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
        session.getTransaction().commit();
        session.close();
    }

    public void delete(Player player, GameProfile targetProfile, Punishment punishment) {
        if (punishments.contains(punishment)) {
            this.punishments.remove(punishment);
            this.removed.add(punishment);
        }
        BaseComponent baseComponent = new TextComponent(ChatColor.RED + player.getName() + " deleted a " + punishment.getType().getName() + " from " + targetProfile.getName());
        baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Punished by: "
                + BattlegroundsCore.getInstance().getGameProfile(punishment.getEnforcerId()).getRank().getColor().create() + ""
                + ChatColor.BOLD + BattlegroundsCore.getInstance().getGameProfile(punishment.getEnforcerId()).getRank().getName().toUpperCase()
                + ChatColor.WHITE + " " + BattlegroundsCore.getInstance().getGameProfile(punishment.getEnforcerId()).getName() + "\n" + ChatColor.GRAY + "Reason: "
                + ChatColor.GOLD + punishment.getReason().getName() + "\n"
                + ChatColor.GRAY + "Date: " + ChatColor.AQUA + punishment.getDate().minusHours(9).format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(PST)'"))).create()));

        BattlegroundsCore.getInstance().getServer().getOnlinePlayers().stream().filter(staff ->
                BattlegroundsCore.getInstance().getGameProfile(staff.getUniqueId()).hasRank(Rank.HELPER)).forEach(staff ->
                staff.spigot().sendMessage(baseComponent));
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
