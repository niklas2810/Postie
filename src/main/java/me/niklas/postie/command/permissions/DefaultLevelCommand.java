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

package me.niklas.postie.command.permissions;

import me.niklas.postie.command.Command;
import me.niklas.postie.command.Result;
import me.niklas.postie.core.Postie;
import me.niklas.postie.util.Util;
import net.dv8tion.jda.core.entities.Message;

/**
 * Created by Niklas on 23.06.2018 in postie
 */
public class DefaultLevelCommand implements Command {
    @Override
    public String getName() {
        return "defaultlevel";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"dl"};
    }

    @Override
    public String getDescription() {
        return "Sets the default level for a guild";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"defaultlevel 1 (Standard)", "defaultlevel 2 (All voting commands)", "defaultlevel 3 (Administrative tasks)"};
    }

    @Override
    public int getRequiredLevel() {
        return 3;
    }

    @Override
    public Result execute(Message message, String[] args) {
        if (args.length != 1 || !Util.isInteger(args[0]))
            return Postie.getInstance().getStandardsManager().getExamples(this, message);

        int level = Integer.valueOf(args[0]);
        if (level < 0) return new Result("Error", "The number must be larger than 0!", message);
        if (level > Postie.getInstance().getPermissionManager().getLevelOfUser(message.getGuild().getId(), message.getAuthor().getId())) {
            return new Result("Error", "The default level can not be higher than your own!", message);
        }
        Postie.getInstance().getPermissionManager().save(message.getGuild().getId(), "default", level);
        return new Result(String.format("The default level has been set to %s.", level), message);
    }
}
