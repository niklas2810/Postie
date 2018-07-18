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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Niklas on 23.06.2018 in postie
 */
public class DatabaseManager {

    private final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private final DataSource source = new DataSource();
    private final String path = "data.db";

    public DatabaseManager() {
        source.setMinIdle(3);
        source.setMaxIdle(8);
        source.setUrl("jdbc:sqlite:" + path);
        source.setDriverClassName("org.sqlite.JDBC");
        logger.info("Database path: " + path);
    }

    /**
     * @return The {@link DataSource} where to retrieve a new {@link java.sql.Connection} object from.
     */
    public DataSource getSource() {
        return source;
    }

    /**
     * @return The name of the database file, be default that is "data.db".
     */
    public String getDatabaseFileName() {
        return path;
    }

    /**
     * Executes a basic SQL statement.
     *
     * @param sql    The sql statement itself.
     * @param params All params which should be replaced.
     */
    public void execute(String sql, String... params) {
        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= params.length; i++) {
                statement.setString(i, params[i - 1]);
            }
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred while performing an SQL statement", e);
        }
    }
}
