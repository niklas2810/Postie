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

package me.niklas.postie.command.managment;


import me.niklas.postie.command.Command;
import me.niklas.postie.command.Result;
import me.niklas.postie.core.Postie;
import net.dv8tion.jda.core.entities.Message;

/**
 * Created by Niklas on 23.06.2018 in postie
 */
public class EnableCommand implements Command {
    @Override
    public String getName() {
        return "enable";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"activate"};
    }

    @Override
    public String getDescription() {
        return "Enables a commands, makes it usable for permitted users.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"enable vote", "enable dice"};
    }

    @Override
    public int getRequiredLevel() {
        return 3;
    }

    @Override
    public Result execute(Message message, String[] args) {
        if (args.length != 1) return Postie.getInstance().getStandardsManager().getExamples(this, message);

        if (!Postie.getInstance().getCommandManager().getCommands().stream().anyMatch(
                command -> command.getName().equalsIgnoreCase(args[0]))) {
            return new Result("Error", String.format("The command `%s` was not found!", args[0].toLowerCase()), message);
        }

        if (args[0].toLowerCase().equalsIgnoreCase("enable") || args[0].toLowerCase().equalsIgnoreCase("disable")) {
            return new Result(String.format("You can not disable `%s`!", args[0].toLowerCase()), message);
        }

        Postie.getInstance().getCommandManager().enableCommand(message.getGuild().getId(), args[0]);
        return new Result("Enabled", String.format("The command `%s` has been enabled.", args[0].toLowerCase()), message).deleteOrigin(true);
    }
}
