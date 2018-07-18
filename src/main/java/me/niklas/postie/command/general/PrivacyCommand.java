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

package me.niklas.postie.command.general;

import me.niklas.postie.command.Command;
import me.niklas.postie.command.Result;
import me.niklas.postie.core.Postie;
import me.niklas.postie.util.DataCollector;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;

/**
 * Created by Niklas on 15.07.2018 in postie
 */
public class PrivacyCommand implements Command {
    @Override
    public String getName() {
        return "privacy";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "All commands concerning privacy.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"privacy request me", "privacy delete me", "privacy delete guild"};
    }

    @Override
    public int getRequiredLevel() {
        return 0;
    }

    @Override
    public Result execute(Message message, String[] args) {
        if (args.length == 2) {
            String userId = message.getAuthor().getId();
            String guildId = message.getGuild().getId();
            if (args[0].equalsIgnoreCase("request")) {
                if (args[1].equalsIgnoreCase("me")) {
                    DataCollector collector = new DataCollector(userId);
                    collector.create();
                    PrivateChannel userChannel = message.getAuthor().openPrivateChannel().complete();
                    userChannel.sendMessage("Here is all of your data stored: Unzip the file and inspect the files (it's in JSON format). " +
                            "Use the command `privacy delete me` to delete all of the data.").queue();
                    userChannel.sendFile(collector.getZipFile()).queue(result -> collector.remove());
                    userChannel.close().queue();
                    return new Result("Check out your DM's! " + message.getAuthor().getAsMention(), message);
                }
            }
            if (args[0].equalsIgnoreCase("delete")) {
                if (args[1].equalsIgnoreCase("me")) {
                    Postie.getInstance().getPermissionManager().removeUserLevels(userId);
                    Postie.getInstance().getDataManager().remove(userId);
                    return new Result("All of your data has been removed from the database. Please be aware of that some data might be recreated " +
                            "when you receive a permission level or use the bot.", message);
                }
                if (args[1].equalsIgnoreCase("guild")) {
                    if (Postie.getInstance().getPermissionManager().getLevelOfUser(guildId, userId) < 3) {
                        return new Result("Lack of Permission", "You are not permitted to do that!", message);
                    }
                    Postie.getInstance().getPermissionManager().removeGuild(guildId);
                    Postie.getInstance().getDataManager().remove(guildId);
                    Postie.getInstance().getAnswersManager().removeGuild(guildId);
                    return new Result("The data of this guild has been removed from the database. " +
                            "If the bot is used again, some data might be recreated. " +
                            "In order to make the bot leave the guild and delete all data, use the *leave* command.", message);
                }
            }
        }
        return Postie.getInstance().getStandardsManager().getExamples(this, message);
    }
}
