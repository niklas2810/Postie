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

import me.niklas.postie.command.Command;
import me.niklas.postie.command.Instruction;
import me.niklas.postie.command.Result;
import me.niklas.postie.comparator.CommandComparator;
import me.niklas.postie.core.Postie;
import me.niklas.postie.util.Reactions;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.exceptions.PermissionException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Niklas on 06.06.2018 in postie
 */
public class CommandManager {

    private final List<Command> commands = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    /**
     * Registers a new {@link Command}
     *
     * @param commands The {@link Command}
     */
    public void registerCommands(Command... commands) {
        Collections.addAll(this.commands, commands);
        this.commands.sort(new CommandComparator());
    }

    /**
     * Unregisters a {@link Command}. It wont be usable anymore.
     *
     * @param command The {@link Command}.
     */
    public void unregisterCommand(Command command) {
        commands.remove(command);
    }

    /**
     * Checks whether the {@link Command} is stored
     *
     * @param command The {@link Command} which should be checked.
     * @return Whether the {@link Command} is stored.
     */
    public boolean hasCommand(Command command) {
        return commands.contains(command);
    }

    /**
     * Checks whether the {@link Command} is stored
     *
     * @param name The name of the {@link Command}, <b>Aliases included</b>.
     * @return Whether the {@link Command} is stored.
     */
    public boolean hasCommand(String name) {
        return getResultsFor(name).size() > 0;
    }

    /**
     * Returns all results for a specific input.
     *
     * @param command The input, this can be a {@link Command} name or an alias for it.
     * @return A {@link List} of all possible Results
     */
    private List<Command> getResultsFor(String command) {
        return commands.stream().filter(sample -> sample.getName().equalsIgnoreCase(command)
                || Arrays.stream(sample.getAliases()).anyMatch(alias -> alias.equalsIgnoreCase(command))).collect(Collectors.toList());
    }

    /**
     * Handles an {@link Instruction}, calls commands if there is a {@link Command} (or alias) stored.
     *
     * @param instruction The {@link Instruction} which should be handled, supposed to be from {@link me.niklas.postie.listener.MessageReceiveListener}
     * @return A {@link Result} which can be sent as an answer
     */
    public Result handle(Instruction instruction) {
        try {
            List<Command> results = getResultsFor(instruction.getCommand());
            if (results.size() > 0) {
                if (results.size() > 1) {
                    logger.warn("Warning: More than one result for '" + instruction.getCommand() + "'!");
                }
                Command command = results.get(0);

                if (!isCommandEnabled(instruction.getMessage().getGuild().getId(), command)) {
                    instruction.getMessage().addReaction(Reactions.SLEEPY).queue();
                    return new Result(instruction.getMessage());
                }
                int level = Postie.getInstance().getPermissionManager().getLevelOfUser(
                        instruction.getMessage().getGuild().getId(),
                        instruction.getMessage().getAuthor().getId());
                if (instruction.getMessage().getGuild().getOwner().equals(instruction.getMessage().getMember())
                        || instruction.getMessage().getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    if (level < 3) {
                        Postie.getInstance().getPermissionManager().save(instruction.getMessage().getGuild().getId(),
                                instruction.getMessage().getAuthor().getId(), 3);
                        level = 3;
                    }
                }
                if (level < command.getRequiredLevel()) {
                    return new Result(String.format("You are not permitted to use that command. (Has: %s, Required: %s)"
                            , level, command.getRequiredLevel()), instruction.getMessage());
                }
                return results.get(0).execute(instruction.getMessage(), instruction.getArgs());
            }
            return null;
        } catch (PermissionException permissionException) {
            return new Result("Lack of Permission", "I need an additional permission: `" + permissionException.getPermission().getName() + "`", instruction.getMessage());
        } catch (Exception exception) { //Always show errors to the user
            logger.error("An uncaught error occurred: ", exception);
            return new Result("Error", "An error occurred: " + exception.getClass().getSimpleName() + "(" + exception.getMessage() + ")", instruction.getMessage());
        }
    }

    /**
     * Returns information about a {@link Command} (in a printable way)
     *
     * @param command The {@link Command}
     * @return A description of the {@link Command}
     */
    public String getCommandHelp(Command command) {
        StringBuilder builder = new StringBuilder();
        builder.append("**Description**: ").append(command.getDescription()).append("\n");
        builder.append("**Required Level**: ").append(command.getRequiredLevel()).append("\n");
        if (command.getAliases().length > 0) {
            builder.append("**Aliases**: ").append(String.join(", ", command.getAliases())).append("\n");
        }
        if (command.getExamples().length > 0) {
            builder.append("**Example Usage**: `\n\n").append(String.join("\n", command.getExamples()).trim()).append("`\n");
        }
        return builder.toString().trim();
    }

    /**
     * Returns information about a {@link Command} (in a printable way)
     *
     * @param name The name of the {@link Command}
     * @return A description of the {@link Command}
     */
    public String getCommandHelp(String name) {
        Optional<Command> command = commands.stream().filter(sample -> sample.getName().equalsIgnoreCase(name)).findFirst();

        if (command.isPresent()) return getCommandHelp(command.get());
        else {
            command = commands.stream().filter(sample -> Arrays.asList(sample.getAliases()).contains(name.toLowerCase())).findFirst();
            if (command.isPresent()) return getCommandHelp(command.get());
            return "Command not found.";
        }
    }

    /**
     * @return An unmodifiable list of all {@link Command}'s.
     */
    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    /**
     * Disables a command.
     *
     * @param guildId     The ID of {@link net.dv8tion.jda.core.entities.Guild}.
     * @param commandName The name of the {@link Command}.
     */
    public void disableCommand(String guildId, String commandName) {
        JSONObject object = Postie.getInstance().getDataManager().get(guildId);
        object.put(commandName, "disabled");

        Postie.getInstance().getDataManager().save(guildId, object);
    }

    /**
     * Enables a command.
     *
     * @param guildId     The ID of {@link net.dv8tion.jda.core.entities.Guild}.
     * @param commandName The name of the {@link Command}.
     */
    public void enableCommand(String guildId, String commandName) {
        JSONObject object = Postie.getInstance().getDataManager().get(guildId);
        object.remove(commandName);

        Postie.getInstance().getDataManager().save(guildId, object);
    }

    /**
     * Checks whether a {@link Command} is enabled.
     *
     * @param guildId The ID of {@link net.dv8tion.jda.core.entities.Guild}.
     * @param command The {@link Command}.
     * @return Whether a {@link Command} is enabled.
     */
    public boolean isCommandEnabled(String guildId, Command command) {
        JSONObject object = Postie.getInstance().getDataManager().get(guildId);
        return !object.has(command.getName()) || !object.getString(command.getName()).equals("disabled");
    }
}
