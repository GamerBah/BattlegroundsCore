package com.battlegroundspvp.utils;
/* Created by GamerBah on 2/7/2018 */


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class HttpUtils {

    private static final String ADMIN = "/admin/";

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String BASE_URL = "https://dashboard.battlegroundspvp.com/";

    public static boolean get(final String addition) {
        try {
            URL url = new URL(BASE_URL + addition);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean post(final String addition, final String... params) {
        try {
            URL url = new URL(BASE_URL + addition);
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

        // PROFILE
        KITS("kits.php"),
        COSMETICS("cosmetics.php"),
        FRIENDS("friends.php"),
        CLANS("clans.php");

        private String phpExtension;
    }

}
