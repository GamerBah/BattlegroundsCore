package com.battlegroundspvp.administration.data.old;
/* Created by GamerBah on 7/6/2016 */

public class MySQL {

    /*private static HikariDataSource dataSource = null;
    private Core plugin;

    public MySQL(Core plugin) {
        this.plugin = plugin;
        String host = plugin.getConfig().getString("host");
        String db = plugin.getConfig().getString("database");
        String user = plugin.getConfig().getString("username");
        String pass = plugin.getConfig().getString("password");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + "/" + db + "?useSSL=false");
        config.setUsername(user);
        config.setPassword(pass);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setLeakDetectionThreshold(60000);
        config.setMaximumPoolSize(100);
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("useLocalTransactionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        try {
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().severe("Unable to start HikariCP data source");
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close MySQL connection!");
        }
    }

    public void disconnect() {
        try {
            dataSource.getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataSource.close();
    }

    public ArrayList<GameProfile> getAllGameProfiles() {
        try (Connection connection = dataSource.getConnection()) {
            try (ResultSet result = executeQuery(connection, Query.GET_ALL_PLAYER_DATA)) {
                ArrayList<GameProfile> gameProfiles = new ArrayList<>();
                if (result != null) {
                    while (result.next()) {
                        GameProfile gameProfile = new GameProfile(result.getString("name"), UUID.fromString(result.getString("uuid")));
                        gameProfile.setRank(Rank.valueOf(result.getString("rank")));
                        gameProfile.setCoins(result.getInt("coins"));
                        gameProfile.setPlayersRecruited(result.getInt("playersRecruited"));
                        gameProfile.setRecruitedBy(result.getString("recruitedBy"));
                        gameProfile.setDailyReward(result.getBoolean("dailyReward"));
                        gameProfile.setTrail(Cosmetic.Item.valueOf(result.getString("trail")));
                        gameProfile.setGore(Cosmetic.Item.valueOf(result.getString("gore")));
                        gameProfile.setWarcry(Cosmetic.Item.valueOf(result.getString("warcry")));
                        gameProfile.setDailyRewardLast(LocalDateTime.parse(result.getString("dailyRewardLast")));
                        gameProfile.setLastOnline(LocalDateTime.parse(result.getString("lastOnline")));
                        gameProfile.setFriends(result.getString("friends"));
                        gameProfile.setOwnedCosmetics(result.getString("cosmetics"));
                        gameProfile.setKitPvpData(getKitPvpData(gameProfile));
                        gameProfile.setPlayerSettings(getPlayerSettings(gameProfile));
                        gameProfile.setEssenceData(getEssenceData(gameProfile));

                        gameProfiles.add(gameProfile);
                    }
                    result.getStatement().close();
                    closeConnection(connection);
                    return gameProfiles;
                }
                closeConnection(connection);
            } catch (SQLException e) {
                plugin.getLogger().severe("Uh oh! Unable to get all player data");
                e.printStackTrace();
                closeConnection(connection);
            }
            closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Punishment getPunishment(UUID uuid, Punishment.Type type, LocalDateTime date) {
        try {
            Connection connection = dataSource.getConnection();
            try (ResultSet result = executeQuery(connection, Query.GET_PUNISHMENT, uuid.toString(), type.toString(), date.toString())) {
                if (result != null) {
                    if (result.next()) {
                        Punishment punishment = new Punishment(result.getInt("id"), UUID.fromString(result.getString("uuid")), result.getString("name"), Punishment.Type.valueOf(result.getString("type")),
                                LocalDateTime.parse(result.getString("date")), result.getInt("duration"), LocalDateTime.parse(result.getString("expiration")), UUID.fromString(result.getString("enforcer")),
                                Punishment.Reason.valueOf(result.getString("reason")), result.getBoolean("pardoned"));
                        result.getStatement().close();
                        closeConnection(connection);
                        return punishment;
                    }
                    result.getStatement().close();
                }
                closeConnection(connection);
            } catch (SQLException e) {
                plugin.getLogger().severe("Uh oh! Unable to get the punishment!");
                e.printStackTrace();
            }
            closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Punishment> getAllPunishments() {
        try {
            Connection connection = dataSource.getConnection();
            try (ResultSet result = executeQuery(connection, Query.GET_PUNISHMENTS)) {
                ArrayList<Punishment> punishments = new ArrayList<>();
                if (result != null) {
                    while (result.next()) {
                        punishments.add(new Punishment(result.getInt("id"), UUID.fromString(result.getString("uuid")), result.getString("name"), Punishment.Type.valueOf(result.getString("type")),
                                LocalDateTime.parse(result.getString("date")), result.getInt("duration"), LocalDateTime.parse(result.getString("expiration")), UUID.fromString(result.getString("enforcer")),
                                Punishment.Reason.valueOf(result.getString("reason")), result.getBoolean("pardoned")));
                    }
                    result.getStatement().close();
                    closeConnection(connection);
                    return punishments;
                }
                closeConnection(connection);
            } catch (SQLException e) {
                plugin.getLogger().severe("Uh oh! Unable to get punishments!");
                e.printStackTrace();
            }
            closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Executes a MySQL query (NOT ASYNCHRONOUS)
     *
     * @param connection Connection to use
     * @param query      MySQL query to execute
     * @return Result of MySQL query
     */
    /*private ResultSet executeQuery(Connection connection, Query query, Object... parameters) {
        if (connection == null) {
            plugin.getLogger().severe("Could not execute MySQL query: Connection is null");
            return null;
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            int i = 1;
            for (Object parameter : parameters) {
                preparedStatement.setObject(i++, parameter);
            }

            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not execute MySQL query: " + e.getMessage());
            closeConnection(connection);
            return null;
        }
    }*/
}
