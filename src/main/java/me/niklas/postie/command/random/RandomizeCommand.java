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

package me.niklas.postie.command.random;

import me.niklas.postie.command.Command;
import me.niklas.postie.command.Result;
import me.niklas.postie.core.Postie;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Niklas on 07.06.2018 in postie
 */
public class RandomizeCommand implements Command {
    @Override
    public String getName() {
        return "randomize";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"randlist", "rmz"};
    }

    @Override
    public String getDescription() {
        return "Randomizes a list";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"randomize 1,2,3", "randomize Pizza, Ice cream, Sushi"};
    }

    @Override
    public Result execute(Message message, String[] args) {
        String input = String.join(" ", args).trim();
        if (input.length() == 0 || !input.contains(",")) {
            return new Result("How to use", Postie.getInstance().getCommandManager().getCommandHelp(this), message);
        }

        List<String> elements = Arrays.asList(input.split(","));
        Collections.shuffle(elements);

        StringBuilder builder = new StringBuilder();
        builder.append("Elements: ").append(elements.size()).append("\n\n");
        for (int i = 1; i <= elements.size(); i++) {
            builder.append("**").append(i).append("**: ").append(elements.get(i - 1).trim()).append("\n");
        }
        return new Result("Randomized List", builder.toString().trim(), message);
    }
}
