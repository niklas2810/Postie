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
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;

/**
 * Created by Niklas on 10.06.2018 in postie
 */
public class AnswerCommand implements Command {

    @Override
    public String getName() {
        return "answer";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Adds/Removes an answer.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"answer add Hello // Hello, how are you today?", "answer remove Hello"};
    }

    @Override
    public int getRequiredLevel() {
        return 2;
    }

    @Override
    public Result execute(Message message, String[] args) {
        if (args.length < 2 || (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove"))) {
            return Postie.getInstance().getStandardsManager().getExamples(this, message);
        }
        String action = args[0];
        String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();

        if (action.equalsIgnoreCase("remove")) { //answer remove
            if (input.contains("//")) { //Supposed to be for add, not for the remove sub-command
                return Postie.getInstance().getStandardsManager().getExamples(this, message);
            }
            input = input.replaceAll("[^A-Za-z0-9]", "").trim().toLowerCase();

            if (!Postie.getInstance().getAnswersManager().hasAnswerFor(message.getGuild().getId(), input)) {
                return new Result("Not found", "There was no answer found for `" + input + "`.", message);
            }

            Postie.getInstance().getAnswersManager().remove(message.getGuild().getId(), input);
            return new Result("Removed Answer", "Removed `" + input + "` from the database.", message);
        } else if (action.equalsIgnoreCase("add")) { //answer add
            if (!input.contains("//") || input.split("//").length != 2) { //answer add <input> // <answer>
                return Postie.getInstance().getStandardsManager().getExamples(this, message);
            }
            String answer = input.split("//")[1].trim();
            input = input.split("//")[0].trim();
            Postie.getInstance().getAnswersManager().save(message.getGuild().getId(), input, answer);
            return new Result("Saved Answer", "Saved the answer `" + answer + "` for the input `" + input + "`", message);
        }
        return Postie.getInstance().getStandardsManager().getExamples(this, message);
    }
}
