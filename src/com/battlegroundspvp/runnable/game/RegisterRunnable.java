package com.battlegroundspvp.runnable.game;
/* Created by GamerBah on 2/11/2018 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.sql.GameProfilesEntity;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.manager.SessionManager;
import com.battlegroundspvp.util.message.MessageBuilder;
import de.Herbystar.TTA.TTA_Methods;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.hibernate.Session;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RegisterRunnable implements Runnable {

    private BattlegroundsCore plugin;
    private Player player;

    public RegisterRunnable(final BattlegroundsCore plugin, final Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        while (true) {
            if (!checkRegistration(player))
                TTA_Methods.sendTitle(player, ChatColor.YELLOW + "Create an account to play!", 30, 60, 30,
                        ChatColor.GRAY + "Use " + ChatColor.RED + "/panel register" + ChatColor.GRAY + " to sign up!", 30, 60, 30);
            else break;
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!checkRegistration(player))
                TTA_Methods.sendTitle(player, ChatColor.AQUA + "View stats, clans, and more!", 30, 60, 30,
                        ChatColor.GRAY + "Use " + ChatColor.RED + "/panel register" + ChatColor.GRAY + " to sign up!", 30, 60, 30);
            else break;
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!checkRegistration(player))
                TTA_Methods.sendTitle(player, ChatColor.LIGHT_PURPLE + "Easy access to game features!", 30, 60, 30,
                        ChatColor.GRAY + "Use " + ChatColor.RED + "/panel register" + ChatColor.GRAY + " to sign up!", 30, 60, 30);
            else break;
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Thread.currentThread().interrupt();
    }

    private boolean checkRegistration(Player player) {
        GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());
        if (gameProfile != null) {
            final ExecutorService executorService = Executors.newSingleThreadExecutor();
            Session session = SessionManager.openSession();
            session.beginTransaction();
            Future<Boolean> result = executorService.submit(() -> {
                String password = null;
                if (!session.createQuery("from GameProfilesEntity where uuid = :uuid", GameProfilesEntity.class)
                        .setParameter("uuid", player.getUniqueId().toString()).getResultList().isEmpty())
                    password = session.createQuery("from GameProfilesEntity where uuid = :uuid", GameProfilesEntity.class)
                            .setParameter("uuid", player.getUniqueId().toString()).getSingleResult().getPassword();
                boolean registered = password != null && !password.isEmpty();
                Future<Boolean> future = Bukkit.getServer().getScheduler().callSyncMethod(plugin, () -> {
                    if (registered) {
                        gameProfile.setPassword(session.createQuery("from GameProfilesEntity where uuid = :uuid", GameProfilesEntity.class)
                                .setParameter("uuid", player.getUniqueId().toString()).getSingleResult().getPassword());
                        gameProfile.setEmail(session.createQuery("from GameProfilesEntity where uuid = :uuid", GameProfilesEntity.class)
                                .setParameter("uuid", player.getUniqueId().toString()).getSingleResult().getEmail());
                        TTA_Methods.sendTitle(player, ChatColor.GREEN + "Registration Complete!", 10, 60, 20,
                                ChatColor.GRAY + "Thanks, and welcome to the community!", 10, 60, 20);
                        plugin.respawn(player);
                        player.setWalkSpeed(0.2F);
                        for (PotionEffect effect : player.getActivePotionEffects())
                            player.removePotionEffect(effect.getType());
                        player.setFoodLevel(20);
                        player.setSaturation(20);
                        player.sendMessage(" ");
                        player.sendMessage(new MessageBuilder(ChatColor.GREEN).bold().create() + "Registration complete! " + ChatColor.GRAY + "Welcome to Battlegrounds!");
                        player.sendMessage(ChatColor.AQUA + "Whenever you want to access your dashboard, either");
                        player.sendMessage(ChatColor.WHITE + "  \u00BB " + ChatColor.GRAY + "Click the player head in your inventory, or");
                        player.sendMessage(ChatColor.WHITE + "  \u00BB " + ChatColor.GRAY + "use the command: " + ChatColor.RED + "/panel view");
                        player.sendMessage(" ");
                        EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinEvent(player, new MessageBuilder(ChatColor.GOLD).bold().create() + "New! "
                                + new MessageBuilder(ChatColor.DARK_GRAY).bold().create() + "[" + new MessageBuilder(ChatColor.GREEN).bold().create() + "+"
                                + new MessageBuilder(ChatColor.DARK_GRAY).bold().create() + "] " + ChatColor.WHITE + player.getName()));
                        return true;
                    }
                    return false;
                });
                try {
                    return future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    BattlegroundsCore.getInstance().getLogger().severe("Future failed to execute or was interrupted!");
                }
                return false;
            });
            try {
                return result.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                BattlegroundsCore.getInstance().getLogger().severe("Result failed to execute or was interrupted!");
            } finally {
                session.getTransaction().commit();
                SessionManager.closeSession(session);
                SessionManager.getService().shutdown();
            }
            return true;
        }
        return false;
    }

}
