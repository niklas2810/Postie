/*
 *
 *      Copyright 2018 Niklas Arndt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package me.niklas.postie.manager;

import me.niklas.postie.core.Postie;
import me.niklas.postie.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niklas on 23.06.2018 in postie
 */
public class PermissionManager {

    private final Logger logger = LoggerFactory.getLogger(PermissionManager.class);
    private final DatabaseManager database;

    private final Map<String, Map<String, String>> levels = new HashMap<>(); //Guilds -> Members + Levels

    public PermissionManager() {
        database = Postie.getInstance().getDatabaseManager();

        database.execute("CREATE TABLE IF NOT EXISTS permissions(guildId TEXT NOT NULL, userId TEXT NOT NULL, level INT NOT NULL)");

        retrieveFromDatabase();
    }

    /**
     * Stores something in the database.
     *
     * @param guildId The ID of the {@link net.dv8tion.jda.core.entities.Guild}.
     * @param userId  The ID of the {@link net.dv8tion.jda.core.entities.Member}.
     * @param level   The desired permission level.
     */
    public void save(String guildId, String userId, int level) {
        if (!levels.containsKey(guildId)) levels.put(guildId, new HashMap<>());

        if (levels.get(guildId).containsKey(userId)) {
            if (levels.get(guildId).get(userId).equals(level + "")) return;
            levels.get(guildId).put(userId, level + "");
            database.execute("UPDATE permissions SET level = ? WHERE guildId = ? AND userId = ?", level + "", guildId, userId);
        } else {
            levels.get(guildId).put(userId, level + "");
            database.execute("INSERT INTO permissions(guildId, userId, level) VALUES(?,?,?)", guildId, userId, level + "");
        }
    }

    /**
     * Removes a specific level from the data set.
     *
     * @param guildId The ID of the {@link net.dv8tion.jda.core.entities.Guild}.
     * @param userId  The ID of the {@link net.dv8tion.jda.core.entities.Member}.
     */
    public void remove(String guildId, String userId) {
        if (!levels.containsKey(guildId) || !levels.get(guildId).containsKey(userId)) return;
        levels.get(guildId).remove(userId);
        database.execute("DELETE FROM permissions WHERE guildId = ? AND userId = ?", guildId, userId);
    }

    /**
     * Checks whether a value is stored.
     *
     * @param guildId The ID of the {@link net.dv8tion.jda.core.entities.Guild}.
     * @param userId  The ID of the {@link net.dv8tion.jda.core.entities.Member}.
     * @return Whether a value is stored.
     */
    public boolean hasUser(String guildId, String userId) {
        return levels.containsKey(guildId) && levels.get(guildId).containsKey(userId);
    }

    /**
     * @param guildId The ID of the {@link net.dv8tion.jda.core.entities.Guild}.
     * @param userId  The ID of the {@link net.dv8tion.jda.core.entities.Member}.
     * @return The level of the {@link net.dv8tion.jda.core.entities.Member}.
     */
    public int getLevelOfUser(String guildId, String userId) {
        if (!levels.containsKey(guildId)) return 1;
        if (!levels.get(guildId).containsKey(userId))
            return getDefaultGuildLevel(guildId); //Nothing special stored for the user
        String level = levels.get(guildId).get(userId);
        if (!Util.isInteger(level)) return 1;
        return Integer.valueOf(level);
    }

    /**
     * @param guildId The ID of the {@link net.dv8tion.jda.core.entities.Guild}.
     * @return The default permission level for a {@link net.dv8tion.jda.core.entities.Guild}.
     */
    public int getDefaultGuildLevel(String guildId) {
        if (!hasUser(guildId, "default")) return 1;
        String level = levels.get(guildId).get("default");
        if (!Util.isInteger(level)) return 1;
        return Integer.valueOf(level);
    }

    /**
     * Removes a {@link net.dv8tion.jda.core.entities.User}s permission levels completely (from all {@link net.dv8tion.jda.core.entities.Guild}s).
     *
     * @param userId The ID of the {@link net.dv8tion.jda.core.entities.User}.
     */
    public void removeUserLevels(String userId) {
        levels.forEach((id, map) -> map.remove(userId));
        database.execute("DELETE FROM permissions WHERE userId = ?", userId);
    }

    /**
     * Removes a {@link net.dv8tion.jda.core.entities.Guild} completely from the database
     *
     * @param guildId The ID of the {@link net.dv8tion.jda.core.entities.Guild}.
     */
    public void removeGuild(String guildId) {
        levels.remove(guildId);
        database.execute("DELETE FROM permissions WHERE guildId = ?", guildId);
    }

    /**
     * Retrieves all values from the table and stores them into the "levels" map.
     */
    public void retrieveFromDatabase() {
        levels.clear();


        try (Connection connection = database.getSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM permissions")) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String guildId = result.getString("guildId");
                String userId = result.getString("userId");
                String level = result.getString("level");
                if (!levels.containsKey(guildId)) levels.put(guildId, new HashMap<>());
                levels.get(guildId).put(userId, level);
            }
            result.close();
        } catch (SQLException e) {
            logger.warn("Was not able to establish a connection to the database: ", e);
        }
    }
}
