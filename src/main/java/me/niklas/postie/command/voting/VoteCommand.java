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

package me.niklas.postie.command.voting;

import me.niklas.postie.command.Command;
import me.niklas.postie.command.Result;
import me.niklas.postie.util.Reactions;
import me.niklas.postie.util.Titles;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Niklas on 06.06.2018 in postie
 */
public class VoteCommand implements Command {

    @Override
    public String getName() {
        return "vote";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Starts a voting about a question with yes/no answers OR starts a voting with multiple choices (up to 9 possible answers)";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"vote Do you like Discord?", "vote What do you like more? // Discord, TeamSpeak, Skype"};
    }

    @Override
    public Result execute(Message message, String[] args) {
        String input = String.join(" ", args).trim();

        if (input.contains("//") && input.split("//")[1].contains(",") && input.split("//")[1].length() > 2) { // min is a || a,b
            String title = input.split("//")[0].trim();
            String rawChoices = input.split("//")[1].trim();
            List<String> choices = Arrays.asList(rawChoices.split(","));

            if (title.length() == 0) {
                return new Result("No title", "Please enter a title!", message);
            }
            if (choices.size() > 9) {
                return new Result("Too many choices", "Sorry, but only up to 9 choices are supported.", message);
            }

            title += !title.endsWith("?") ? "?" : "";
            StringBuilder builder = new StringBuilder("Question: ").append(title).append("\n\n");

            for (int i = 1; i <= choices.size(); i++) {
                String choice = choices.get(i - 1).trim();
                builder.append(i).append(". ").append(choice).append("\n");
            }

            return new Result(Titles.MASS_VOTING, builder.toString().trim(), message)
                    .addFooterTag(choices.size() + "")
                    .reactWhenSent(Reactions.NUMBERS.subList(1, choices.size() + 1))
                    .deleteOrigin(true);
        }
        input += !input.endsWith("?") ? "?" : "";
        return new Result("Public Voting", "Question: " + input, message)
                .reactWhenSent(Reactions.YES_NO_REACTIONS)
                .deleteOrigin(true);
    }
}
