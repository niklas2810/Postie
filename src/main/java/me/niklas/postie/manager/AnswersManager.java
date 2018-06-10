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

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niklas on 09.06.2018 in postie
 */
public class AnswersManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String path = System.getProperty("user.dir") + File.separator + "data.db";
    private final Map<String, Map<String, String>> answers = new HashMap<>();
    private final DataSource source = new DataSource();


    public AnswersManager() {
        source.setMinIdle(3);
        source.setMaxIdle(8);
        source.setUrl("jdbc:sqlite:\\" + path);
        source.setDriverClassName("org.sqlite.JDBC");
        logger.info("Database path: " + path);

        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS io(guildId TEXT NOT NULL, input TEXT NOT NULL, answer TEXT NOT NULL)")) {
            statement.execute();
            statement.close();
            logger.info("Established a connection to the database file!");
        } catch (SQLException e) {
            logger.warn("Was not able to establish a connection to the database: ", e);
        }
        logger.debug("Got data from " + answers.size() + " guilds!");
        retrieveFromDatabase();
    }

    /**
     * Searches the database table for all input-output pairs stored.
     *
     * @param guildId The id of the {@link net.dv8tion.jda.core.entities.Guild}.
     * @return A {@link Map}, including input-answers.
     */
    public Map<String, String> getAnswersForGuild(String guildId) {
        if (answers.containsKey(guildId)) return answers.get(guildId);
        return new HashMap<>();
    }

    /**
     * Returns whether an answer is stored for a specific input.
     *
     * @param guildId The id of the {@link net.dv8tion.jda.core.entities.Guild}.
     * @param input   The input string, for normal entered by the user.
     * @return Whether an answer is stored for that input.
     */
    public boolean hasAnswerFor(String guildId, String input) {
        input = input.replaceAll("[^A-Za-z0-9]", "").trim().toLowerCase();
        return answers.containsKey(guildId) && answers.get(guildId).containsKey(input);
    }

    /**
     * Returns the answer for a specific input
     *
     * @param guildId The id of the {@link net.dv8tion.jda.core.entities.Guild}.
     * @param input   The input string, for normal entered by the user.
     * @return The answer which can be returned.
     */
    public String getAnswerForInput(String guildId, String input) {
        if (!hasAnswerFor(guildId, input)) return "";
        input = input.replaceAll("[^A-Za-z0-9]", "").trim().toLowerCase();
        return answers.get(guildId).get(input);
    }

    /**
     * Saves something into the database.
     *
     * @param guildId The id of the {@link net.dv8tion.jda.core.entities.Guild}.
     * @param input   The input string, for normal entered by the user.
     * @param answer  The answer which can be returned.
     */
    public void save(String guildId, String input, String answer) {
        if (!answers.containsKey(guildId)) answers.put(guildId, new HashMap<>());
        input = input.split("//")[0].replaceAll("[^A-Za-z0-9]", "").trim().toLowerCase();

        if (answers.get(guildId).containsKey(input)) { //Remove old entry if present
            remove(guildId, input);
        }

        answers.get(guildId).put(input, answer);
        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT into io(guildId, input, answer) VALUES(?,?,?)")) {
            statement.setString(1, guildId);
            statement.setString(2, input);
            statement.setString(3, answer);
            statement.execute();
        } catch (SQLException e) {
            logger.warn("Was not able to establish a connection to the database: ", e);
        }
    }

    /**
     * Removes something from the database.
     *
     * @param guildId The id of the {@link net.dv8tion.jda.core.entities.Guild}.
     * @param input   The input string, for normal entered by the user.
     */
    public void remove(String guildId, String input) {
        if (!answers.containsKey(guildId)) answers.put(guildId, new HashMap<>());

        answers.get(guildId).remove(input);
        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM io WHERE guildId = ? AND input = ?")) {
            statement.setString(1, guildId);
            statement.setString(2, input);
            statement.execute();
        } catch (SQLException e) {
            logger.warn("Was not able to establish a connection to the database: ", e);
        }
    }

    /**
     * Removes a guild from the database. WARNING: All data will be deleted!
     *
     * @param guildId The id of the {@link net.dv8tion.jda.core.entities.Guild}.
     */
    public void removeGuild(String guildId) {
        answers.remove(guildId);
        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM io WHERE guildId = ?")) {
            statement.setString(1, guildId);
            statement.execute();
        } catch (SQLException e) {
            logger.warn("Was not able to establish a connection to the database: ", e);
        }
    }

    /**
     * Retrieves all values from the table and stores them into the "answers" variable.
     */
    private void retrieveFromDatabase() {
        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM io")) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String guildId = result.getString("guildId");
                String input = result.getString("input");
                String answer = result.getString("answer");
                if (!answers.containsKey(guildId)) answers.put(guildId, new HashMap<>());

                answers.get(guildId).put(input, answer);
            }
            result.close();
        } catch (SQLException e) {
            logger.warn("Was not able to establish a connection to the database: ", e);
        }
    }
}
