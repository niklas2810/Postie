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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Niklas on 05.06.2018 in postie
 */
public class Startup {

    private static final Logger logger = LoggerFactory.getLogger(Startup.class);

    public static void main(String[] args) {
        ConfigurationVerifier verifier = new ConfigurationVerifier();
        verifier.require("api-token", "Please enter your API-Token from https://discordapp.com/developers/applications/me !");

        if (!verifier.isConfigurationValid()) {
            LoggerFactory.getLogger(Startup.class).info("Please verify that your config.yml file is valid. If that is not in case, please delete it.");
            System.exit(1);
        }

        logger.info("Version: " + VersionInfo.VERSION);
        try {
            new Postie();
        } catch (Exception e) {
            logger.error("An unknown error occurred which was not caught by the bots instance: ", e);
        }
    }
}
