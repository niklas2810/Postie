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
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

/**
 * Created by Niklas on 23.06.2018 in postie
 */
public class SetlevelCommand implements Command {

    @Override
    public String getName() {
        return "setlevel";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getDescription() {
        return "Sets the level of a guild member. Please use the defaultlevel command for all users.";
    }

    @Override
    public String[] getExamples() {
        return new String[]{"setlevel <mention> 2 (Can use voting commands)", "setlevel <mention> 3 (Can use administrative commands)"};
    }

    @Override
    public int getRequiredLevel() {
        return 3;
    }

    @Override
    public Result execute(Message message, String[] args) {
        if (message.getMentionedMembers().size() != 2)
            return Postie.getInstance().getStandardsManager().getExamples(this, message);
        if (!Util.isInteger(args[args.length - 1])) {
            if (args[args.length - 1].equalsIgnoreCase("reset")) {
                Postie.getInstance().getPermissionManager().removeUserLevel(message.getGuild().getId(), message.getAuthor().getId());
                return new Result("The permission level has been removed, default level will be applied.", message);
            }
            return Postie.getInstance().getStandardsManager().getExamples(this, message);
        }

        Member target = message.getMentionedMembers().get(1); //@Postie is the first one
        int targetLevel = Integer.valueOf(args[args.length - 1]);

        int selfLevel = Postie.getInstance().getPermissionManager().getLevelOfUser(message.getGuild().getId(), message.getAuthor().getId());
        int targetCurrent = Postie.getInstance().getPermissionManager().getLevelOfUser(message.getGuild().getId(), target.getUser().getId());

        if (selfLevel < targetCurrent) {
            return new Result("Error", "The user has a higher rank than you!", message);
        }
        if (targetLevel > selfLevel) {
            return new Result("Error", "You can not set the level higher than your own!", message);
        }
        if (targetLevel < 0) {
            return new Result("Error", "You can not set the level lower than 0!", message);
        }
        Postie.getInstance().getPermissionManager().save(message.getGuild().getId(), target.getUser().getId(), targetLevel);

        return new Result(String.format("The level of %s has been set to %s.", target.getEffectiveName(), targetLevel), message);
    }
}
