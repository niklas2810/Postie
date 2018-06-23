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
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Niklas on 23.06.2018 in postie
 */
public class DataManager {

    private final Logger logger = LoggerFactory.getLogger(PermissionManager.class);
    private final DatabaseManager database;

    private final Map<String, JSONObject> storage;

    public DataManager() {
        storage = ExpiringMap.builder().maxSize(100).expiration(5, TimeUnit.MINUTES)
                .expirationPolicy(ExpirationPolicy.ACCESSED).build();
        database = Postie.getInstance().getDatabaseManager();

        database.execute("CREATE TABLE IF NOT EXISTS data(identifier TEXT NOT NULL, context TEXT NOT NULL)");
    }

    /**
     * Saves a {@link JSONObject} in the database.
     *
     * @param identifier The unique ID (e.g. a discord guild/user id)
     * @param context    A JSON object which should be stored
     */
    public void save(String identifier, String context) {
        save(identifier, new JSONObject(context));
    }

    /**
     * Saves a {@link JSONObject} in the database.
     *
     * @param identifier The unique ID (e.g. a discord guild/user id)
     * @param context    A JSON object which should be stored
     */
    public void save(String identifier, JSONObject context) {
        if (has(identifier)) {
            database.execute("UPDATE data SET context = ? WHERE identifier = ?", context.toString(), identifier);
        } else {
            database.execute("INSERT INTO data(identifier, context) VALUES(?,?)", identifier, context.toString());
        }
        storage.put(identifier, context);
    }

    /**
     * Removes something from the database.
     *
     * @param identifier The unique ID (e.g. a discord guild/user id).
     */
    public void remove(String identifier) {
        database.execute("DELETE FROM data WHERE identifier = ?", identifier);
        storage.remove(identifier);
    }

    /**
     * Check whether the database table has a specific value stored.
     *
     * @param identifier The unique ID (e.g. a discord guild/user id).
     * @return Whether the database table has a specific value stored.
     */
    public boolean has(String identifier) {
        try (Connection connection = database.getSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM data WHERE identifier = ?")) {
            statement.setString(1, identifier);
            ResultSet set = statement.executeQuery();
            return set.next();
        } catch (SQLException e) {
            logger.error("An error occurred while interacting with the database: ", e);
            return false;
        }
    }

    /**
     * Reads in something from the storage.
     *
     * @param identifier The unique ID (e.g. a discord guild/user id).
     * @return The data as a {@link JSONObject}.
     */
    public JSONObject get(String identifier) {
        if (!storage.containsKey(identifier)) storage.put(identifier, fetchFromDatabase(identifier));
        return storage.get(identifier);
    }

    /**
     * Reads in something from the database.
     *
     * @param identifier The unique ID (e.g. a discord guild/user id).
     * @return The data as a {@link JSONObject}.
     */
    private JSONObject fetchFromDatabase(String identifier) {
        try (Connection connection = database.getSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM data WHERE identifier = ?")) {
            statement.setString(1, identifier);
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                String raw = set.getString("context").trim();
                if (!raw.startsWith("{") || !raw.endsWith("}")) {
                    logger.warn("Inappropriate JSON data");
                    return new JSONObject();
                }
                return new JSONObject(raw);
            }
        } catch (SQLException e) {
            logger.error("An error occurred while interacting with the database: ", e);
        }
        return new JSONObject();
    }

    /**
     * Clears the chache of the object, everything will have to be fetched from the database again.
     */
    public void clearCache() {
        storage.clear();
    }
}
