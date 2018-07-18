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

package me.niklas.postie.core;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

/**
 * Created by Niklas on 05.06.2018 in postie
 */
class ConfigurationVerifier {

    private final File file = new File("config.yml");
    private final Map<String, String> config = new HashedMap<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Yaml yaml;

    public ConfigurationVerifier() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        yaml = new Yaml(options);

        readInConfiguration();
    }

    /**
     * Reads in the config.yml file, creates a new one if necessary
     */
    private void readInConfiguration() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("An error occurred while creating the configuration file.", e);
            } finally {
                logger.info("A default configuration file has been created. (Location: " + file.getAbsolutePath() + ")");
            }
        }

        try {
            Map<?, ?> input = yaml.load(new FileInputStream(file));
            if (input == null) return;
            for (Map.Entry<?, ?> entry : input.entrySet()) {
                config.put(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (FileNotFoundException e) {
            logger.error("An error occurred while reading in the configuration. The file does not exist.", e);
        }
    }

    /**
     * @return The absolute Path of the configuration file.
     */
    public String getConfigurationFilePath() {
        return file.getAbsolutePath();
    }

    /**
     * @return whether the configuration has values stored
     */
    public boolean isConfigurationValid() {
        return config.size() > 0;
    }

    /**
     * @return The configuration in String-String pairs. As only Strings are stored, it is not necessary to store the values as objects.
     */
    public Map<String, String> getConfig() {
        return config;
    }

    /**
     * Requests a not given value from the user if already given.
     *
     * @param key     The key of the value.
     * @param request The question which should be printed to the user (for instance "What's your name?")
     */
    public void require(String key, String request) {
        if (config.containsKey(key) && config.get(key).length() > 0) {
            return;
        }
        request += !request.endsWith(":") ? ":" : "";
        boolean solved = false;
        while (!solved) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println(request);
                String input = reader.readLine();
                if (input != null && input.length() > 0) {
                    solved = true;
                    config.put(key, input);
                    saveConfiguration();
                }
            } catch (IOException e) {
                logger.error("An error occurred while getting user input: ", e);
            }
        }
    }

    /**
     * Stores all values into the config.yml file.
     */
    public void saveConfiguration() {
        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(config, writer);
        } catch (IOException e) {
            logger.error("There was an error while writing the configuration into the file: ", e);
        }
    }


}
