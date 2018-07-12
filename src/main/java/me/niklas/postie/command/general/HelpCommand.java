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

/**
 * Created by Niklas on 07.06.2018 in postie
 */
public class HelpCommand implements Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Returns a help site about the bots commands / about a specific command.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"help", "help dice"};
    }

    @Override
    public int getRequiredLevel() {
        return 1;
    }

    @Override
    public Result execute(Message message, String[] args) {
        if (args.length == 0) {
            //return new Result("General Help", getDefaultHelp(message.getJDA()), message);
            return getDefaultResultHelp(message);
        }
        if (args.length == 1) {
            if (!Postie.getInstance().getCommandManager().hasCommand(args[0])) {
                return Postie.getInstance().getStandardsManager().getCommandNotFoundResult(message);
            }
            return new Result("About " + args[0].toLowerCase(), Postie.getInstance().getCommandManager().getCommandHelp(args[0]), message);
        }
        return Postie.getInstance().getStandardsManager().getExamples(this, message);
    }

    private Result getDefaultResultHelp(Message message) {

        String builder = "\nUse `help <command name>` to get a more detailed command description.\n" +
                "A list of commands with detailed descriptions is available [online](https://github.com/Chromecube/Postie/wiki/Command-List).\n" +
                "If you don't know how to get started, refer to [this Wiki page](https://github.com/Chromecube/Postie/wiki/Quick-Start-Guide).\n" +
                "Always feel free to refer to the [Wiki](https://github.com/Chromecube/Postie/wiki) if you have any question or problem.\n\n" +
                "You can invite the bot using [this link](" + Postie.getInstance().getInviteLink(message.getJDA()) + ").\n";
        return new Result("General help", builder, message);
    }
}
