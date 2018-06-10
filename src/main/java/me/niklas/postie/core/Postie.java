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

import me.niklas.postie.command.general.AnswerCommand;
import me.niklas.postie.command.general.HelpCommand;
import me.niklas.postie.command.general.RemoveCommand;
import me.niklas.postie.command.general.VersionCommand;
import me.niklas.postie.command.random.DiceCommand;
import me.niklas.postie.command.random.RandomizeCommand;
import me.niklas.postie.command.voting.VoteCommand;
import me.niklas.postie.listener.MessageReceiveListener;
import me.niklas.postie.listener.ReactionListener;
import me.niklas.postie.manager.AnswersManager;
import me.niklas.postie.manager.CommandManager;
import me.niklas.postie.manager.ReactionManager;
import me.niklas.postie.manager.StandardsManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Niklas on 05.06.2018 in postie
 */
public class Postie {

    private static Postie instance;
    private final Logger logger = LoggerFactory.getLogger(Postie.class);
    private final CommandManager commandManager = new CommandManager();
    private final StandardsManager standardsManager = new StandardsManager();
    private final ReactionManager reactionManager = new ReactionManager();
    private final AnswersManager answersManager = new AnswersManager();
    private JDABuilder builder;

    public Postie() {
        instance = this;
        connect();
    }

    public static Postie getInstance() {
        return instance;
    }

    private void connect() {
        ConfigurationVerifier verifier = new ConfigurationVerifier();

        builder = new JDABuilder(AccountType.BOT);
        builder.setGame(Game.playing("v. " + VersionInfo.VERSION));
        builder.setToken(verifier.getConfig().get("api-token"));

        registerListeners();
        registerCommands();

        logger.info("Registered " + commandManager.getCommands().size() + " commands");
        logger.info("Configuration path: " + verifier.getConfigurationFilePath());

        try {
            builder.buildBlocking();
        } catch (LoginException e) {
            System.err.println("\nOops! Looks like you've got an invalid API token. Please insert one here:");
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String input = reader.readLine();
                verifier.getConfig().put("api-token", input);
            } catch (IOException ex) {
                logger.error("An error occurred while getting console input: ", ex);
            }
            verifier.saveConfiguration();
            logger.info("The new token has been saved. Please restart to connect to Discord.");
            System.exit(0);
        } catch (Exception e) {
            logger.error("An error occurred while running the discord bot: ", e);
        }
    }

    /**
     * Registers all commands, to keep connect() maintainable.
     */
    private void registerCommands() {
        commandManager.registerCommands(new AnswerCommand(), new VersionCommand(), new RemoveCommand(), new HelpCommand(),
                new VoteCommand(), new DiceCommand(), new RandomizeCommand());
    }

    /**
     * Registers all listeners, to keep connect() maintainable.
     */
    private void registerListeners() {
        builder.addEventListener(new MessageReceiveListener());
        builder.addEventListener(new ReactionListener());
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public StandardsManager getStandardsManager() {
        return standardsManager;
    }

    public ReactionManager getReactionManager() {
        return reactionManager;
    }

    public AnswersManager getAnswersManager() {
        return answersManager;
    }
}