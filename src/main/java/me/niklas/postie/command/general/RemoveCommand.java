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
import me.niklas.postie.util.Util;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;

/**
 * Created by Niklas on 08.06.2018 in postie
 */
public class RemoveCommand implements Command {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rm", "clear"};
    }

    @Override
    public String getDescription() {
        return "Removes a message by ID / a specific amount of messages.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"remove 454714441901015040", "remove 10"};
    }

    @Override
    public int getRequiredLevel() {
        return 3;
    }

    @Override
    public Result execute(Message message, String[] args) {
        if (args.length != 1) return Postie.getInstance().getStandardsManager().getExamples(this, message);

        if (args[0].length() > 3) { //Supposed to be a message id
            Message mentioned = message.getChannel().getMessageById(args[0]).complete();
            if (mentioned == null) return new Result("Not found", "A message with that id was not found.", message);
            mentioned.delete().queue();
            message.addReaction("\u2705").queue();
            return new Result("", "", message);
        }

        //Amount of messages to be deleted
        if (!Util.isInteger(args[0])) {
            return new Result("Error", "That is not a valid number!", message);
        }
        int amount = Integer.valueOf(args[0]);
        MessageHistory history = message.getChannel().getHistory();
        message.getTextChannel().deleteMessages(history.retrievePast(amount).complete()).queue();
        return null;
    }
}
