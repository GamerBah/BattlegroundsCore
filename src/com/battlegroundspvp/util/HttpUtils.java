package com.battlegroundspvp.util;
/* Created by GamerBah on 2/7/2018 */


import com.battlegroundspvp.BattlegroundsCore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HttpUtils {

    private static final String ADMIN = "/admin/";

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String BASE_URL = "https://dashboard.battlegroundspvp.com/";

    /**
     * Creates a HTTP GET request from the {@value BASE_URL} with the appended extension
     * Runs through an {@code ExecutorService} to prevent running I/O on the main thread
     *
     * @param extension the extension to give the {@value BASE_URL}
     * @return a new instance of StreamManager
     * @see StreamManager
     */
    public static StreamManager get(final String extension) {
        Future<StreamManager> future = BattlegroundsCore.executorService.submit(() -> {
            try {
                URL url = new URL(BASE_URL + extension);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", USER_AGENT);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                    return new StreamManager(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            BattlegroundsCore.getInstance().getLogger().severe("Unable to get the requested page!");
            return new StreamManager(null);
        });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            BattlegroundsCore.getInstance().getLogger().severe("Future failed to execute or was interrupted!");
        }
        return new StreamManager(null);
    }

    public static StreamManager get(final PanelExtension extension) {
        return get(extension.getPhpExtension());
    }

    public static StreamManager get() {
        return get("");
    }

    /**
     * Creates an HTTP POST request with the given parameters to the {@value BASE_URL}
     * with the given extension appended to the end of it
     * Runs through an {@code ExecutorService} to prevent running I/O on the main thread
     *
     * @param extension the extension to give the {@value BASE_URL}
     * @param params    the tags to apply to the address
     * @return whether response code was OK or not
     * @deprecated since POST requests aren't needed
     */
    @Deprecated
    public static boolean post(final String extension, final String[] params) {
        Future<Boolean> future = BattlegroundsCore.executorService.submit(() -> {
            try {
                URL url = new URL(BASE_URL + extension);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setDoOutput(true);
                OutputStream stream = connection.getOutputStream();
                stream.write(StringUtils.join(Arrays.asList(params).iterator(), '&').getBytes());
                stream.flush();
                stream.close();
                return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
            } catch (IOException e) {
                e.printStackTrace();
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
    }

    @Deprecated
    public static boolean post(final PanelExtension extension, final String... params) {
        return post(extension.getPhpExtension(), params);
    }

    @Deprecated
    public static boolean post(final String... params) {
        return post("", params);
    }

    /**
     * StreamManager class for handling InputStreams in a clean manner.
     * Takes an InputStream, and puts that through a StringWriter
     * for easy management.
     */
    public static class StreamManager {

        @Getter
        private final InputStream stream;

        StreamManager(InputStream stream) {
            this.stream = stream;
        }

        /**
         * Reads through the InputStream using a StringWriter
         * without any post-read actions preformed.
         *
         * @return a string of the output of the InputStream
         * @throws NullPointerException if the InputStream instance is null
         */
        public String read() throws NullPointerException {
            try {
                StringWriter writer = new StringWriter();
                try {
                    IOUtils.copy(stream, writer, Charset.defaultCharset());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return writer.toString();
            } catch (NullPointerException e) {
                return "null";
            }
        }

        public void sendToPlayer(final Player player) {
            if (player.isOnline())
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.read()));
        }

        public void sendToPlayers(final Collection<Player> players) {
            players.forEach(this::sendToPlayer);
        }

        public String get(final String key) {
            List<String> entries = Arrays.asList(read().split("\n"));
            HashMap<String, String> map = new HashMap<>();
            entries.forEach(entry -> {
                String[] split = entry.split("=");
                map.put(split[0], ChatColor.translateAlternateColorCodes('&', split[1].replace("\"", "")
                        .replaceAll("\\r", "").replaceAll("\\n", "")));
            });
            return map.getOrDefault(key, "null");
        }

    }

    @AllArgsConstructor
    @Getter
    public enum PanelExtension {
        // ADMIN
        PLAYERS(ADMIN + "players.php"),
        DONATIONS(ADMIN + "donations.php"),
        PUNISHMENTS(ADMIN + "punishments.php"),
        ECONOMY(ADMIN + "economy.php"),
        BUG_REPORTS(ADMIN + "bugreports.php"),
        PLAYER_REPORTS(ADMIN + "playerreports.php"),
        CONFIGURATION(ADMIN + "configuration.php"),
        CFG_EDIT(ADMIN + "cfg/server_cfg.txt"),

        // PROFILE
        KITS("kits.php"),
        COSMETICS("cosmetics.php"),
        FRIENDS("friends.php"),
        CLANS("clans.php");

        private String phpExtension;
    }

}
