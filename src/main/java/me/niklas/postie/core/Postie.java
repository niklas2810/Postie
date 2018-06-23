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

import me.niklas.postie.command.general.*;
import me.niklas.postie.command.permissions.DefaultLevelCommand;
import me.niklas.postie.command.permissions.LevelCommand;
import me.niklas.postie.command.permissions.SetlevelCommand;
import me.niklas.postie.command.random.DiceCommand;
import me.niklas.postie.command.random.RandomizeCommand;
import me.niklas.postie.command.voting.VoteCommand;
import me.niklas.postie.listener.MessageReceiveListener;
import me.niklas.postie.listener.ReactionListener;
import me.niklas.postie.listener.ReadyListener;
import me.niklas.postie.manager.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
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

    //Most basic managers
    private final CommandManager commandManager = new CommandManager();
    private final StandardsManager standardsManager = new StandardsManager();
    private final DatabaseManager databaseManager = new DatabaseManager();
    private final ReactionManager reactionManager = new ReactionManager();

    //More advanced managers, depending on the above
    private final AnswersManager answersManager;
    private final PermissionManager permissionManager;

    private JDABuilder builder;

    public Postie() {
        instance = this;

        answersManager = new AnswersManager();
        permissionManager = new PermissionManager();

        connect();
    }

    public static Postie getInstance() {
        return instance;
    }

    private void connect() {
        ConfigurationVerifier verifier = new ConfigurationVerifier();

        builder = new JDABuilder(AccountType.BOT);
        builder.setAutoReconnect(true);
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
     * Performs a reload, reads in all data again.
     */
    public void performReload() {
        permissionManager.retrieveFromDatabase();
        answersManager.retrieveFromDatabase();
    }

    /**
     * Registers all commands, to keep connect() maintainable.
     */
    private void registerCommands() {
        commandManager.registerCommands(new AnswerCommand(), new InviteCommand(), new VersionCommand(), new ReloadCommand(), new RemoveCommand(), new HelpCommand(),
                new VoteCommand(), new DiceCommand(), new RandomizeCommand(), new DefaultLevelCommand(), new SetlevelCommand(), new LevelCommand());
    }

    /**
     * Registers all listeners, to keep connect() maintainable.
     */
    private void registerListeners() {
        builder.addEventListener(new ReadyListener());
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

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    /**
     * Returns an invite link which can be used to add the bot to a guild.
     *
     * @param jda The {@link JDA} instance.
     * @return An invite link.
     */
    public String getInviteLink(JDA jda) {
        return String.format("https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot", jda.getSelfUser().getId());
    }
}
